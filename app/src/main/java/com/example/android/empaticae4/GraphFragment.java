package com.example.android.empaticae4;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/* This tab allows the display in real-time the values of the blood pulse, skin temperature and electrodermal activity
   on a graph.
*/

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    String TAG = "GRAPH_FRAGMENT";

    private static final double MAX_PULSE = 200;
    private static final double MIN_PULSE = -200;
    private static final double MAX_TEMP = 100;
    private static final double MIN_TEMP = 10;
    private static final double MAX_EDA = 10;
    private static final double MIN_EDA = -10;

    private GraphView pulseGraph;
    private GraphView tempGraph;
    private GraphView edaGraph;

    private static ArrayList<Float> bvpCounterArray = new ArrayList<>();
    private static ArrayList<Float> bvpArray = new ArrayList<>();

    private static ArrayList<Float> tempCounterArray = new ArrayList<>();
    private static ArrayList<Float> tempArray = new ArrayList<>();

    private static ArrayList<Float> edaCounterArray = new ArrayList<>();
    private static ArrayList<Float> edaArray = new ArrayList<>();

    private float pulseIndex;
    private float pulseTemp;
    private double pulseMax = MIN_PULSE;
    private double pulseMin = MAX_PULSE;

    private float tempIndex;
    private float temperatureTemp;
    private double tempMax = MIN_TEMP;
    private double tempMin = MAX_TEMP;

    private float edaIndex;
    private float edaTemp;
    private double edaMax = MIN_EDA;
    private double edaMin = MAX_EDA;

    LineGraphSeries<DataPoint> pulseSeries;
    LineGraphSeries<DataPoint> tempSeries;
    LineGraphSeries<DataPoint> edaSeries;

    public GraphFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        pulseGraph = rootView.findViewById(R.id.pulse_graph);
        pulseSeries = new LineGraphSeries<>();

        tempGraph = rootView.findViewById(R.id.temp_graph);
        tempSeries = new LineGraphSeries<>();

        edaGraph = rootView.findViewById(R.id.eda_graph);
        edaSeries = new LineGraphSeries<>();


        if (bvpArray.size() != 0) {
            createPulseGraph();
        }

        if (tempArray.size() != 0) {
            createTempGraph();

        }

        if (edaArray.size() != 0) {
            createEdaGraph();
        }

        return rootView;
    }

    public void createPulseGraph() {

        float maxIndex = bvpCounterArray.get(bvpCounterArray.size() - 1);

        // Pulse graph

        for (int i = 0; i < bvpArray.size(); i ++) {

            if (bvpArray.size() > 2) {
                pulseGraph.setVisibility(View.VISIBLE);
            }

            pulseIndex = bvpCounterArray.get(i);
            pulseTemp = bvpArray.get(i);

            pulseSeries.appendData(new DataPoint(pulseIndex, pulseTemp), true, 1000);

            if (pulseTemp > pulseMax) {
                pulseMax = pulseTemp;
            }

            if (pulseTemp < pulseMin) {
                pulseMin = pulseTemp;
            }
        }

        Viewport pulseViewport = pulseGraph.getViewport();
        pulseViewport.setScrollableY(true);
        pulseViewport.setScrollable(true);
        pulseViewport.setScalableY(true);
        pulseViewport.setScalable(true);

        pulseViewport.setYAxisBoundsManual(true);
        pulseViewport.setMaxY(pulseMax);
        pulseViewport.setMinY(pulseMin);

        pulseViewport.setXAxisBoundsManual(true);
        pulseViewport.setMaxX(maxIndex);

        if (maxIndex > 10) {
            pulseViewport.setMinX(maxIndex - 10);
        } else {
            pulseViewport.setMinX(0);
        }

        GridLabelRenderer pulseLabel = pulseGraph.getGridLabelRenderer();
        pulseLabel.setNumVerticalLabels(5);


        pulseGraph.addSeries(pulseSeries);

    }

    public void addBVP(float bvp_counter, float bvp) {
        bvpCounterArray.add(bvp_counter);
        bvpArray.add(bvp);
    }

    public void createTempGraph() {
        // Temperature Graph

        float maxIndex = tempCounterArray.get(tempCounterArray.size() - 1);

        for (int i = 0; i < tempArray.size(); i ++) {

            if (tempArray.size() > 2) {
                tempGraph.setVisibility(View.VISIBLE);
            }

            tempIndex = tempCounterArray.get(i);
            temperatureTemp = tempArray.get(i);

            tempSeries.appendData(new DataPoint(tempIndex, temperatureTemp), true, 1000);

            if (temperatureTemp > tempMax) {
                tempMax = temperatureTemp;
            }

            if (temperatureTemp < tempMin) {
                tempMin = temperatureTemp;
            }
        }

        Viewport tempViewport = tempGraph.getViewport();
        tempViewport.setScrollableY(true);
        tempViewport.setScrollable(true);
        tempViewport.setScalableY(true);
        tempViewport.setScalable(true);

        tempViewport.setYAxisBoundsManual(true);
        tempViewport.setMaxY(tempMax);
        tempViewport.setMinY(tempMin);

        tempViewport.setXAxisBoundsManual(true);
        tempViewport.setMaxX(maxIndex);

        if (maxIndex > 10) {
            tempViewport.setMinX(maxIndex - 10);
        } else {
            tempViewport.setMinX(0);
        }

        GridLabelRenderer tempLabel = tempGraph.getGridLabelRenderer();
        tempLabel.setNumVerticalLabels(5);

        tempGraph.addSeries(tempSeries);
    }

    public void addTemp(float temp_counter, float temp) {
        tempCounterArray.add(temp_counter);
        tempArray.add(temp);
    }

    public void createEdaGraph() {
        // EDA Graph

        float maxIndex = edaCounterArray.get(edaCounterArray.size() - 1);

        for (int i = 0; i < edaArray.size(); i ++) {

            if (edaArray.size() > 2) {
                edaGraph.setVisibility(View.VISIBLE);
            }

            edaIndex = edaCounterArray.get(i);
            edaTemp = edaArray.get(i);

            edaSeries.appendData(new DataPoint(edaIndex, edaTemp), true, 1000);

            if (edaTemp > edaMax) {
                edaMax = edaTemp;
            }

            if (edaTemp < edaMin) {
                edaMin = edaTemp;
            }
        }

        Viewport edaViewport = edaGraph.getViewport();
        edaViewport.setScrollableY(true);
        edaViewport.setScrollable(true);
        edaViewport.setScalableY(true);
        edaViewport.setScalable(true);

        edaViewport.setYAxisBoundsManual(true);
        edaViewport.setMaxY(edaMax);
        edaViewport.setMinY(edaMin);

        edaViewport.setXAxisBoundsManual(true);
        edaViewport.setMaxX(maxIndex);

        if (maxIndex > 10) {
            edaViewport.setMinX(maxIndex - 10);
        } else {
            edaViewport.setMinX(0);
        }

        edaGraph.addSeries(edaSeries);
    }

    public void addEDA(float eda_counter, float eda) {
        edaCounterArray.add(eda_counter);
        edaArray.add(eda);
    }

}