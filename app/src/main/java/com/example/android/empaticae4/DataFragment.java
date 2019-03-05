package com.example.android.empaticae4;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.EmpaticaDevice;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;

import jxl.write.WriteException;


/** This tab allows the user to see in real-time the value of the different datas collected by the Empatica wristband
 * (accelerometers, blood pulse, skin temperature, etc...). Two modes are available : the research mode will save all the
 * datas on the Firebase server, while the normal mode won't save anything.
 */

public class DataFragment extends Fragment implements EmpaDataDelegate, EmpaStatusDelegate {

    String TAG = "DATA_FRAGMENT";

    private ArrayList<DashModel> dashModelArrayList;
    private RecyclerView recyclerView;
    private DashAdapter dashAdapter;

    private float heartRate;

    private static int accel_counter;
    private static int temp_counter;
    private static int eda_counter;
    private static int bvp_counter;

    private ArrayList<FirebaseValue> tempArray = new ArrayList<>();
    private ArrayList<FirebaseValue> bvpArray = new ArrayList<>();
    private ArrayList<FirebaseValue> edaArray = new ArrayList<>();
    private ArrayList<FirebaseValue> hrArray = new ArrayList<>();

    private ArrayList<AccelValue> xaccelArray = new ArrayList<>();
    private ArrayList<AccelValue> yaccelArray = new ArrayList<>();
    private ArrayList<AccelValue> zaccelArray = new ArrayList<>();

    private TextView deviceNameLabel;
    private TextView statusLabel;

    private Button researchButton;
    private Button normalButton;

    private LinearLayout tagLayout;
    private TextView tagText;

    private Button stressButton;
    private Button workoutButton;
    private Button falseButton;

    private boolean researchMode = false;

    private Button disconnectButton;

    // Activity Recognition Client
    private ActivityRecognitionClient mActivityRecognitionClient;

    private WriteExcel writeExcel = new WriteExcel();

    // Empatica Implementation

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private static final String EMPATICA_API_KEY = "a3a1fd9dca144dbfae708d906891031b"; // TODO insert your API Key here
    private EmpaDeviceManager deviceManager = null;

    // Firebase Implementation

    String date = (DateFormat.format("dd-MM-yyyy kk:mm:ss", new java.util.Date()).toString());

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(date);

