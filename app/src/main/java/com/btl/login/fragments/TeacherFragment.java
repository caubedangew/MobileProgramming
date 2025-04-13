package com.btl.login.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.adapter.TeacherAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.TeacherDTO;
import com.btl.login.entities.Teacher;
import com.btl.login.interfaces.OnTeacherActionListener;

import java.util.List;

public class TeacherFragment extends Fragment implements OnTeacherActionListener {

    private RecyclerView recyclerTeacher; // Thay ListView bằng RecyclerView
    private Spinner spinnerTeachers;
    private EditText eTxtEmail, eTxtFirstName, eTxtLastName;
    private Button btnAddTeacher, btnEditTeacher;
    private ProgressBar progressBarLoading;
    private TeacherAdapter adapter;
    private AppDatabase appDatabase;
    private List<TeacherDTO> teacherList;
    private String selectedDepartment; // Lưu trữ khoa được chọn

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_teacher, container, false);

        // Liên kết các thành phần giao diện
        recyclerTeacher = view.findViewById(R.id.recycler_teacher); // Sử dụng RecyclerView
        spinnerTeachers = view.findViewById(R.id.spinnerTeachers);
        eTxtEmail = view.findViewById(R.id.eTxtEmail);
        eTxtFirstName = view.findViewById(R.id.eTxtFirstName);
        eTxtLastName = view.findViewById(R.id.eTxtLastName);
        btnAddTeacher = view.findViewById(R.id.btnAdd);
        btnEditTeacher = view.findViewById(R.id.btnEdit);
        progressBarLoading = view.findViewById(R.id.progressBarLoading);

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

        return view;
    }

    @Override
    public void onTeacherClick(TeacherDTO teacher) {
        if (teacher == null || teacher.getTeacher() == null) {
            Log.e("TeacherClickError", "Dữ liệu giáo viên bị null hoặc không hợp lệ!");
            showToast("Dữ liệu giáo viên không hợp lệ!");
            return; // Dừng xử lý nếu dữ liệu không hợp lệ
        }

        // Gán dữ liệu vào các trường nhập liệu
        eTxtEmail.setText(teacher.getTeacher().getEmail());
        eTxtFirstName.setText(teacher.getTeacher().getFirstName());
        eTxtLastName.setText(teacher.getTeacher().getLastName());

        // Cập nhật Spinner nếu tên khoa không null
        String departmentName = teacher.getDepartmentName();
        if (departmentName != null) {
            // Kiểm tra và đặt vị trí cho Spinner
            if (spinnerTeachers.getAdapter() != null) {
                int position = ((ArrayAdapter<String>) spinnerTeachers.getAdapter()).getPosition(departmentName);
                if (position >= 0) {
                    spinnerTeachers.setSelection(position);
                } else {
                    Log.e("SpinnerError", "Tên khoa không tồn tại trong danh sách Spinner!");
                }
            } else {
                Log.e("SpinnerError", "Adapter của Spinner chưa được khởi tạo!");
                showToast("Danh sách khoa chưa được tải, vui lòng thử lại!");
            }
        }

        // Gán dữ liệu giáo viên được chọn vào nút chỉnh sửa
        btnEditTeacher.setTag(teacher);
    }

    private boolean isInputValid() {
        String email = eTxtEmail.getText().toString().trim();
        String firstName = eTxtFirstName.getText().toString().trim();
        String lastName = eTxtLastName.getText().toString().trim();
        String selectedDepartment = (String) spinnerTeachers.getSelectedItem();

        // Kiểm tra email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email không hợp lệ hoặc để trống!");
            return false;
        }

        // Kiểm tra họ và tên
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showToast("Vui lòng điền đầy đủ họ và tên!");
            return false;
        }

        // Kiểm tra khoa
        if ("Vui lòng chọn khoa".equals(selectedDepartment)) {
            showToast("Vui lòng chọn khoa hợp lệ!");
            return false;
        }

        return true;
    }

    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Email không được để trống!", Toast.LENGTH_SHORT).show());
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Email không hợp lệ!", Toast.LENGTH_SHORT).show());
            return false;
        }
        return true;
    }

    private void handleAddTeacher() {
        new Thread(() -> {
            try {
                // Kiểm tra đầu vào
                if (!isInputValid()) {
                    return; // Dừng xử lý nếu dữ liệu không hợp lệ
                }

                String email = eTxtEmail.getText().toString().trim();

                // Kiểm tra email đã tồn tại trong cơ sở dữ liệu
                int emailExists = appDatabase.teacherDao().checkEmailExists(email);
                requireActivity().runOnUiThread(() -> {
                    if (emailExists > 0) {
                        showToast("Email đã được sử dụng!"); // Hiển thị lỗi nếu email đã tồn tại
                    } else {
                        addTeacher(); // Gọi phương thức thêm giáo viên mới
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Đã xảy ra lỗi khi kiểm tra email!"));
                Log.e("HandleAddTeacher", "Lỗi kiểm tra email: ", e);
            }
        }).start();
    }

    private void addTeacher() {
        new Thread(() -> {
            try {
                // Lấy dữ liệu từ các trường nhập liệu
                String firstName = eTxtFirstName.getText().toString().trim();
                String lastName = eTxtLastName.getText().toString().trim();
                String email = eTxtEmail.getText().toString().trim();
                String selectedDepartment = (String) spinnerTeachers.getSelectedItem();

                // Lấy departmentId từ tên khoa thông qua DAO
                int departmentId = getDepartmentIdByName(selectedDepartment);
                if (departmentId <= 0) {
                    showToast("Khoa không hợp lệ, vui lòng thử lại!");
                    return;
                }

                // Tạo đối tượng Teacher và thêm vào cơ sở dữ liệu
                Teacher newTeacher = new Teacher(firstName, lastName, email, departmentId);
                appDatabase.teacherDao().addTeachers(newTeacher);

                // Cập nhật giao diện sau khi thêm thành công
                requireActivity().runOnUiThread(() -> {
                    loadTeachersFromDatabase(); // Tải lại danh sách giáo viên
                    showToast("Đã thêm giáo viên mới!"); // Hiển thị thông báo thành công
                    clearInputFields(); // Dọn dẹp các trường nhập liệu
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Lỗi khi thêm giáo viên!"));
                Log.e("AddTeacher", "Lỗi thêm giáo viên: ", e);
            }
        }).start();
    }

    @Override
    public void onTeacherTagForEdit(TeacherDTO teacherDTO) {
        // Gán thông tin giáo viên vào nút btnEditTeacher
        btnEditTeacher.setTag(teacherDTO);
    }

    private void handleEditTeacher() {
        // Lấy đối tượng giáo viên được chọn từ Tag của btnEditTeacher
        TeacherDTO selectedTeacher = (TeacherDTO) btnEditTeacher.getTag();
        if (selectedTeacher != null) {
            new Thread(() -> {
                try {
                    // Lấy email từ trường nhập liệu
                    String email = eTxtEmail.getText().toString().trim();

                    // Kiểm tra email với phương thức tách riêng
                    if (!isEmailValid(email)) {
                        return; // Dừng xử lý nếu email không hợp lệ
                    }

                    // Kiểm tra email trùng (nhưng không phải email của giáo viên đang chỉnh sửa)
                    int emailExists = appDatabase.teacherDao().checkEmailExists(email);
                    requireActivity().runOnUiThread(() -> {
                        if (emailExists > 0 && !email.equals(selectedTeacher.getTeacher().getEmail())) {
                            showToast("Email đã được sử dụng!"); // Hiển thị thông báo lỗi
                        } else {
                            editTeacher(selectedTeacher); // Gọi phương thức chỉnh sửa
                        }
                    });
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() ->
                            showToast("Đã xảy ra lỗi khi kiểm tra email!"));
                    Log.e("HandleEditTeacher", "Lỗi kiểm tra email: ", e);
                }
            }).start();
        } else {
            // Hiển thị thông báo nếu không có giáo viên nào được chọn
            showToast("Vui lòng chọn giáo viên để chỉnh sửa!");
        }
    }

    private void editTeacher(TeacherDTO selectedTeacher) {
        new Thread(() -> {
            try {
                // Lấy dữ liệu giáo viên được chỉnh sửa
                Teacher updatedTeacher = selectedTeacher.getTeacher();
                updatedTeacher.setEmail(eTxtEmail.getText().toString().trim());
                updatedTeacher.setFirstName(eTxtFirstName.getText().toString().trim());
                updatedTeacher.setLastName(eTxtLastName.getText().toString().trim());

                // Lấy departmentId từ Spinner
                String selectedDepartment = (String) spinnerTeachers.getSelectedItem();
                if (selectedDepartment == null || selectedDepartment.isEmpty() || "Vui lòng chọn khoa".equals(selectedDepartment)) {
                    showToast("Vui lòng chọn khoa hợp lệ!");
                    return;
                }

                int departmentId = getDepartmentIdByName(selectedDepartment);
                if (departmentId <= 0) {
                    showToast("Khoa không hợp lệ, vui lòng thử lại!");
                    return;
                }
                updatedTeacher.setDepartmentId(departmentId);

                // Cập nhật thông tin giáo viên trong cơ sở dữ liệu
                appDatabase.teacherDao().updateTeacher(updatedTeacher);

                // Cập nhật giao diện sau khi chỉnh sửa thành công
                requireActivity().runOnUiThread(() -> {
                    loadTeachersFromDatabase(); // Tải lại danh sách giáo viên
                    showToast("Thông tin giáo viên đã được cập nhật!"); // Hiển thị thông báo thành công
                    clearInputFields(); // Dọn dẹp giao diện
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Lỗi khi chỉnh sửa giáo viên!"));
                Log.e("EditTeacher", "Lỗi: ", e);
            }
        }).start();
    }

    private void loadTeachersFromDatabase() {
        requireActivity().runOnUiThread(() -> progressBarLoading.setVisibility(View.VISIBLE)); // Hiển thị ProgressBar

        new Thread(() -> {
            try {
                // Tải danh sách giáo viên từ cơ sở dữ liệu
                teacherList = appDatabase.teacherDao().getTeachersWithDepartmentName();

                requireActivity().runOnUiThread(() -> {
                    if (teacherList != null && !teacherList.isEmpty()) {
                        adapter = new TeacherAdapter(requireContext(), teacherList, this);
                        recyclerTeacher.setLayoutManager(new LinearLayoutManager(requireContext()));
                        recyclerTeacher.setAdapter(adapter);
                    } else {
                        showToast("Không có giáo viên nào trong danh sách!");
                    }
                    progressBarLoading.setVisibility(View.GONE); // Ẩn ProgressBar
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showToast("Lỗi tải danh sách giáo viên!"); // Hiển thị thông báo lỗi
                    progressBarLoading.setVisibility(View.GONE);
                });
                Log.e("LoadTeachers", "Lỗi: ", e);
            }
        }).start();
    }

    private void loadSpinnerData() {
        new Thread(() -> {
            try {
                List<String> departmentNames = appDatabase.departmentDao().getAllDepartmentNames();

                requireActivity().runOnUiThread(() -> {
                    if (departmentNames != null && !departmentNames.isEmpty()) {
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, departmentNames);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerTeachers.setAdapter(spinnerAdapter);
                    } else {
                        showToast("Không có khoa nào trong danh sách!");
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> showToast("Lỗi tải danh sách khoa!")); // Hiển thị lỗi
                Log.e("SpinnerData", "Lỗi: ", e);
            }
        }).start();
    }

    private int getDepartmentIdByName(String departmentName) {
        if (departmentName == null || departmentName.isEmpty()) {
            Log.e("DepartmentId", "Tên khoa không hợp lệ!");
            return -1; // Trả về giá trị lỗi nếu tên khoa không hợp lệ
        }
        return appDatabase.departmentDao().getDepartmentIdByName(departmentName);
    }

    private void clearInputFields() {
        eTxtEmail.setText("");
        eTxtFirstName.setText("");
        eTxtLastName.setText("");
        if (spinnerTeachers.getAdapter() != null) {
            spinnerTeachers.setSelection(0); // Đặt Spinner về vị trí đầu tiên
        }
    }

    public void onTeacherDelete(TeacherDTO teacher, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa giáo viên")
                .setMessage("Bạn có chắc chắn muốn xóa giáo viên "
                        + teacher.getTeacher().getFirstName() + " "
                        + teacher.getTeacher().getLastName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            // Xóa giáo viên khỏi cơ sở dữ liệu
                            appDatabase.teacherDao().deleteTeacher(teacher.getTeacher());

                            requireActivity().runOnUiThread(() -> {
                                if (teacherList != null && position < teacherList.size()) {
                                    teacherList.remove(position); // Xóa giáo viên khỏi danh sách
                                    adapter.notifyItemRemoved(position); // Cập nhật RecyclerView
                                    showToast("Đã xóa giáo viên: " + teacher.getTeacher().getFirstName());
                                } else {
                                    showToast("Xóa thất bại do danh sách bị lỗi!");
                                }
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() -> showToast("Lỗi khi xóa giáo viên!"));
                            Log.e("DeleteTeacher", "Lỗi khi xóa giáo viên: ", e);
                        }
                    }).start();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()) // Xử lý nút hủy
                .setCancelable(false)
                .show();
    }

    private void showToast(String message) {
        // Phương thức hiển thị Toast đơn giản, dễ tái sử dụng
        requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }
}