package com.btl.login.fragments;

import android.app.AlertDialog;
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
import androidx.fragment.app.Fragment;

import com.btl.login.R;
import com.btl.login.adapter.ClassAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.StudentClassWithMajorDTO;
import com.btl.login.entities.StudentClass;
import com.btl.login.interfaces.OnClassDeleteListener;

import java.util.List;

public class ClassFragment extends Fragment implements OnClassDeleteListener {

    private ListView listViewClasses;
    private Spinner spinnerMajors;
    private EditText eTxtClassName;
    private Button btnAddClass, btnEditClass;
    private ClassAdapter adapter;
    private AppDatabase appDatabase;
    private String selectedMajor; // Biến lưu ngành học được chọn
    private List<StudentClassWithMajorDTO> classList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_class, container, false);

        // Liên kết các thành phần giao diện
        listViewClasses = view.findViewById(R.id.listViewClasses);
        spinnerMajors = view.findViewById(R.id.spinnerMajors);
        eTxtClassName = view.findViewById(R.id.eTxtClassName);
        btnAddClass = view.findViewById(R.id.btnAdd);
        btnEditClass = view.findViewById(R.id.btnEdit);

        // Khởi tạo Room Database
        appDatabase = AppDatabase.getDatabase(requireContext());

        // Tải dữ liệu lên Spinner
        loadSpinnerData();

        // Tải danh sách lớp
        loadClassesFromDatabase();

        // Xử lý sự kiện thêm lớp học
        btnAddClass.setOnClickListener(v -> handleAddClass());

        // Xử lý sự kiện chỉnh sửa lớp học
        btnEditClass.setOnClickListener(v -> handleEditClass());

        // Xử lý sự kiện chọn từ Spinner
        spinnerMajors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMajor = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMajor = null;
            }
        });

        // Xử lý sự kiện nhấn vào danh sách lớp (ListView)
        listViewClasses.setOnItemClickListener((parent, view1, position, id) -> {
            StudentClassWithMajorDTO selectedClass = (StudentClassWithMajorDTO) parent.getItemAtPosition(position);
            if (selectedClass != null) {
                displayClassInfo(selectedClass);
                btnEditClass.setTag(selectedClass); // Lưu thông tin lớp để chỉnh sửa
            }
        });

        return view;
    }

    private void displayClassInfo(StudentClassWithMajorDTO selectedClass) {
        eTxtClassName.setText(selectedClass.getClassName());

        // Tìm vị trí của ngành trong Spinner và đặt lựa chọn
        ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) spinnerMajors.getAdapter();
        int spinnerPosition = spinnerAdapter.getPosition(selectedClass.getMajorName());
        if (spinnerPosition >= 0) {
            spinnerMajors.setSelection(spinnerPosition);
        }
    }

    private void handleAddClass() {
        new Thread(() -> {
            String className = eTxtClassName.getText().toString();
            int majorId = appDatabase.majorDao().getMajorIdByName(selectedMajor);

            int exists = appDatabase.studentClassDao().checkClassExists(className);
            requireActivity().runOnUiThread(() -> {
                if (exists > 0) {
                    Toast.makeText(getContext(), "Lớp đã tồn tại!", Toast.LENGTH_SHORT).show();
                } else {
                    addClass(className, majorId);
                }
            });
        }).start();
    }


    private void addClass(String className, int majorId) {
        new Thread(() -> {
            // Chuyển đổi sang StudentClass để thêm vào cơ sở dữ liệu
            StudentClass newClass = new StudentClass(className, majorId);
            appDatabase.studentClassDao().addClasses(newClass);

            requireActivity().runOnUiThread(() -> {
                loadClassesFromDatabase();
                Toast.makeText(getContext(), "Đã thêm lớp!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            });
        }).start();
    }

    private void handleEditClass() {
        StudentClassWithMajorDTO selectedClass = (StudentClassWithMajorDTO) btnEditClass.getTag();
        if (selectedClass != null) {
            new Thread(() -> {
                String newClassName = eTxtClassName.getText().toString();
                int majorId = appDatabase.majorDao().getMajorIdByName(selectedMajor);

                int exists = appDatabase.studentClassDao().checkClassExists(newClassName);
                requireActivity().runOnUiThread(() -> {
                    if (exists > 0 && !selectedClass.getClassName().equals(newClassName)) {
                        Toast.makeText(getContext(), "Tên lớp đã tồn tại!", Toast.LENGTH_SHORT).show();
                    } else {
                        updateClass(selectedClass, newClassName, majorId);
                    }
                });
            }).start();
        } else {
            Toast.makeText(getContext(), "Vui lòng chọn lớp!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateClass(StudentClassWithMajorDTO selectedClass, String newClassName, int majorId) {
        new Thread(() -> {
            // Chuyển đổi sang StudentClass để cập nhật trong cơ sở dữ liệu
            StudentClass updatedClass = new StudentClass(newClassName, majorId);
            updatedClass.setId(selectedClass.getId());
            appDatabase.studentClassDao().updateClass(updatedClass);

            requireActivity().runOnUiThread(() -> {
                loadClassesFromDatabase();
                Toast.makeText(getContext(), "Thông tin lớp đã được cập nhật!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            });
        }).start();
    }

    private void loadClassesFromDatabase() {
        new Thread(() -> {
            classList = appDatabase.studentClassDao().getClassesWithMajor();
            requireActivity().runOnUiThread(() -> {
                adapter = new ClassAdapter(requireContext(), classList, this);
                listViewClasses.setAdapter(adapter);
            });
        }).start();
    }

    private void loadSpinnerData() {
        new Thread(() -> {
            List<String> majors = appDatabase.majorDao().getAllMajorNames();
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, majors);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMajors.setAdapter(spinnerAdapter);
            });
        }).start();
    }

    @Override
    public void onClassDelete(StudentClassWithMajorDTO studentClass, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa lớp học")
                .setMessage("Bạn có chắc chắn muốn xóa lớp " + studentClass.getClassName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new Thread(() -> {
                        // Xóa lớp khỏi cơ sở dữ liệu
                        appDatabase.studentClassDao().deleteClassById(studentClass.getId());

                        requireActivity().runOnUiThread(() -> {
                            // Xóa lớp khỏi danh sách hiển thị
                            classList.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Đã xóa lớp: " + studentClass.getClassName(), Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void clearInputFields() {
        eTxtClassName.setText("");
        if (spinnerMajors.getAdapter() != null && spinnerMajors.getAdapter().getCount() > 0) {
            spinnerMajors.setSelection(0);
        }
    }
}