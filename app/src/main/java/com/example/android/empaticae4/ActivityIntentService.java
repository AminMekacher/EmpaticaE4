package com.example.android.empaticae4;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.android.empaticae4.MainActivity;
import com.example.android.empaticae4.R;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by aminmekacher on 02.08.18.
 */

public class ActivityIntentService extends IntentService {

    protected static final String TAG = "Activity";

    public ActivityIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // Called method whenever an activity detection update is available
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (ActivityRecognitionResult.hasResult(intent)) {

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            // Confidence percentage
            int confidence = mostProbableActivity.getConfidence();

            // Activity type
            int activityType = mostProbableActivity.getType();
        }

    }

    static String getActivityString(Context context, int detectedActivityType) {

        Resources resources = context.getResources();
        switch (detectedActivityType) {

            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.vehicle);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unknown_activity);
        }
    }

    static final int[] POSSIBLE_ACTIVITES = {

            DetectedActivity.IN_VEHICLE,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.ON_FOOT,
            DetectedActivity.RUNNING,
            DetectedActivity.STILL,
            DetectedActivity.WALKING,
            DetectedActivity.UNKNOWN,
    };

    static String detectedActivitiesToJson(ArrayList<DetectedActivity> detectedActivitiesList) {

        Type type = new TypeToken<ArrayList<DetectedActivity>>() {}.getType();
        return new Gson().toJson(detectedActivitiesList, type);
    }

    static ArrayList<DetectedActivity> detectedActivitiesFromJson(String jsonArray) {
        Type listType = new TypeToken<ArrayList<DetectedActivity>>(){}.getType();
        ArrayList<DetectedActivity> detectedActivities = new Gson().fromJson(jsonArray, listType);

        if (detectedActivities == null) {
            detectedActivities = new ArrayList<>();
        }

        return detectedActivities;
    }
}