    private int indexFirebase;
    private float valueFirebase;
    private int xFirebase, yFirebase, zFirebase;

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_data, container, false);

        recyclerView = rootView.findViewById(R.id.rv1);
        deviceNameLabel = rootView.findViewById(R.id.deviceName);
        statusLabel = rootView.findViewById(R.id.status);

        disconnectButton = rootView.findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (deviceManager != null) {

                    // Writing the datas to the Firebase server after the experiment is done, but only if we are in researcher mode

                    if (researchMode == true) {
                        for (int i = 0; i < tempArray.size(); i++) {
                            indexFirebase = tempArray.get(i).getCount();
                            valueFirebase = tempArray.get(i).getValue();
                            myRef.child("Temperature").child(Integer.toString(indexFirebase)).setValue(valueFirebase);
                        }

                        for (int i = 0; i < hrArray.size(); i++) {
                            indexFirebase = hrArray.get(i).getCount();
                            valueFirebase = hrArray.get(i).getValue();
                            myRef.child("heartRate").child(Integer.toString(indexFirebase)).setValue(valueFirebase);
                        }

                        for (int i = 0; i < edaArray.size(); i++) {
                            indexFirebase = edaArray.get(i).getCount();
                            valueFirebase = edaArray.get(i).getValue();
                            myRef.child("EDA").child(Integer.toString(indexFirebase)).setValue(valueFirebase);
                        }

                        for (int i = 0; i < bvpArray.size(); i++) {
                            indexFirebase = bvpArray.get(i).getCount();
                            valueFirebase = bvpArray.get(i).getValue();
                            myRef.child("pulseWave").child(Integer.toString(indexFirebase)).setValue(valueFirebase);
                        }

                        for (int i = 0; i < xaccelArray.size(); i++) {
                            indexFirebase = xaccelArray.get(i).getCount();
                            xFirebase = xaccelArray.get(i).getValue();
                            yFirebase = yaccelArray.get(i).getValue();
                            zFirebase = zaccelArray.get(i).getValue();

                            myRef.child("accelerometer").child("x").child(Integer.toString(indexFirebase)).setValue(xFirebase);
                            myRef.child("accelerometer").child("y").child(Integer.toString(indexFirebase)).setValue(yFirebase);
                            myRef.child("accelerometer").child("z").child(Integer.toString(indexFirebase)).setValue(zFirebase);

                        }

                        myRef.child("duration").setValue(bvp_counter / 64);

                    }


                    deviceManager.disconnect();
                }
            }
        });

        researchButton = rootView.findViewById(R.id.researchButton);
        researchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (researchMode == false) {
                    researchButton.setBackgroundColor(getResources().getColor(R.color.buttonPressed));
                    normalButton.setBackgroundColor(getResources().getColor(R.color.buttonReleased));
                    researchMode = true;
                }
            }
        });

        normalButton = rootView.findViewById(R.id.normalButton);
        normalButton.setBackgroundColor(getResources().getColor(R.color.buttonPressed));
        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (researchMode == true) {
                    researchButton.setBackgroundColor(getResources().getColor(R.color.buttonReleased));
                    normalButton.setBackgroundColor(getResources().getColor(R.color.buttonPressed));
                    researchMode = false;
                }
            }
        });

        normalButton = rootView.findViewById(R.id.normalButton);

        /* The layout containing the buttons related to an event is invisible until the user triggers an event
         */

        tagLayout = rootView.findViewById(R.id.tagLayout);
        tagText = rootView.findViewById(R.id.tagText);

        stressButton = rootView.findViewById(R.id.stressButton);
        stressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagLayout.setVisibility(View.INVISIBLE);
                tagText.setVisibility(View.INVISIBLE);
            }
        });

        workoutButton = rootView.findViewById(R.id.workoutButton);
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagLayout.setVisibility(View.INVISIBLE);
                tagText.setVisibility(View.INVISIBLE);
            }
        });

        falseButton = rootView.findViewById(R.id.falseButton);
        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagLayout.setVisibility(View.INVISIBLE);
                tagText.setVisibility(View.INVISIBLE);
            }
        });


        // Defining the Recycler View where the user can see the real-time datas sent by the wristband

        dashModelArrayList = new ArrayList<>();

        String heads[] = {"Heart Rate", "Blood Volume Pulse", "Temperature", "Battery", "EDA",
                "x-Acceleration", "y-Acceleration", "z-Acceleration"};
        String subs[] = {"-", "-", "-", "-", "-", "-", "-", "-"};

        int images[] = {R.drawable.heart_rate, R.drawable.blood, R.drawable.temperature, R.drawable.battery,
                R.drawable.eda, R.drawable.xaxis, R.drawable.yaxis, R.drawable.zaxis};

        for (int count = 0; count < heads.length; count++) {
            DashModel dashModel = new DashModel(heads[count], subs[count], images[count]);
            dashModel.setHead(heads[count]);
            dashModel.setSub(subs[count]);
            dashModel.setImage(images[count]);

            dashModelArrayList.add(dashModel);
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        dashAdapter = new DashAdapter(dashModelArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(dashAdapter);

        initEmpaticaDeviceManager();

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    initEmpaticaDeviceManager();
                } else {
                    // Permission denied, boo!
                    final boolean needRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Permission required")
                            .setMessage("Without this permission bluetooth low energy devices cannot be found, allow it in order to connect to the device.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // try again
                                    if (needRationale) {
                                        // the "never ask again" flash is not set, try again with permission request
                                        initEmpaticaDeviceManager();
                                    } else {
                                        // the "never ask again" flag is set so the permission requests is disabled, try open app settings to enable the permission
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .setNegativeButton("Exit application", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // without permission exit is the only way
                                    getActivity().finish();
                                }
                            })
                            .show();
                }
                break;
        }
    }

    private void initEmpaticaDeviceManager() {
        // Android 6 (API level 23) now require ACCESS_COARSE_LOCATION permission to use BLE
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {

            if (TextUtils.isEmpty(EMPATICA_API_KEY)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Warning")
                        .setMessage("Please insert your API KEY")
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // without permission exit is the only way
                                getActivity().finish();
                            }
                        })
                        .show();
                return;
            }

            // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
            deviceManager = new EmpaDeviceManager(getActivity().getApplicationContext(), this, this);

            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (deviceManager != null) {
            deviceManager.stopScanning();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deviceManager != null) {
            deviceManager.cleanUp();
        }
    }

    @Override
    public void didDiscoverDevice(EmpaticaDevice bluetoothDevice, String deviceName, int rssi, boolean allowed) {
        // Check if the discovered device can be used with your API key. If allowed is always false,
        // the device is not linked with your API key. Please check your developer area at
        // https://www.empatica.com/connect/developer.php
        if (allowed) {
            // Stop scanning. The first allowed device will do.
            deviceManager.stopScanning();
            try {
                // Connect to the device
                deviceManager.connectDevice(bluetoothDevice);
                //updateLabel(deviceNameLabel, "To: " + deviceName);
            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
                Toast.makeText(getActivity(), "Sorry, you can't connect to this device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user chose not to enable Bluetooth
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            // You should deal with this
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void didUpdateSensorStatus(@EmpaSensorStatus int status, EmpaSensorType type) {

        didUpdateOnWristStatus(status);
    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {
        // Update the UI
        updateLabel(statusLabel, status.name());

        // The device manager is ready for use
        if (status == EmpaStatus.READY) {
            updateLabel(statusLabel, status.name() + " - Turn on your device");
            // Start scanning
            deviceManager.startScanning();
            // The device manager has established a connection

        } else if (status == EmpaStatus.CONNECTED) {

            // The device manager disconnected from a device
        } else if (status == EmpaStatus.DISCONNECTED) {

            updateLabel(deviceNameLabel, "");
        }
    }

    /* For each available data, we leave 4 seconds before starting to save the values, in order to give time to the wristband
    to get in a stable state
     */

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) { // sampling frequency : 32 Hz

        accel_counter++;

        if (accel_counter >= 128) {
            dashModelArrayList.get(5).setSub(Integer.toString(x));
            dashModelArrayList.get(6).setSub(Integer.toString(y));
            dashModelArrayList.get(7).setSub(Integer.toString(z));
            dashAdapter.notifyItemChanged(5);
            dashAdapter.notifyItemChanged(6);
            dashAdapter.notifyItemChanged(7);

            xaccelArray.add(new AccelValue(accel_counter, x));
            yaccelArray.add(new AccelValue(accel_counter, y));
            zaccelArray.add(new AccelValue(accel_counter, z));


        }
    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {  // sampling frequency : 64 Hz

        bvp_counter++;

        if (bvp_counter >= 256) { // 4 seconds before we start recording
            dashModelArrayList.get(1).setSub(Float.toString(bvp));
            dashAdapter.notifyItemChanged(1);

            bvpArray.add(new FirebaseValue(bvp_counter, bvp));

            // The data is sent to the other fragment to be displayed on the graph

            GraphFragment graphFragment = new GraphFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.second_frag, graphFragment).commit();

            graphFragment.addBVP((float) bvp_counter / 64, bvp);

        }

    }

    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {
        dashModelArrayList.get(3).setSub(Float.toString(battery));
        dashAdapter.notifyItemChanged(3);
    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) { // sampling frequency : 4 Hz

        eda_counter++;

        if (eda_counter >= 16) {

            dashModelArrayList.get(4).setSub(Float.toString(gsr));
            dashAdapter.notifyItemChanged(4);

            edaArray.add(new FirebaseValue(eda_counter, gsr));

            GraphFragment graphFragment = new GraphFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.second_frag, graphFragment).commit();

            graphFragment.addEDA((float) eda_counter / 4, gsr);

        }
    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {
        heartRate = (float) Math.ceil(60.0 / ibi);
        dashModelArrayList.get(0).setSub(Double.toString(heartRate));
        dashAdapter.notifyItemChanged(0);

        hrArray.add(new FirebaseValue(bvp_counter / 64, heartRate));

    }

    @Override
    public void didReceiveTemperature(float temp, double timestamp) { // sampling frequency : 4 Hz

        temp_counter++;

        if (temp_counter >= 16) {

            dashModelArrayList.get(2).setSub(Float.toString(temp));
            dashAdapter.notifyItemChanged(2);

            tempArray.add(new FirebaseValue(temp_counter, temp));

            GraphFragment graphFragment = new GraphFragment();

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.second_frag, graphFragment).commit();
            graphFragment.addTemp((float) temp_counter / 4, temp);

        }
    }

    // Update a label with some text, making sure this is run in the UI thread
    private void updateLabel(final TextView label, final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }

    @Override
    public void didReceiveTag(double timestamp) { // method triggered every time the user presses the button on the wristband

        tagEvent();

    }

    public void tagEvent() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tagLayout.setVisibility(View.VISIBLE);
                tagText.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void didEstablishConnection() {

    }

    @Override
    public void didUpdateOnWristStatus(@EmpaSensorStatus final int status) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (status == EmpaSensorStatus.ON_WRIST) {

                    ((TextView) getView().findViewById(R.id.wrist_status_label)).setText("ON WRIST");
                } else {

                    ((TextView) getView().findViewById(R.id.wrist_status_label)).setText("NOT ON WRIST");
                }
            }
        });
    }
}
