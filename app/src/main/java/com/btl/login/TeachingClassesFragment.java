package com.btl.login;

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
import android.widget.ListView;

import com.btl.login.adapter.TeachingClassesAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.TeachingClassesDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeachingClassesFragment extends Fragment {
    private static final String OPEN_CLASS_ID = "openClassId";
    private static final String USER_ID = "userId";

    int openClassId;
    int userId;

    public static TeachingClassesFragment newInstance(int userId, int openClassId) {
        TeachingClassesFragment fragment = new TeachingClassesFragment();
        Bundle args = new Bundle();
        args.putString(OPEN_CLASS_ID, String.valueOf(openClassId));
        args.putString(USER_ID, String.valueOf(userId));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teaching_classes, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());

        if (getArguments() != null) {
            userId = Integer.parseInt(getArguments().getString(USER_ID));
            openClassId = Integer.parseInt(getArguments().getString(OPEN_CLASS_ID));
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            List<TeachingClassesDTO> listTeachingClasses = appDatabase.openClassDao().getTeachingClassesByUserAndOpenClass(userId, openClassId);
            handler.post(() -> {
                Log.d("RESULT", String.valueOf(listTeachingClasses.size()));
                TeachingClassesAdapter teachingClassesAdapter = new TeachingClassesAdapter(getContext(), listTeachingClasses);

                ListView listView = view.findViewById(R.id.listTeachingSubjects);
                listView.setAdapter(teachingClassesAdapter);
                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    int subjectId = listTeachingClasses.get(position).getSubjectId();
                    int openClassId = listTeachingClasses.get(position).getOpenClassId();
                    InputScoreFragment inputScoreFragment = InputScoreFragment.newInstance(openClassId, subjectId);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, inputScoreFragment);
                    fragmentTransaction.commit();
                });
            });
        });
    }
}