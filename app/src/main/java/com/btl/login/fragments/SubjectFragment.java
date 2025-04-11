package com.btl.login.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.btl.login.R;
import com.btl.login.adapter.SubjectAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.entities.Subject;
import com.btl.login.interfaces.OnSubjectDeleteListener;

import java.util.Arrays;
import java.util.List;

public class SubjectFragment extends Fragment implements OnSubjectDeleteListener {

    private ListView listViewSubjects;
    private Spinner spinnerCreditNumbers;
    private EditText eTxtSubjectName;
    private Button btnAddSubject, btnEditSubject;
    private SubjectAdapter adapter;
    private AppDatabase appDatabase;

    private Subject selectedSubject; // Môn học được chọn để chỉnh sửa
    private List<Subject> subjectList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_subject, container, false);

        // Liên kết các thành phần giao diện
        listViewSubjects = view.findViewById(R.id.listViewSubjects);
        spinnerCreditNumbers = view.findViewById(R.id.spinnerCreditNumbers);
        eTxtSubjectName = view.findViewById(R.id.eTxtSubjName);
        btnAddSubject = view.findViewById(R.id.btnAdd);
        btnEditSubject = view.findViewById(R.id.btnEdit);

        // Khởi tạo Room Database
        appDatabase = AppDatabase.getDatabase(requireContext());

        // Tải dữ liệu lên Spinner
        setupSpinner();

        // Tải danh sách môn học
        loadSubjectsFromDatabase();

        // Xử lý sự kiện thêm môn học
        btnAddSubject.setOnClickListener(v -> handleAddSubject());

        // Xử lý sự kiện chỉnh sửa môn học
        btnEditSubject.setOnClickListener(v -> handleEditSubject());

        // Xử lý sự kiện chọn môn học trong danh sách
        listViewSubjects.setOnItemClickListener((parent, view1, position, id) -> {
            selectedSubject = (Subject) parent.getItemAtPosition(position);
            if (selectedSubject != null) {
                displaySubjectInfo(selectedSubject);
            }
        });

        return view;
    }

    private void setupSpinner() {
        List<Double> creditOptions = Arrays.asList(1.0, 1.5, 2.0, 2.5, 3.0, 4.0);
        ArrayAdapter<Double> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, creditOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCreditNumbers.setAdapter(adapter);
    }

    private void displaySubjectInfo(Subject subject) {
        eTxtSubjectName.setText(subject.getSubjectName());
        ArrayAdapter<Double> adapter = (ArrayAdapter<Double>) spinnerCreditNumbers.getAdapter();
        int spinnerPosition = adapter.getPosition(subject.getCreditNumber());
        spinnerCreditNumbers.setSelection(spinnerPosition);
    }

    private void handleAddSubject() {
        new Thread(() -> {
            String subjectName = eTxtSubjectName.getText().toString();
            double creditNumber = Double.parseDouble(spinnerCreditNumbers.getSelectedItem().toString());

            // Kiểm tra môn học đã tồn tại
            int exists = appDatabase.subjectDao().checkSubjectExists(subjectName);
            requireActivity().runOnUiThread(() -> {
                if (exists > 0) {
                    Toast.makeText(getContext(), "Môn học đã tồn tại!", Toast.LENGTH_SHORT).show();
                } else {
                    addSubject(subjectName, creditNumber);
                }
            });
        }).start();
    }

    private void addSubject(String subjectName, double creditNumber) {
        new Thread(() -> {
            Subject newSubject = new Subject(subjectName, creditNumber);
            appDatabase.subjectDao().addSubject(newSubject);

            requireActivity().runOnUiThread(() -> {
                loadSubjectsFromDatabase();
                Toast.makeText(getContext(), "Đã thêm môn học!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            });
        }).start();
    }

    private void handleEditSubject() {
        if (selectedSubject != null) {
            new Thread(() -> {
                try {
                    String newSubjectName = eTxtSubjectName.getText().toString();
                    double newCreditNumber = Double.parseDouble(spinnerCreditNumbers.getSelectedItem().toString());

                    int exists = appDatabase.subjectDao().checkSubjectExists(newSubjectName);
                    requireActivity().runOnUiThread(() -> {
                        if (exists > 0 && !selectedSubject.getSubjectName().equals(newSubjectName)) {
                            Toast.makeText(getContext(), "Tên môn học đã tồn tại!", Toast.LENGTH_SHORT).show();
                        } else {
                            updateSubject(newSubjectName, newCreditNumber);
                        }
                    });
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Lỗi khi chỉnh sửa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        } else {
            Toast.makeText(getContext(), "Vui lòng chọn môn học để chỉnh sửa!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSubject(String newSubjectName, double newCreditNumber) {
        new Thread(() -> {
            try {
                selectedSubject.setSubjectName(newSubjectName);
                selectedSubject.setCreditNumber(newCreditNumber);

                appDatabase.subjectDao().updateSubject(selectedSubject);

                requireActivity().runOnUiThread(() -> {
                    loadSubjectsFromDatabase();
                    Toast.makeText(getContext(), "Thông tin môn học đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    clearInputFields();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loadSubjectsFromDatabase() {
        new Thread(() -> {
            subjectList = appDatabase.subjectDao().getAllSubjects();
            requireActivity().runOnUiThread(() -> {
                adapter = new SubjectAdapter(requireContext(), subjectList, this); // Truyền Fragment làm listener
                listViewSubjects.setAdapter(adapter);
            });
        }).start();
    }

    private void clearInputFields() {
        eTxtSubjectName.setText("");
        spinnerCreditNumbers.setSelection(0);
        selectedSubject = null;
    }

    @Override
    public void onSubjectDelete(Subject subject, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa môn học")
                .setMessage("Bạn có chắc chắn muốn xóa môn học " + subject.getSubjectName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new Thread(() -> {
                        appDatabase.subjectDao().deleteSubject(subject);
                        requireActivity().runOnUiThread(() -> {
                            subjectList.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Đã xóa môn học: " + subject.getSubjectName(), Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}