package com.btl.login.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.btl.login.MainActivity;
import com.btl.login.R;
import com.btl.login.adapter.TeachingSubjectsAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.SemesterDTO;
import com.btl.login.dto.SubjectsTaughtByTeacherDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TeachingSubjectsFragment extends Fragment {

    EditText eTxtSearchSubjectName;
    Button btnSearch;
    String searchSubjectName = "";
    List<SubjectsTaughtByTeacherDTO> listTeachingSubjects;
    Spinner spinnerSemesters;
    AppDatabase appDatabase;
    GridView teachingSubjects;
    String title = "";
    String chosenSemesterName = "";
    int semesterId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teaching_subjects, container, false);

        eTxtSearchSubjectName = view.findViewById(R.id.eTxtSearchingSubjectName);
        btnSearch = view.findViewById(R.id.btnSearch);
        spinnerSemesters = view.findViewById(R.id.spinnerSemester);
        teachingSubjects = view.findViewById(R.id.grid_teaching_subjects);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appDatabase = AppDatabase.getDatabase(getContext());
        List<String> semesters = new ArrayList<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            List<SemesterDTO> semesterList = appDatabase.semesterDao().getSemesterNameAndAcademicYearName(System.currentTimeMillis());
            semesterId = appDatabase.semesterDao().getSemesterIdAtCurrentTime(System.currentTimeMillis());
            handler.post(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    semesters.addAll(semesterList.stream().map(SemesterDTO::getFullSemesterName).toList());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSemesters.setAdapter(adapter);

                spinnerSemesters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        semesterId = semesterList.get(position).getSemesterId();
                        chosenSemesterName = semesters.get(position);
                        executorService.execute(() -> {
                            handleSearch(view, handler);
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            });
        });

        executorService.execute(() -> {
            handleSearch(view, handler);
        });

        btnSearch.setOnClickListener(v -> {
            searchSubjectName = eTxtSearchSubjectName.getText().toString();
            executorService.execute(() -> {
                handleSearch(view, handler);
            });
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(TeachingSubjectsFragment.this).attach(TeachingSubjectsFragment.this).commit();
        });
    }

    private void handleSearch(@NonNull View view, Handler handler) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int userId = appDatabase.teacherDao().getTeacherByEmail(user.getEmail()).getId();
        listTeachingSubjects = appDatabase.subjectDao().getSubjectsTaughtByTeacherLogin(semesterId, userId, searchSubjectName);
        handler.post(() -> {
            TeachingSubjectsAdapter teachingSubjectsAdapter = new TeachingSubjectsAdapter(getContext(), listTeachingSubjects);
            teachingSubjects.setAdapter(teachingSubjectsAdapter);
            teachingSubjects.setOnItemClickListener((parent, view1, position, id) -> {
                title = listTeachingSubjects.get(position).getSubjectName() + " " + chosenSemesterName;
                TeachingClassesFragment teachingClassesFragment = TeachingClassesFragment.newInstance(userId, listTeachingSubjects.get(position).getId(), title);
                redirectToSpecifiedFragment(teachingClassesFragment, true);
            });
        });
    }

    private void redirectToSpecifiedFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
        ((MainActivity) getActivity()).checkBackStack();
    }
}