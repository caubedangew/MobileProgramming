package com.btl.login.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.adapter.ScoreAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.ScoreDTO;
import com.btl.login.interfaces.OnScoreActionListener;

import java.util.List;
import java.util.Locale;


public class ScoreFragment extends Fragment implements OnScoreActionListener {
    private Spinner spinnerClasses;
    private RecyclerView recyclerStudents;
    private ScoreAdapter scoreAdapter;
    private AppDatabase appDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_score, container, false);

        // Liên kết giao diện
        spinnerClasses = view.findViewById(R.id.spinner_classes);
        recyclerStudents = view.findViewById(R.id.recycler_students);

        // Cài đặt LayoutManager cho RecyclerView
        recyclerStudents.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Khởi tạo Database
        appDatabase = AppDatabase.getDatabase(requireContext());

        // Load danh sách lớp vào Spinner
        loadClassesIntoSpinner();

        return view;
    }

    private void loadClassesIntoSpinner() {
        new Thread(() -> {
            List<String> classNames = appDatabase.studentClassDao().getAllClassNames();

            // Kiểm tra dữ liệu từ cơ sở dữ liệu
            if (classNames == null || classNames.isEmpty()) {
                requireActivity().runOnUiThread(() -> {
                    // Hiển thị thông báo lỗi nếu không có dữ liệu lớp học
                    Toast.makeText(requireContext(), "Không có lớp học nào trong cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                });
                return; // Ngừng thực hiện nếu không có dữ liệu
            }

            // Cài đặt dữ liệu cho Spinner
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClasses.setAdapter(adapter);

                spinnerClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedClass = parent.getItemAtPosition(position).toString();

                        // Log lớp đã chọn (hữu ích khi gỡ lỗi)
                        Log.d("ScoreFragment", "Lớp được chọn: " + selectedClass);

                        // Tải danh sách sinh viên theo lớp
                        loadStudentsByClass(selectedClass);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d("ScoreFragment", "Không có lớp nào được chọn");
                    }
                });
            });
        }).start();
    }

    private void loadStudentsByClass(String className) {
        new Thread(() -> {
            List<ScoreDTO> studentList = appDatabase.studentScoreDao().getScoresByClassName(className);
            Log.d("ScoreFragment", "Danh sách sinh viên cho lớp " + className + ": " + (studentList != null ? studentList.size() : 0));

            requireActivity().runOnUiThread(() -> {
                if (studentList == null || studentList.isEmpty()) {
                    Toast.makeText(requireContext(), "Không có sinh viên nào trong lớp này!", Toast.LENGTH_SHORT).show();
                    recyclerStudents.setAdapter(null);
                } else {
                    scoreAdapter = new ScoreAdapter(requireContext(), studentList, ScoreFragment.this);
                    recyclerStudents.setAdapter(scoreAdapter);
                }
            });
        }).start();
    }
    @Override
    public void onScoreRowLongClick(ScoreDTO scoreDTO) {
        showScoreDetailsDialog(scoreDTO);
    }

    private void showScoreDetailsDialog(ScoreDTO scoreDTO) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_score_details, null);

        // Hiển thị thông tin giáo viên, môn học, kỳ học, năm học
        ((TextView) dialogView.findViewById(R.id.tv_teacher_name)).setText(scoreDTO.getTeacherFullName());
        ((TextView) dialogView.findViewById(R.id.tv_subject_name)).setText(scoreDTO.getSubjectName());
        ((TextView) dialogView.findViewById(R.id.tv_semester_name)).setText(scoreDTO.getSemesterName());
        ((TextView) dialogView.findViewById(R.id.tv_academic_year_name)).setText(scoreDTO.getAcademicYearName());

        // Định dạng số với Locale.US trước khi hiển thị
        ((TextView) dialogView.findViewById(R.id.tv_process_score))
                .setText(String.format(Locale.US, "%.2f", scoreDTO.getProcessScore()));
        ((TextView) dialogView.findViewById(R.id.tv_midterm_score))
                .setText(String.format(Locale.US, "%.2f", scoreDTO.getMidtermScore()));
        ((TextView) dialogView.findViewById(R.id.tv_final_score))
                .setText(String.format(Locale.US, "%.2f", scoreDTO.getFinalScore()));

        builder.setView(dialogView);
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}