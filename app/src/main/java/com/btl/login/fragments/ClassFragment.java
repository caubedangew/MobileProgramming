package com.btl.login.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.adapter.ClassAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.StudentClassDTO;
import com.btl.login.entities.StudentClass;
import com.btl.login.interfaces.OnClassActionListener;

import java.util.List;

public class ClassFragment extends Fragment implements OnClassActionListener {

    private RecyclerView recyclerClasses;
    private Spinner spinnerMajors;
    private EditText eTxtClassName;
    private Button btnAddClass, btnEditClass;
    private ProgressBar progressBarLoading;
    private TextView emptyTextView;
    private ClassAdapter adapter;
    private AppDatabase appDatabase;
    private String selectedMajor;
    private List<StudentClassDTO> classList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_class, container, false);

        // Liên kết các thành phần giao diện
        recyclerClasses = view.findViewById(R.id.recycler_classes); // Sử dụng RecyclerView
        spinnerMajors = view.findViewById(R.id.spinnerMajors); // Spinner cho ngành học
        eTxtClassName = view.findViewById(R.id.eTxtClassName); // Tên lớp học
        btnAddClass = view.findViewById(R.id.btnAdd); // Nút thêm lớp
        btnEditClass = view.findViewById(R.id.btnEdit); // Nút chỉnh sửa lớp
        progressBarLoading = view.findViewById(R.id.progressBarLoading); // ProgressBar khi tải
        emptyTextView = view.findViewById(R.id.emptyTextView); // Hiển thị nếu danh sách trống

        // Khởi tạo Room Database
        appDatabase = AppDatabase.getDatabase(requireContext());

        // Cấu hình RecyclerView cho danh sách lớp
        recyclerClasses.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tải dữ liệu ngành học vào Spinner
        loadSpinnerData();

        // Tải danh sách lớp học từ cơ sở dữ liệu
        loadClassesFromDatabase();

        // Xử lý sự kiện thêm lớp học
        btnAddClass.setOnClickListener(v -> handleAddClass());

        // Xử lý sự kiện chỉnh sửa lớp học
        btnEditClass.setOnClickListener(v -> handleEditClass());

        return view;
    }
    private void showToast(String message) {
        // Phương thức hiển thị Toast đơn giản, dễ tái sử dụng
        requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }
    @Override
    public void onClassClick(StudentClassDTO studentClass) {
        if (studentClass == null || studentClass.getStudentClass() == null) {
            Log.e("ClassClickError", "Dữ liệu lớp bị null hoặc không hợp lệ!");
            showToast("Dữ liệu lớp không hợp lệ!");
            return; // Dừng xử lý nếu dữ liệu không hợp lệ
        }

        // Gán dữ liệu vào trường nhập liệu
        eTxtClassName.setText(studentClass.getStudentClass().getClassName());

        // Cập nhật Spinner nếu tên ngành học không null
        String majorName = studentClass.getMajorName();
        if (majorName != null) {
            // Kiểm tra và đặt vị trí cho Spinner
            if (spinnerMajors.getAdapter() != null) {
                int position = ((ArrayAdapter<String>) spinnerMajors.getAdapter()).getPosition(majorName);
                if (position >= 0) {
                    spinnerMajors.setSelection(position);
                } else {
                    Log.e("SpinnerError", "Tên ngành học không tồn tại trong danh sách Spinner!");
                }
            } else {
                Log.e("SpinnerError", "Adapter của Spinner chưa được khởi tạo!");
                showToast("Danh sách ngành học chưa được tải, vui lòng thử lại!");
            }
        }

        // Gán dữ liệu lớp được chọn vào nút chỉnh sửa
        btnEditClass.setTag(studentClass);
    }

    private boolean isInputValid() {
        String className = eTxtClassName.getText().toString().trim();
        String selectedMajor = (String) spinnerMajors.getSelectedItem();

        // Kiểm tra tên lớp
        if (className.isEmpty()) {
            showToast("Tên lớp không được để trống!");
            return false;
        }

        // Kiểm tra ngành học
        if ("Vui lòng chọn ngành".equals(selectedMajor) || selectedMajor == null) {
            showToast("Vui lòng chọn ngành học hợp lệ!");
            return false;
        }

        return true; // Dữ liệu hợp lệ
    }

    private boolean isClassNameValid(String className) {
        if (className.isEmpty()) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Tên lớp không được để trống!", Toast.LENGTH_SHORT).show());
            return false;
        }
        return true;
    }
    private void handleAddClass() {
        new Thread(() -> {
            try {
                // Kiểm tra đầu vào
                if (!isInputValid()) {
                    return; // Dừng xử lý nếu dữ liệu không hợp lệ
                }

                String className = eTxtClassName.getText().toString().trim();

                // Kiểm tra tên lớp đã tồn tại trong cơ sở dữ liệu
                int classExists = appDatabase.studentClassDao().checkClassExists(className);
                requireActivity().runOnUiThread(() -> {
                    if (classExists > 0) {
                        showToast("Tên lớp đã tồn tại!"); // Hiển thị lỗi nếu tên lớp đã tồn tại
                    } else {
                        addClass(); // Gọi phương thức thêm lớp học mới
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Đã xảy ra lỗi khi kiểm tra tên lớp!"));
                Log.e("HandleAddClass", "Lỗi kiểm tra tên lớp: ", e);
            }
        }).start();
    }

    private void addClass() {
        new Thread(() -> {
            try {
                // Lấy dữ liệu từ các trường nhập liệu
                String className = eTxtClassName.getText().toString().trim();
                String selectedMajor = (String) spinnerMajors.getSelectedItem();

                // Lấy majorId từ tên ngành học thông qua DAO
                int majorId = appDatabase.majorDao().getMajorIdByName(selectedMajor);
                if (majorId <= 0) {
                    showToast("Ngành học không hợp lệ, vui lòng thử lại!");
                    return;
                }

                // Tạo đối tượng StudentClass và thêm vào cơ sở dữ liệu
                StudentClass newClass = new StudentClass(className, majorId);
                appDatabase.studentClassDao().addClasses(newClass);

                // Cập nhật giao diện sau khi thêm thành công
                requireActivity().runOnUiThread(() -> {
                    loadClassesFromDatabase(); // Tải lại danh sách lớp
                    showToast("Đã thêm lớp học mới!"); // Hiển thị thông báo thành công
                    clearInputFields(); // Dọn dẹp các trường nhập liệu
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Lỗi khi thêm lớp học!"));
                Log.e("AddClass", "Lỗi thêm lớp học: ", e);
            }
        }).start();
    }
    public void onClassTagForEdit(StudentClassDTO classDTO) {
        // Gán thông tin lớp học vào nút btnEditClass
        btnEditClass.setTag(classDTO);
    }

    private void handleEditClass() {
        // Lấy đối tượng lớp học được chọn từ Tag của btnEditClass
        StudentClassDTO selectedClass = (StudentClassDTO) btnEditClass.getTag();
        if (selectedClass != null) {
            new Thread(() -> {
                try {
                    // Lấy tên lớp từ trường nhập liệu
                    String className = eTxtClassName.getText().toString().trim();

                    // Kiểm tra tính hợp lệ của tên lớp
                    if (!isClassNameValid(className)) {
                        return; // Dừng xử lý nếu tên lớp không hợp lệ
                    }

                    // Kiểm tra tên lớp trùng (nhưng không phải tên của lớp đang chỉnh sửa)
                    int classExists = appDatabase.studentClassDao().checkClassExists(className);
                    requireActivity().runOnUiThread(() -> {
                        if (classExists > 0 && !className.equals(selectedClass.getStudentClass().getClassName())) {
                            showToast("Tên lớp đã được sử dụng!"); // Hiển thị thông báo lỗi
                        } else {
                            editClass(selectedClass); // Gọi phương thức chỉnh sửa lớp
                        }
                    });
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() ->
                            showToast("Đã xảy ra lỗi khi kiểm tra tên lớp!"));
                    Log.e("HandleEditClass", "Lỗi kiểm tra tên lớp: ", e);
                }
            }).start();
        } else {
            // Hiển thị thông báo nếu không có lớp nào được chọn
            showToast("Vui lòng chọn lớp để chỉnh sửa!");
        }
    }

    private void editClass(StudentClassDTO selectedClass) {
        new Thread(() -> {
            try {
                // Lấy dữ liệu lớp học được chỉnh sửa
                StudentClass updatedClass = selectedClass.getStudentClass();
                updatedClass.setClassName(eTxtClassName.getText().toString().trim());

                // Lấy majorId từ Spinner
                String selectedMajor = (String) spinnerMajors.getSelectedItem();
                if (selectedMajor == null || selectedMajor.isEmpty() || "Vui lòng chọn ngành".equals(selectedMajor)) {
                    showToast("Vui lòng chọn ngành học hợp lệ!");
                    return;
                }

                int majorId = appDatabase.majorDao().getMajorIdByName(selectedMajor);
                if (majorId <= 0) {
                    showToast("Ngành học không hợp lệ, vui lòng thử lại!");
                    return;
                }
                updatedClass.setMajorId(majorId);

                // Cập nhật thông tin lớp học trong cơ sở dữ liệu
                appDatabase.studentClassDao().updateClass(updatedClass);

                // Cập nhật giao diện sau khi chỉnh sửa thành công
                requireActivity().runOnUiThread(() -> {
                    loadClassesFromDatabase(); // Tải lại danh sách lớp
                    showToast("Thông tin lớp học đã được cập nhật!"); // Hiển thị thông báo thành công
                    clearInputFields(); // Dọn dẹp giao diện
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Lỗi khi chỉnh sửa lớp học!"));
                Log.e("EditClass", "Lỗi: ", e);
            }
        }).start();
    }

    private void loadClassesFromDatabase() {
        requireActivity().runOnUiThread(() -> progressBarLoading.setVisibility(View.VISIBLE)); // Hiển thị ProgressBar

        new Thread(() -> {
            try {
                // Tải danh sách lớp học từ cơ sở dữ liệu
                classList = appDatabase.studentClassDao().getClassesWithMajorName();

                requireActivity().runOnUiThread(() -> {
                    if (classList != null && !classList.isEmpty()) {
                        adapter = new ClassAdapter(requireContext(), classList, this);
                        recyclerClasses.setLayoutManager(new LinearLayoutManager(requireContext())); // Cấu hình RecyclerView
                        recyclerClasses.setAdapter(adapter); // Gắn adapter cho RecyclerView
                    } else {
                        showToast("Không có lớp nào trong danh sách!"); // Hiển thị thông báo nếu danh sách trống
                    }
                    progressBarLoading.setVisibility(View.GONE); // Ẩn ProgressBar
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showToast("Lỗi tải danh sách lớp học!"); // Hiển thị lỗi
                    progressBarLoading.setVisibility(View.GONE); // Ẩn ProgressBar
                });
                Log.e("LoadClasses", "Lỗi: ", e);
            }
        }).start();
    }

    private void loadSpinnerData() {
        new Thread(() -> {
            try {
                // Tải danh sách ngành học từ cơ sở dữ liệu
                List<String> majorNames = appDatabase.majorDao().getAllMajorNames();

                requireActivity().runOnUiThread(() -> {
                    if (majorNames != null && !majorNames.isEmpty()) {
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, majorNames);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Định dạng hiển thị Dropdown
                        spinnerMajors.setAdapter(spinnerAdapter); // Gắn adapter cho Spinner
                    } else {
                        showToast("Không có ngành học nào trong danh sách!"); // Hiển thị thông báo nếu danh sách trống
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> showToast("Lỗi tải danh sách ngành học!")); // Hiển thị lỗi
                Log.e("SpinnerData", "Lỗi: ", e);
            }
        }).start();
    }
    private int getMajorIdByName(String majorName) {
        if (majorName == null || majorName.isEmpty()) {
            Log.e("MajorId", "Tên ngành học không hợp lệ!");
            return -1; // Trả về giá trị lỗi nếu tên ngành không hợp lệ
        }
        return appDatabase.majorDao().getMajorIdByName(majorName); // Truy vấn ID ngành học từ tên
    }
    private void clearInputFields() {
        eTxtClassName.setText(""); // Xóa nội dung trường nhập tên lớp
        if (spinnerMajors.getAdapter() != null) {
            spinnerMajors.setSelection(0); // Đặt Spinner về vị trí đầu tiên
        }
    }
    public void onClassDelete(StudentClassDTO classDTO, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa lớp học")
                .setMessage("Bạn có chắc chắn muốn xóa lớp " + classDTO.getStudentClass().getClassName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            // Xóa lớp khỏi cơ sở dữ liệu
                            appDatabase.studentClassDao().deleteClass(classDTO.getStudentClass());

                            requireActivity().runOnUiThread(() -> {
                                if (classList != null && position < classList.size()) {
                                    classList.remove(position); // Xóa lớp khỏi danh sách
                                    adapter.notifyItemRemoved(position); // Cập nhật RecyclerView
                                    showToast("Đã xóa lớp: " + classDTO.getStudentClass().getClassName());
                                } else {
                                    showToast("Xóa thất bại do danh sách bị lỗi!");
                                }
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() -> showToast("Lỗi khi xóa lớp!"));
                            Log.e("DeleteClass", "Lỗi khi xóa lớp: ", e);
                        }
                    }).start();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()) // Xử lý nút hủy
                .setCancelable(false)
                .show();
    }
}