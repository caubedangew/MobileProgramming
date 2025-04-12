package com.btl.login.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.btl.login.MainActivity;
import com.btl.login.R;
import com.btl.login.adapter.TeachingSubjectsAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.SubjectsTaughtByTeacherDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    String title = "";

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
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
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int userId = appDatabase.teacherDao().getTeacherByEmail(user.getEmail()).getId();
        listTeachingSubjects = appDatabase.subjectDao().getSubjectsTaughtByTeacherLogin(userId, searchSubjectName);
        handler.post(() -> {
            TeachingSubjectsAdapter teachingSubjectsAdapter = new TeachingSubjectsAdapter(getContext(), listTeachingSubjects);
            GridView teachingSubjects = view.findViewById(R.id.grid_teaching_subjects);
            teachingSubjects.setAdapter(teachingSubjectsAdapter);
            teachingSubjects.setOnItemClickListener((parent, view1, position, id) -> {
                title = listTeachingSubjects.get(position).getSubjectName() + " học kỳ II năm học 2024 - 2025";
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