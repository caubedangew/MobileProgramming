package com.btl.login;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserStatisticsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserStatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserStatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserStatisticsFragment newInstance(String param1, String param2) {
        UserStatisticsFragment fragment = new UserStatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_statistics, container, false);;

        BarDataSet barDataSet = new BarDataSet(getBarEntries(), "Data");

        barDataSet.setColor(Color.BLUE);

        barDataSet.setValueTextSize(11f);

        // Barchart configurations
        BarChart statisticsChart = view.findViewById(R.id.statisticsChart);
        BarData barData = new BarData(barDataSet);
        statisticsChart.setData(barData);
        statisticsChart.animateY(1000);
        statisticsChart.getDescription().setEnabled(true);
        statisticsChart.setDragEnabled(true);
        statisticsChart.setVisibleXRangeMaximum(10);

        // Bar width configurations
        barData.setBarWidth(0.15f);

        // X-Axis Data
        XAxis xAxis = statisticsChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        // Enabled grid lines for X-axis
        xAxis.setDrawGridLines(true);

        // Set grid line color
        xAxis.setGridColor(Color.LTGRAY);

        // Set grid line width
        xAxis.setGridLineWidth(1f);

        // Y-Axis Data
        YAxis leftAxis = statisticsChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setGridLineWidth(1f);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = statisticsChart.getAxisRight();

        // Disable right Y-axis
        rightAxis.setEnabled(false);

        statisticsChart.getXAxis().setAxisMinimum(0);
        statisticsChart.animate();

        // Invalidate the chart to refresh
        statisticsChart.invalidate();

        return view;
    }

    private ArrayList<BarEntry> getBarEntries() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1f, 3));
        barEntries.add(new BarEntry(2f, 12));
        barEntries.add(new BarEntry(3f, 6));
        barEntries.add(new BarEntry(4f, 9));
        barEntries.add(new BarEntry(5f, 2));
        barEntries.add(new BarEntry(6f, 5));
        barEntries.add(new BarEntry(7f, 7));

        return barEntries;
    }
}