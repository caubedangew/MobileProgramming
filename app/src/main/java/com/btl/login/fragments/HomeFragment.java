package com.btl.login.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.btl.login.MainActivity;
import com.btl.login.R;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.entities.Teacher;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    TextView txtMessageRecommendToUpdateData;
    LinearLayout layoutInputScore, layoutStatistics;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        txtMessageRecommendToUpdateData = view.findViewById(R.id.txtMessageRecommendToUpdateData);
        layoutInputScore = view.findViewById(R.id.layoutInputScore);
        layoutStatistics = view.findViewById(R.id.layoutStatistics);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String messageForUpdatingData = getString(R.string.txtMessageRecommendToUpdateData);

        SpannableString spannableString = new SpannableString(messageForUpdatingData);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                UserFragment userFragment = new UserFragment();
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                toolbar.setTitle("Thông tin cá nhân");
                ((NavigationView) requireActivity().findViewById(R.id.nav_view)).getMenu().findItem(R.id.user).setChecked(true);
                redirectToSpecifiedFragment(userFragment, true);
            }
        };

        // Inflate the layout for this fragment
        spannableString.setSpan(clickableSpan, messageForUpdatingData.indexOf(".") + 1, messageForUpdatingData.indexOf("đây") + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtMessageRecommendToUpdateData.setText(spannableString);
        txtMessageRecommendToUpdateData.setMovementMethod(LinkMovementMethod.getInstance());

        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            Teacher currentTeacher = appDatabase.teacherDao().getTeacherByEmail(user.getEmail());
            handler.post(() -> {
                if (currentTeacher != null) {
                    if (currentTeacher.getDateOfBirth() != null && currentTeacher.getPhoneNumber() != null && currentTeacher.getAddress() != null) {
                        txtMessageRecommendToUpdateData.setVisibility(View.GONE);
                    } else txtMessageRecommendToUpdateData.setVisibility(View.VISIBLE);
                }
            });
        });

        layoutInputScore.setOnClickListener(v -> {
            TeachingSubjectsFragment teachingSubjectsFragment = new TeachingSubjectsFragment();
            ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle("Nhập điểm cho sinh viên");
            ((NavigationView) requireActivity().findViewById(R.id.nav_view)).getMenu().findItem(R.id.inputScore).setChecked(true);
            redirectToSpecifiedFragment(teachingSubjectsFragment, true);
        });

        layoutStatistics.setOnClickListener(v -> {
            UserStatisticsFragment statisticsFragment = new UserStatisticsFragment();
            ((NavigationView) requireActivity().findViewById(R.id.nav_view)).getMenu().findItem(R.id.statistics).setChecked(true);
            ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle("Thống kê điểm số sinh viên");
            redirectToSpecifiedFragment(statisticsFragment, true);
        });
    }

    private void redirectToSpecifiedFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        ((MainActivity) getActivity()).checkBackStack();
    }
}