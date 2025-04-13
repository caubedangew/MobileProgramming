package com.btl.login.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.btl.login.R;
import com.btl.login.userViewModel.UserViewModel;

public class AdminFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Kết nối với layout XML
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Liên kết UserViewModel để kiểm tra vai trò người dùng
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Quan sát vai trò người dùng và kiểm tra quyền truy cập
        userViewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            if (!"admin".equals(role)) {
                Toast.makeText(getContext(), "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack(); // Quay lại màn hình trước
            }
        });

        // Liên kết các LinearLayout từ bố cục
        LinearLayout layoutRoles = view.findViewById(R.id.layoutRoles);
        LinearLayout layoutStudents = view.findViewById(R.id.layoutStudents);
        LinearLayout layoutCTS = view.findViewById(R.id.layoutCTS);
        LinearLayout layoutScore = view.findViewById(R.id.layoutScore);
        LinearLayout layoutStatistics = view.findViewById(R.id.layoutStatistics);
        LinearLayout layoutLogout = view.findViewById(R.id.layoutLogout);

        // Xử lý sự kiện khi nhấn vào các phần tử
        layoutRoles.setOnClickListener(v -> navigateToRoleFragment());
        layoutStudents.setOnClickListener(v -> navigateToStudentFragment());
        layoutCTS.setOnClickListener(v -> navigateToCTSFragment());
        layoutScore.setOnClickListener(v -> navigateToStudentScoreFragment());
        layoutStatistics.setOnClickListener(v -> navigateToStatisticsFragment());
        layoutLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void navigateToRoleFragment() {
        // Điều hướng đến RoleFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new RoleFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToCTSFragment() {
        // Điều hướng đến CTSFragment (Lớp, Giáo viên, Môn học)
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CTSFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToStudentFragment() {
        // Điều hướng đến StudentFragment (Danh sách sinh viên)
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new StudentFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToStudentScoreFragment() {
        // Điều hướng đến ScoreFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ScoreFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToStatisticsFragment() {
        // Điều hướng đến StatisticsFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new StatisticsAdminFragment())
                .addToBackStack(null)
                .commit();
    }

    private void logout() {
        // Đăng xuất và quay về LoginFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }
}