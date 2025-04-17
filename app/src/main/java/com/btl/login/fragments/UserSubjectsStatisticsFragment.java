    package com.btl.login.fragments;

    import android.annotation.SuppressLint;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Spinner;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;

    import com.btl.login.R;
    import com.btl.login.configurations.AppDatabase;
    import com.btl.login.dto.SemesterDTO;
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
     * Use the {@link UserSubjectsStatisticsFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    public class UserSubjectsStatisticsFragment extends Fragment {

        Spinner spinnerSemester;
        AppDatabase appDatabase;
        ExecutorService executorService;
        Handler handler;
        List<String> labels = new ArrayList<>();
        List<Float> values = new ArrayList<>();
        int semesterId;
        private FirebaseUser currentUser;
        BarChart statisticsChart;

        public UserSubjectsStatisticsFragment() {
            // Required empty public constructor
        }

        public static UserSubjectsStatisticsFragment newInstance(String param1, String param2) {
            UserSubjectsStatisticsFragment fragment = new UserSubjectsStatisticsFragment();
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

        @SuppressLint("NewApi")
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            spinnerSemester = view.findViewById(R.id.spinnerSemester);

            appDatabase = AppDatabase.getDatabase(getContext());

            List<String> semesters = new ArrayList<>();
            executorService = Executors.newSingleThreadExecutor();
            handler = new Handler(Looper.getMainLooper());

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            statisticsChart = view.findViewById(R.id.statisticsChart);

            executorService.execute(() -> {
                List<SemesterDTO> semesterList = appDatabase.semesterDao().getSemesterNameAndAcademicYearName(System.currentTimeMillis());
                semesterId = appDatabase.semesterDao().getSemesterIdAtCurrentTime(System.currentTimeMillis());
                handler.post(() -> {
                    semesters.addAll(semesterList.stream().map(SemesterDTO::getFullSemesterName).toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSemester.setAdapter(adapter);

                    spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            values.clear();
                            labels.clear();
                            semesterId = semesterList.get(position).getSemesterId();
                            createChart(view);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                });
            });
        }

        private void createChart(@NonNull View view) {
            executorService.execute(() -> {
                Teacher teacher = appDatabase.teacherDao().getTeacherByEmail(currentUser.getEmail());
                List<StatisticsSubjectDTO> statisticsResult = appDatabase.statisticsDao().getStatisticsBySemester(semesterId, teacher.getId());
                List<StatisticsScoreDTO> score = appDatabase.statisticsDao().getAverageScoreOfOneStudent(semesterId, teacher.getId());
                List<StatisticsStudentNumberDTO> number = appDatabase.statisticsDao().numberStudentInClass(semesterId, teacher.getId());
                handler.post(() -> {
                    if (statisticsResult != null && !statisticsResult.isEmpty()) {
                        labels = statisticsResult.stream().map(StatisticsSubjectDTO::getSubjectName).collect(Collectors.toList());
                        for (int i = 0; i < statisticsResult.size(); i++) {
                            values.add(0f);
                            for (int j = 0; j < score.size(); j++)
                                if (statisticsResult.get(i).getSubjectId() == score.get(j).getSubjectId())
                                    for (int k = 0; k < number.size(); k++) {
                                        if (score.get(j).getSubjectId() == number.get(k).getSubjectId())
                                            values.set(i, (float) (score.get(j).getAvarageScore() / number.get(k).getStudentNumber()));
                                    }
                        }
                        labels.add(0, "");

                        Log.d("Values", String.valueOf(values.size()));

                        BarDataSet barDataSet = new BarDataSet(getBarEntries(), "Điểm trung bình");
                        barDataSet.setColor(0xFFBB86FC);
                        barDataSet.setValueTextSize(15f);

                        // Barchart configurations
                        BarData barData = new BarData(barDataSet);
                        statisticsChart.setData(barData);
                        statisticsChart.animateY(1000);
                        statisticsChart.getDescription().setEnabled(true);
                        statisticsChart.setDragEnabled(true);
                        statisticsChart.setVisibleXRangeMaximum(3);
                        statisticsChart.moveViewToX(0);
                        statisticsChart.setExtraBottomOffset(50f);

                        Legend legend = statisticsChart.getLegend();
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                        legend.setDrawInside(true);
                        legend.setTextSize(16f);

                        // Bar width configurations
                        barData.setBarWidth(0.15f);
                        // X-Axis Data
                        XAxis xAxis = statisticsChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                        xAxis.setTextSize(15f);
                        Log.d("Labels", statisticsChart.getXAxis().getFormattedLabel(1));

                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);
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

                        statisticsChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                            @Override
                            public void onValueSelected(Entry e, Highlight h) {
                                executorService.execute(() -> {
                                    Subject subject = appDatabase.subjectDao().getSubjectById(statisticsResult.get(Math.round(e.getX())).getSubjectId());
                                    UserClassesStatisticsFragment fragment = UserClassesStatisticsFragment.newInstance(subject.getId(), semesterId);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                                    fragmentTransaction.commit();
                                });
                            }

                            @Override
                            public void onNothingSelected() {

                            }
                        });
                    } else {
                        statisticsChart.clear();
                        statisticsChart.setNoDataText("Bạn không được phân công giảng dạy môn học nào ở học kỳ này");
                        statisticsChart.setNoDataTextColor(Color.RED);
                        statisticsChart.invalidate();
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