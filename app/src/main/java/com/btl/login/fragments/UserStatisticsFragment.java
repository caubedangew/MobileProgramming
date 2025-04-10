package com.btl.login.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.btl.login.R;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.StatisticsDTO;
import com.btl.login.dto.TestDTO;
import com.btl.login.entities.Semester;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserStatisticsFragment extends Fragment {

    Spinner spinnerSemester;
    AppDatabase appDatabase;
    ExecutorService executorService;
    Handler handler;

    int semesterId;

    public UserStatisticsFragment() {
        // Required empty public constructor
    }

    public static UserStatisticsFragment newInstance(String param1, String param2) {
        UserStatisticsFragment fragment = new UserStatisticsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_statistics, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerSemester = view.findViewById(R.id.spinnerSemester);

        appDatabase = AppDatabase.getDatabase(getContext());

        List<String> semesters = new ArrayList<>();
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            List<String> semesterList = appDatabase.semesterDao().getSemesterNameAndAcademicYearName();
            handler.post(() -> {
                semesters.addAll(semesterList);
            });
        });

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, semesters);
        spinnerSemester.setAdapter(adapter);
        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int semesterId = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        executorService.execute(() -> {
            List<Double> test = appDatabase.statisticsDao().getAverageScoreOfOneStudent(11, 31);
            int number = appDatabase.statisticsDao().numberStudentInClass(31);
            int numberClass = appDatabase.statisticsDao().numberClassOfSubjectOnTheSemester(31);
            handler.post(() -> {
                Log.d("TEST", String.valueOf(test.size()));
                Log.d("STUDENT", String.valueOf(number));
                Log.d("CLASS", String.valueOf(numberClass));
            });
        });

        BarDataSet barDataSet = new BarDataSet(getBarEntries(), "Điểm trung bình");
        barDataSet.setColor(Color.BLUE);
        barDataSet.setValueTextSize(15f);


        // Barchart configurations
        BarChart statisticsChart = view.findViewById(R.id.statisticsChart);
        Legend legend = statisticsChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(true);
        legend.setTextSize(16f);
        BarData barData = new BarData(barDataSet);
        statisticsChart.setData(barData);
        statisticsChart.animateY(1000);
        statisticsChart.getDescription().setEnabled(true);
        statisticsChart.setDragEnabled(true);
        statisticsChart.setVisibleXRangeMaximum(3);
        statisticsChart.moveViewToX(0);
        statisticsChart.setExtraBottomOffset(50f);

        // Bar width configurations
        barData.setBarWidth(0.15f);
        // X-Axis Data
        XAxis xAxis = statisticsChart.getXAxis();
        executorService.execute(() -> {
            List<String> statisticsResult = appDatabase.statisticsDao().getStatisticsBySemester(11, 31);
            handler.post(() -> {
                Log.d("SIZE", String.valueOf(statisticsResult.size()));
                List<String> labels = new ArrayList<>(statisticsResult);
                labels.add(0, "");
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            });
        });
        xAxis.setTextSize(15f);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Cực kỳ quan trọng để các label không bị skip
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(15f);


        // Y-Axis Data
        YAxis leftAxis = statisticsChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(15f);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = statisticsChart.getAxisRight();

        // Disable right Y-axis
        rightAxis.setEnabled(false);

        statisticsChart.getXAxis().setAxisMinimum(0);
        statisticsChart.animate();

        // Invalidate the chart to refresh
        statisticsChart.invalidate();
    }

    private ArrayList<BarEntry> getBarEntries() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1f, 8));
        barEntries.add(new BarEntry(2f, 10));
        barEntries.add(new BarEntry(3f, 6));
        barEntries.add(new BarEntry(4f, 9));

        return barEntries;
    }
}