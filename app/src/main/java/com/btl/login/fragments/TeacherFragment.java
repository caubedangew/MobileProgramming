package com.btl.login.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.btl.login.adapter.TeacherAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.TeacherDTO;
import com.btl.login.entities.Teacher;
import com.btl.login.interfaces.OnTeacherDeleteListener;

import java.util.List;

public class TeacherFragment extends Fragment implements OnTeacherDeleteListener {

    private ListView listViewTeachers;
    private Spinner spinnerTeachers;
    private EditText eTxtEmail, eTxtFirstName, eTxtLastName;
    private Button btnAddTeacher, btnEditTeacher;
    private TeacherAdapter adapter;
    private AppDatabase appDatabase;
    private List<TeacherDTO> teacherList;

    private String selectedDepartment; // Lưu trữ khoa được chọn

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_teacher, container, false);

        // Liên kết các thành phần giao diện
        listViewTeachers = view.findViewById(R.id.listViewTeachers);
        spinnerTeachers = view.findViewById(R.id.spinnerTeachers);
        eTxtEmail = view.findViewById(R.id.eTxtEmail);
        eTxtFirstName = view.findViewById(R.id.eTxtFirstName);
        eTxtLastName = view.findViewById(R.id.eTxtLastName);
        btnAddTeacher = view.findViewById(R.id.btnAdd);
        btnEditTeacher = view.findViewById(R.id.btnEdit);

        // Khởi tạo Room Database
        appDatabase = AppDatabase.getDatabase(requireContext());

        // Tải dữ liệu lên Spinner
        loadSpinnerData();

        // Tải danh sách giáo viên
        loadTeachersFromDatabase();

        // Xử lý sự kiện thêm giáo viên
        btnAddTeacher.setOnClickListener(v -> handleAddTeacher());

        // Xử lý sự kiện chỉnh sửa giáo viên
        btnEditTeacher.setOnClickListener(v -> handleEditTeacher());

        // Xử lý sự kiện chọn từ Spinner
        spinnerTeachers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDepartment = null;
            }
        });

        // Xử lý sự kiện nhấn vào danh sách giáo viên (ListView)
        listViewTeachers.setOnItemClickListener((parent, view1, position, id) -> {
            TeacherDTO selectedTeacher = (TeacherDTO) parent.getItemAtPosition(position);
            if (selectedTeacher != null) {
                displayTeacherInfo(selectedTeacher);
                btnEditTeacher.setTag(selectedTeacher); // Lưu thông tin giáo viên để chỉnh sửa
            }
        });

        return view;
    }

    private void displayTeacherInfo(TeacherDTO selectedTeacher) {
        eTxtEmail.setText(selectedTeacher.getTeacher().getEmail());
        eTxtFirstName.setText(selectedTeacher.getTeacher().getFirstName());
        eTxtLastName.setText(selectedTeacher.getTeacher().getLastName());

        ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) spinnerTeachers.getAdapter();
        int spinnerPosition = spinnerAdapter.getPosition(selectedTeacher.getDepartmentName());
        if (spinnerPosition >= 0) {
            spinnerTeachers.setSelection(spinnerPosition);
        }
    }

    private void handleAddTeacher() {
        new Thread(() -> {
            String email = eTxtEmail.getText().toString();

            // Kiểm tra email trùng
            int emailExists = appDatabase.teacherDao().checkEmailExists(email);

            requireActivity().runOnUiThread(() -> {
                if (emailExists > 0) {
                    Toast.makeText(getContext(), "Email đã được sử dụng!", Toast.LENGTH_SHORT).show();
                } else {
                    addTeacher();
                }
            });
        }).start();
    }

    private void addTeacher() {
        new Thread(() -> {
            int departmentId = getDepartmentIdByName(selectedDepartment);
            Teacher newTeacher = new Teacher(
                    eTxtFirstName.getText().toString(),
                    eTxtLastName.getText().toString(),
                    eTxtEmail.getText().toString(),
                    departmentId
            );
            appDatabase.teacherDao().addTeachers(newTeacher);

            requireActivity().runOnUiThread(() -> {
                loadTeachersFromDatabase();
                Toast.makeText(getContext(), "Đã thêm giáo viên mới!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            });
        }).start();
    }

    private void handleEditTeacher() {
        TeacherDTO selectedTeacher = (TeacherDTO) btnEditTeacher.getTag();
        if (selectedTeacher != null) {
            new Thread(() -> {
                String email = eTxtEmail.getText().toString();
                int emailExists = appDatabase.teacherDao().checkEmailExists(email);

                requireActivity().runOnUiThread(() -> {
                    if (emailExists > 0 && !email.equals(selectedTeacher.getTeacher().getEmail())) {
                        Toast.makeText(getContext(), "Email đã được sử dụng!", Toast.LENGTH_SHORT).show();
                    } else {
                        editTeacher(selectedTeacher);
                    }
                });
            }).start();
        } else {
            Toast.makeText(getContext(), "Vui lòng chọn giáo viên để chỉnh sửa!", Toast.LENGTH_SHORT).show();
        }
    }

    private void editTeacher(TeacherDTO selectedTeacher) {
        new Thread(() -> {
            Teacher updatedTeacher = selectedTeacher.getTeacher();
            updatedTeacher.setEmail(eTxtEmail.getText().toString());
            updatedTeacher.setFirstName(eTxtFirstName.getText().toString());
            updatedTeacher.setLastName(eTxtLastName.getText().toString());
            int departmentId = getDepartmentIdByName(selectedDepartment);
            updatedTeacher.setDepartmentId(departmentId);

            appDatabase.teacherDao().updateTeacher(updatedTeacher);

            requireActivity().runOnUiThread(() -> {
                loadTeachersFromDatabase();
                Toast.makeText(getContext(), "Thông tin giáo viên đã được cập nhật!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            });
        }).start();
    }

    private void loadTeachersFromDatabase() {
        new Thread(() -> {
            teacherList = appDatabase.teacherDao().getTeachersWithDepartmentName();
            requireActivity().runOnUiThread(() -> {
                adapter = new TeacherAdapter(requireContext(), teacherList, this); // Truyền listener để xử lý xóa
                listViewTeachers.setAdapter(adapter);
            });
        }).start();
    }

    private void loadSpinnerData() {
        new Thread(() -> {
            List<String> departmentNames = appDatabase.departmentDao().getAllDepartmentNames();
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, departmentNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTeachers.setAdapter(spinnerAdapter);
            });
        }).start();
    }

    private int getDepartmentIdByName(String departmentName) {
        return appDatabase.departmentDao().getDepartmentIdByName(departmentName);
    }

    private void clearInputFields() {
        eTxtEmail.setText("");
        eTxtFirstName.setText("");
        eTxtLastName.setText("");
        spinnerTeachers.setSelection(0);
    }

    @Override
    public void onTeacherDelete(TeacherDTO teacher, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa giáo viên")
                .setMessage("Bạn có chắc chắn muốn xóa giáo viên " + teacher.getTeacher().getFirstName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new Thread(() -> {
                        appDatabase.teacherDao().deleteTeacher(teacher.getTeacher());
                        requireActivity().runOnUiThread(() -> {
                            teacherList.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Đã xóa giáo viên: " + teacher.getTeacher().getFirstName(), Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}