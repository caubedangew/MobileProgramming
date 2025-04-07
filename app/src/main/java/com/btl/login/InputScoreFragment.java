package com.btl.login;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.btl.login.adapter.InputScoreAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.StudentInClassDTO;
import com.btl.login.dto.StudentScoreDTO;
import com.btl.login.entities.StudentScore;
import com.btl.login.entities.SubjectScore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InputScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputScoreFragment extends Fragment {
    private static final String SUBJECT_ID_PARAM = "subjectId";
    private static final String OPEN_CLASS_PARAM = "openClassId";

    private int subjectId, openClassId, studentId;
    ;

    EditText eTxtProgressScore, eTxtMiddleScore, eTxtFinalScore;
    TextView txtProgressDescription, txtMiddleDescription, txtFinalDescription,
            txtProgressWeight, txtMiddleWeight, txtFinalWeight;
    Button btnInputScore;
    private List<SubjectScore> subjectScore;
    boolean haveProgressScore, haveMiddleScore, haveFinalScore;

    public InputScoreFragment() {

    }

    public static InputScoreFragment newInstance(int openClassId, int subjectScoreId) {
        InputScoreFragment fragment = new InputScoreFragment();
        Bundle args = new Bundle();
        args.putInt(OPEN_CLASS_PARAM, openClassId);
        args.putInt(SUBJECT_ID_PARAM, subjectScoreId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            openClassId = getArguments().getInt(OPEN_CLASS_PARAM);
            subjectId = getArguments().getInt(SUBJECT_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_score, container, false);

        txtProgressDescription = view.findViewById(R.id.txtProgressDescription);
        txtMiddleDescription = view.findViewById(R.id.txtMiddleSemesterDescription);
        txtFinalDescription = view.findViewById(R.id.txtFinalSemesterDescription);
        txtProgressWeight = view.findViewById(R.id.txtProgressPercentage);
        txtMiddleWeight = view.findViewById(R.id.txtMiddleSemesterPercentage);
        txtFinalWeight = view.findViewById(R.id.txtFinalSemesterPercentage);
        eTxtProgressScore = view.findViewById(R.id.eTxtProgressScore);
        eTxtMiddleScore = view.findViewById(R.id.eTxtMiddleSemesterScore);
        eTxtFinalScore = view.findViewById(R.id.eTxtFinalSemesterScore);
        btnInputScore = view.findViewById(R.id.btnSave);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            subjectScore = appDatabase.subjectScoreDao().getSubjectScoreBySubjectId(subjectId);
            List<StudentInClassDTO> listStudents = appDatabase.studentScoreDao().getStudentsInOpenClass(openClassId);
            handler.post(() -> {
                if (!subjectScore.isEmpty()) {
                    txtProgressDescription.setText(subjectScore.get(0).getDescription());
                    txtProgressWeight.setText((int) (subjectScore.get(0).getWeight() * 100) + "%");
                    txtMiddleDescription.setText(subjectScore.get(1).getDescription());
                    txtMiddleWeight.setText((int) (subjectScore.get(1).getWeight() * 100) + "%");
                    txtFinalDescription.setText(subjectScore.get(2).getDescription());
                    txtFinalWeight.setText((int) (subjectScore.get(2).getWeight() * 100) + "%");
                }
                InputScoreAdapter inputScoreAdapter = new InputScoreAdapter(getContext(), listStudents);
                Spinner spinner_list_students = view.findViewById(R.id.spinner_list_students);
                spinner_list_students.setAdapter(inputScoreAdapter);
                spinner_list_students.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        haveProgressScore = false;
                        haveMiddleScore = false;
                        haveFinalScore = false;
                        eTxtProgressScore.setText("");
                        eTxtMiddleScore.setText("");
                        eTxtFinalScore.setText("");
                        studentId = listStudents.get(position).getId();
                        executorService.execute(() -> {
                            List<StudentScoreDTO> studentScores = appDatabase.studentScoreDao()
                                    .getStudentScoreAndSubjectScoreByStudentId(studentId, openClassId);
                            handler.post(() -> {
                                if (!studentScores.isEmpty()) {
                                    for (int i = 0; i < studentScores.size(); i++) {
                                        if (studentScores.get(i).getSubjectScoreId() == subjectScore.get(0).getId()) {
                                            haveProgressScore = true;
                                            eTxtProgressScore.setText(String.format("%.2f", studentScores.get(i).getScore()));
                                        } else if (studentScores.get(i).getSubjectScoreId() == subjectScore.get(1).getId()) {
                                            haveMiddleScore = true;
                                            eTxtMiddleScore.setText(String.format("%.2f", studentScores.get(i).getScore()));
                                        } else if (studentScores.get(i).getSubjectScoreId() == subjectScore.get(2).getId()) {
                                            haveFinalScore = true;
                                            eTxtFinalScore.setText(String.format("%.2f", studentScores.get(i).getScore()));
                                        }
                                    }
                                }
                            });
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            });
        });
        btnInputScore.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setMessage("Bạn đã chắc chắn với quyết định của mình chứ!")
                    .setPositiveButton("Chắc chắn", (dialog, which) -> {
                        String progressScore = eTxtProgressScore.getText().toString(),
                                middleScore = eTxtMiddleScore.getText().toString(),
                                finalScore = eTxtFinalScore.getText().toString();
                        ArrayList<StudentScore> studentScores = new ArrayList<>();
                        if (!progressScore.isEmpty())
                            if (haveProgressScore) {
                                executorService.execute(() -> {
                                    StudentScore progressStudentScore = appDatabase.studentScoreDao().getStudentScoreBySubjectScoreAndStudent(subjectScore.get(0).getId(), studentId);
                                    handler.post(() -> {
                                        progressStudentScore.setScore(Double.valueOf(progressScore));
                                        executorService.execute(() -> appDatabase.studentScoreDao().updateStudentScore(progressStudentScore));
                                    });
                                });
                            } else
                                studentScores.add(new StudentScore(Double.valueOf(progressScore), openClassId, studentId, subjectScore.get(0).getId()));
                        if (!middleScore.isEmpty())
                            if (haveMiddleScore) {
                                executorService.execute(() -> {
                                    StudentScore middleStudentScore = appDatabase.studentScoreDao().getStudentScoreBySubjectScoreAndStudent(subjectScore.get(1).getId(), studentId);
                                    handler.post(() -> {
                                        middleStudentScore.setScore(Double.valueOf(middleScore));
                                        executorService.execute(() -> appDatabase.studentScoreDao().updateStudentScore(middleStudentScore));
                                    });
                                });
                            } else
                                studentScores.add(new StudentScore(Double.valueOf(middleScore), openClassId, studentId, subjectScore.get(1).getId()));
                        if (!finalScore.isEmpty())
                            if (haveFinalScore) {
                                executorService.execute(() -> {
                                    StudentScore finalStudentScore = appDatabase.studentScoreDao().getStudentScoreBySubjectScoreAndStudent(subjectScore.get(2).getId(), studentId);
                                    handler.post(() -> {
                                        finalStudentScore.setScore(Double.valueOf(finalScore));
                                        executorService.execute(() -> appDatabase.studentScoreDao().updateStudentScore(finalStudentScore));
                                    });
                                });
                            } else
                                studentScores.add(new StudentScore(Double.valueOf(finalScore), openClassId, studentId, subjectScore.get(2).getId()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            executorService.execute(() -> appDatabase.studentScoreDao().addStudentScores(studentScores.toArray(StudentScore[]::new)));
                            Toast.makeText(getContext(), "Lưu điểm thành công", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Để tôi kiểm tra lại", null)
                    .show();
        });
    }
}