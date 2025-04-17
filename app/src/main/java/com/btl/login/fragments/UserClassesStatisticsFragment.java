package com.btl.login.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.btl.login.R;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.StatisticsScoreDTO;
import com.btl.login.dto.StatisticsStudentNumberDTO;
import com.btl.login.dto.StatisticsSubjectDTO;
import com.btl.login.entities.Subject;
import com.btl.login.entities.Teacher;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserClassesStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserClassesStatisticsFragment extends Fragment {
    private static final String SUBJECT_ID = "subjectId";
    private static final String SEMESTER_ID = "semesterId";

    // TODO: Rename and change types of parameters
    private int subjectId;
    private int semesterId;
    BarChart classesStatisticsChart;
    TextView txtClassesSubjectsTitle;
    private List<String> labels;
    private List<Float> values = new ArrayList<>();
    private FirebaseUser currentUser;
    private ExecutorService executorService;
    private Handler handler;
    private AppDatabase appDatabase;

    public UserClassesStatisticsFragment() {

    }

    public static UserClassesStatisticsFragment newInstance(int subjectId, int semesterId) {
        UserClassesStatisticsFragment fragment = new UserClassesStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt(SUBJECT_ID, subjectId);
        args.putInt(SEMESTER_ID, semesterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            semesterId = getArguments().getInt(SEMESTER_ID);
            subjectId = getArguments().getInt(SUBJECT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_classes_statistics, container, false);

        classesStatisticsChart = view.findViewById(R.id.classesStatisticsChart);
        txtClassesSubjectsTitle = view.findViewById(R.id.txtClassesStatisticsTitle);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        appDatabase = AppDatabase.getDatabase(requireContext());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        createChart(view);
    }

    private void createChart(@NonNull View view) {
        executorService.execute(() -> {
            Teacher teacher = appDatabase.teacherDao().getTeacherByEmail(currentUser.getEmail());
            List<String> statisticsResult = appDatabase.statisticsDao().getStudentClassNameAndAverageScore(subjectId, semesterId, teacher.getId());
            List<StatisticsScoreDTO> score = appDatabase.statisticsDao().getAverageScoreOfOneStudent(semesterId, teacher.getId());
            List<StatisticsStudentNumberDTO> number = appDatabase.statisticsDao().numberStudentInClass(semesterId, teacher.getId());
            handler.post(() -> {
                Log.d("RESULT", String.valueOf(statisticsResult.size()));
                if (statisticsResult != null && !statisticsResult.isEmpty()) {
                    labels = statisticsResult;
//                    for (int i = 0; i < statisticsResult.size(); i++) {
//                        values.add(0f);
//                        for (int j = 0; j < score.size(); j++)
//                            if (statisticsResult.get(i).getSubjectId() == score.get(j).getSubjectId())
//                                for (int k = 0; k < number.size(); k++) {
//                                    if (score.get(j).getSubjectId() == number.get(k).getSubjectId())
//                                        values.set(i, (float) (score.get(j).getAvarageScore() / number.get(k).getStudentNumber()));
//                                }
//                    }
                    labels.add(0, "");

                    BarDataSet barDataSet = new BarDataSet(getBarEntries(), "Điểm trung bình");
                    barDataSet.setColor(0xFFBB86FC);
                    barDataSet.setValueTextSize(15f);

                    // Barchart configurations
                    BarData barData = new BarData(barDataSet);
                    classesStatisticsChart.setData(barData);
                    classesStatisticsChart.animateY(1000);
                    classesStatisticsChart.getDescription().setEnabled(true);
                    classesStatisticsChart.setDragEnabled(true);
                    classesStatisticsChart.setVisibleXRangeMaximum(3);
                    classesStatisticsChart.moveViewToX(0);
                    classesStatisticsChart.setExtraBottomOffset(50f);

                    Legend legend = classesStatisticsChart.getLegend();
                    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                    legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                    legend.setDrawInside(true);
                    legend.setTextSize(16f);

                    // Bar width configurations
                    barData.setBarWidth(0.15f);
                    // X-Axis Data
                    XAxis xAxis = classesStatisticsChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setTextSize(15f);
                    Log.d("Labels", classesStatisticsChart.getXAxis().getFormattedLabel(1));

                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setCenterAxisLabels(false);
                    xAxis.setAvoidFirstLastClipping(false);
                    xAxis.setDrawGridLines(false);
                    xAxis.setLabelRotationAngle(15f);


                    // Y-Axis Data
                    YAxis leftAxis = classesStatisticsChart.getAxisLeft();
                    leftAxis.setTextColor(Color.BLACK);
                    leftAxis.setTextSize(15f);
                    leftAxis.setDrawGridLines(false);

                    YAxis rightAxis = classesStatisticsChart.getAxisRight();

                    // Disable right Y-axis
                    rightAxis.setEnabled(false);

                    classesStatisticsChart.getXAxis().setAxisMinimum(0);
                    classesStatisticsChart.animate();

                    // Invalidate the chart to refresh
                    classesStatisticsChart.invalidate();

                    classesStatisticsChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            executorService.execute(() -> {
                                // Subject subject = appDatabase.subjectDao().getSubjectById(statisticsResult.get(Math.round(e.getX())).getSubjectId());
                                // UserClassesStatisticsFragment fragment = UserClassesStatisticsFragment.newInstance(subject.getId(), semesterId);
//                                FragmentManager fragmentManager = getFragmentManager();
//                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                fragmentTransaction.addToBackStack(null);
//                                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                                fragmentTransaction.commit();
                            });
                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });
                } else {
                    classesStatisticsChart.clear();
                    classesStatisticsChart.setNoDataText("Bạn không được phân công giảng dạy môn học nào ở học kỳ này");
                    classesStatisticsChart.setNoDataTextColor(Color.RED);
                    classesStatisticsChart.invalidate();
                }
            });
        });
    }

    private ArrayList<BarEntry> getBarEntries() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            barEntries.add(new BarEntry(i + 1, values.get(i)));
        }

        return barEntries;
    }
}