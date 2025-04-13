package com.btl.login.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.btl.login.MainActivity;
import com.btl.login.R;
import com.btl.login.adapter.TeachingClassesAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.TeachingClassesDTO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeachingClassesFragment extends Fragment {
    private static final String OPEN_CLASS_ID = "openClassId";
    private static final String USER_ID = "userId";
    private static final String TITLE = "title";

    int openClassId;
    int userId;
    String title;
    TextView txtTitle;

    public static TeachingClassesFragment newInstance(int userId, int openClassId, String title) {
        TeachingClassesFragment fragment = new TeachingClassesFragment();
        Bundle args = new Bundle();
        args.putString(OPEN_CLASS_ID, String.valueOf(openClassId));
        args.putString(USER_ID, String.valueOf(userId));
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = Integer.parseInt(getArguments().getString(USER_ID));
            openClassId = Integer.parseInt(getArguments().getString(OPEN_CLASS_ID));
            title = getArguments().getString(TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teaching_classes, container, false);

        txtTitle = view.findViewById(R.id.txtTeachingClassesTitle);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());


        txtTitle.setText("Danh sách các lớp học được giảng dạy môn " + title);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            Log.d("RESULT", String.valueOf(appDatabase.openClassDao().countSubjectScoreBySubjectId()));
            List<TeachingClassesDTO> listTeachingClasses = appDatabase.openClassDao().getTeachingClassesByUserAndOpenClass(userId, openClassId);
            handler.post(() -> {
                TeachingClassesAdapter teachingClassesAdapter = new TeachingClassesAdapter(getContext(), listTeachingClasses);

                ListView listView = view.findViewById(R.id.listTeachingSubjects);
                listView.setAdapter(teachingClassesAdapter);
                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    String title = "lớp " + listTeachingClasses.get(position).getClassName();
                    int subjectId = listTeachingClasses.get(position).getSubjectId();
                    int openClassId = listTeachingClasses.get(position).getOpenClassId();
                    InputScoreFragment inputScoreFragment = InputScoreFragment.newInstance(openClassId, subjectId, title);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, inputScoreFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    ((MainActivity) getActivity()).checkBackStack();
                });
            });
        });
    }
}