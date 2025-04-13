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
import com.btl.login.adapter.SubjectAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.SubjectDTO;
import com.btl.login.entities.Subject;
import com.btl.login.interfaces.OnSubjectActionListener;

import java.util.Arrays;
import java.util.List;

public class SubjectFragment extends Fragment implements OnSubjectActionListener {

    private RecyclerView recyclerSubjects;
    private EditText eTxtSubjectName, eTxtCreditNumber;
    private Spinner spinnerCreditNumbers;
    private Button btnAddSubject, btnEditSubject;
    private ProgressBar progressBarLoading;
    private TextView emptyTextView;
    private SubjectAdapter adapter;
    private AppDatabase appDatabase;
    private List<SubjectDTO> subjectList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_subject, container, false);

        // Liên kết các thành phần giao diện
        recyclerSubjects = view.findViewById(R.id.recycler_subjects); // Sử dụng RecyclerView
        eTxtSubjectName = view.findViewById(R.id.eTxtSubjectName); // Tên môn học
        btnAddSubject = view.findViewById(R.id.btnAdd); // Nút thêm môn học
        btnEditSubject = view.findViewById(R.id.btnEdit); // Nút chỉnh sửa môn học
        spinnerCreditNumbers = view.findViewById(R.id.spinnerCreditNumbers); // Spinner cho số tín chỉ
        progressBarLoading = view.findViewById(R.id.progressBarLoading); // ProgressBar khi tải
        emptyTextView = view.findViewById(R.id.emptyTextView); // Hiển thị nếu danh sách trống

        // Khởi tạo Room Database
        appDatabase = AppDatabase.getDatabase(requireContext());

        // Cấu hình RecyclerView cho danh sách môn học
        recyclerSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Thiết lập Spinner cho số tín chỉ
        setupSpinner();

        // Tải danh sách môn học từ cơ sở dữ liệu
        loadSubjectsFromDatabase();

        // Xử lý sự kiện thêm môn học
        btnAddSubject.setOnClickListener(v -> handleAddSubject());

        // Xử lý sự kiện chỉnh sửa môn học
        btnEditSubject.setOnClickListener(v -> handleEditSubject());

        return view;
    }
    private void showToast(String message) {
        // Phương thức hiển thị Toast đơn giản, dễ tái sử dụng
        requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setupSpinner() {
        // Danh sách các số tín chỉ hợp lệ
        List<Double> creditOptions = Arrays.asList(1.0, 1.5, 2.0, 2.5, 3.0, 4.0);

        // Tạo ArrayAdapter với danh sách số tín chỉ
        ArrayAdapter<Double> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, creditOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gắn Adapter cho Spinner
        spinnerCreditNumbers.setAdapter(adapter);
    }

    @Override
    public void onSubjectClick(SubjectDTO subjectDTO) {
        if (subjectDTO == null || subjectDTO.getSubject() == null) {
            Log.e("SubjectClickError", "Dữ liệu môn học bị null hoặc không hợp lệ!");
            showToast("Dữ liệu môn học không hợp lệ!");
            return; // Dừng xử lý nếu dữ liệu không hợp lệ
        }

        // Gán dữ liệu vào trường nhập liệu
        eTxtSubjectName.setText(subjectDTO.getSubject().getSubjectName());

        // Cập nhật Spinner nếu số tín chỉ không null
        Double creditNumber = subjectDTO.getSubject().getCreditNumber();
        if (creditNumber != null) {
            // Kiểm tra và đặt vị trí cho Spinner
            if (spinnerCreditNumbers.getAdapter() != null) {
                int position = ((ArrayAdapter<Double>) spinnerCreditNumbers.getAdapter()).getPosition(creditNumber);
                if (position >= 0) {
                    spinnerCreditNumbers.setSelection(position);
                } else {
                    Log.e("SpinnerError", "Số tín chỉ không tồn tại trong danh sách Spinner!");
                }
            } else {
                Log.e("SpinnerError", "Adapter của Spinner chưa được khởi tạo!");
                showToast("Danh sách số tín chỉ chưa được tải, vui lòng thử lại!");
            }
        }

        // Gán dữ liệu môn học được chọn vào nút chỉnh sửa
        btnEditSubject.setTag(subjectDTO);
    }
    private boolean isInputValid() {
        String subjectName = eTxtSubjectName.getText().toString().trim();
        Double selectedCredit = (Double) spinnerCreditNumbers.getSelectedItem();

        // Kiểm tra tên môn học
        if (subjectName.isEmpty()) {
            showToast("Tên môn học không được để trống!");
            return false;
        }

        // Kiểm tra số tín chỉ
        if (selectedCredit == null) {
            showToast("Vui lòng chọn số tín chỉ hợp lệ!");
            return false;
        }

        return true; // Dữ liệu hợp lệ
    }
    private boolean isSubjectNameValid(String subjectName) {
        if (subjectName.isEmpty()) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Tên môn học không được để trống!", Toast.LENGTH_SHORT).show());
            return false;
        }
        return true;
    }

    private void handleAddSubject() {
        new Thread(() -> {
            try {
                // Kiểm tra đầu vào
                if (!isInputValid()) {
                    return; // Dừng xử lý nếu dữ liệu không hợp lệ
                }

                String subjectName = eTxtSubjectName.getText().toString().trim();

                // Kiểm tra tên môn học đã tồn tại trong cơ sở dữ liệu
                int subjectExists = appDatabase.subjectDao().checkSubjectExists(subjectName);
                requireActivity().runOnUiThread(() -> {
                    if (subjectExists > 0) {
                        showToast("Tên môn học đã tồn tại!"); // Hiển thị lỗi nếu tên môn học đã tồn tại
                    } else {
                        addSubject(); // Gọi phương thức thêm môn học mới
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Đã xảy ra lỗi khi kiểm tra tên môn học!"));
                Log.e("HandleAddSubject", "Lỗi kiểm tra tên môn học: ", e);
            }
        }).start();
    }
    private void addSubject() {
        new Thread(() -> {
            try {
                // Lấy dữ liệu từ các trường nhập liệu
                String subjectName = eTxtSubjectName.getText().toString().trim();
                Double creditNumber = (Double) spinnerCreditNumbers.getSelectedItem();

                if (creditNumber == null || creditNumber <= 0) {
                    showToast("Số tín chỉ không hợp lệ, vui lòng thử lại!");
                    return;
                }

                // Tạo đối tượng Subject và thêm vào cơ sở dữ liệu
                Subject newSubject = new Subject(subjectName, creditNumber);
                appDatabase.subjectDao().addSubject(newSubject);

                // Cập nhật giao diện sau khi thêm thành công
                requireActivity().runOnUiThread(() -> {
                    loadSubjectsFromDatabase(); // Tải lại danh sách môn học
                    showToast("Đã thêm môn học mới!"); // Hiển thị thông báo thành công
                    clearInputFields(); // Dọn dẹp các trường nhập liệu
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Lỗi khi thêm môn học!"));
                Log.e("AddSubject", "Lỗi thêm môn học: ", e);
            }
        }).start();
    }
    public void onSubjectTagForEdit(SubjectDTO subjectDTO) {
        // Gán thông tin môn học vào nút btnEditSubject
        btnEditSubject.setTag(subjectDTO);
    }
    private void handleEditSubject() {
        // Lấy đối tượng môn học được chọn từ Tag của btnEditSubject
        SubjectDTO selectedSubject = (SubjectDTO) btnEditSubject.getTag();
        if (selectedSubject != null) {
            new Thread(() -> {
                try {
                    // Lấy tên môn học từ trường nhập liệu
                    String subjectName = eTxtSubjectName.getText().toString().trim();

                    // Kiểm tra tính hợp lệ của tên môn học
                    if (!isSubjectNameValid(subjectName)) {
                        return; // Dừng xử lý nếu tên môn học không hợp lệ
                    }

                    // Kiểm tra tên môn học trùng (nhưng không phải tên của môn đang chỉnh sửa)
                    int subjectExists = appDatabase.subjectDao().checkSubjectExists(subjectName);
                    requireActivity().runOnUiThread(() -> {
                        if (subjectExists > 0 && !subjectName.equals(selectedSubject.getSubject().getSubjectName())) {
                            showToast("Tên môn học đã được sử dụng!"); // Hiển thị thông báo lỗi
                        } else {
                            editSubject(selectedSubject); // Gọi phương thức chỉnh sửa môn học
                        }
                    });
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() ->
                            showToast("Đã xảy ra lỗi khi kiểm tra tên môn học!"));
                    Log.e("HandleEditSubject", "Lỗi kiểm tra tên môn học: ", e);
                }
            }).start();
        } else {
            // Hiển thị thông báo nếu không có môn học nào được chọn
            showToast("Vui lòng chọn môn học để chỉnh sửa!");
        }
    }

    private void editSubject(SubjectDTO selectedSubject) {
        new Thread(() -> {
            try {
                // Lấy dữ liệu môn học được chỉnh sửa
                Subject updatedSubject = selectedSubject.getSubject();
                updatedSubject.setSubjectName(eTxtSubjectName.getText().toString().trim());

                // Lấy số tín chỉ từ Spinner
                Double selectedCreditNumber = (Double) spinnerCreditNumbers.getSelectedItem();
                if (selectedCreditNumber == null || selectedCreditNumber <= 0) {
                    showToast("Vui lòng chọn số tín chỉ hợp lệ!");
                    return;
                }
                updatedSubject.setCreditNumber(selectedCreditNumber);

                // Cập nhật thông tin môn học trong cơ sở dữ liệu
                appDatabase.subjectDao().updateSubject(updatedSubject);

                // Cập nhật giao diện sau khi chỉnh sửa thành công
                requireActivity().runOnUiThread(() -> {
                    loadSubjectsFromDatabase(); // Tải lại danh sách môn học
                    showToast("Thông tin môn học đã được cập nhật!"); // Hiển thị thông báo thành công
                    clearInputFields(); // Dọn dẹp giao diện
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showToast("Lỗi khi chỉnh sửa môn học!"));
                Log.e("EditSubject", "Lỗi: ", e);
            }
        }).start();
    }

    private void loadSubjectsFromDatabase() {
        requireActivity().runOnUiThread(() -> progressBarLoading.setVisibility(View.VISIBLE)); // Hiển thị ProgressBar

        new Thread(() -> {
            try {
                // Tải danh sách môn học từ cơ sở dữ liệu
                subjectList = appDatabase.subjectDao().getSubjectsWithCreditNumber();

                requireActivity().runOnUiThread(() -> {
                    if (subjectList != null && !subjectList.isEmpty()) {
                        adapter = new SubjectAdapter(requireContext(), subjectList, this);
                        recyclerSubjects.setLayoutManager(new LinearLayoutManager(requireContext())); // Cấu hình RecyclerView
                        recyclerSubjects.setAdapter(adapter); // Gắn adapter cho RecyclerView
                    } else {
                        showToast("Không có môn học nào trong danh sách!"); // Hiển thị thông báo nếu danh sách trống
                    }
                    progressBarLoading.setVisibility(View.GONE); // Ẩn ProgressBar
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showToast("Lỗi tải danh sách môn học!"); // Hiển thị lỗi
                    progressBarLoading.setVisibility(View.GONE); // Ẩn ProgressBar
                });
                Log.e("LoadSubjects", "Lỗi: ", e);
            }
        }).start();
    }
    private void loadSpinnerData() {
        new Thread(() -> {
            try {
                // Danh sách các số tín chỉ hợp lệ từ cơ sở dữ liệu hoặc danh sách tĩnh
                List<Double> creditNumbers = Arrays.asList(1.0, 2.5, 3.0, 4.0);

                requireActivity().runOnUiThread(() -> {
                    if (creditNumbers != null && !creditNumbers.isEmpty()) {
                        ArrayAdapter<Double> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, creditNumbers);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Định dạng hiển thị Dropdown
                        spinnerCreditNumbers.setAdapter(spinnerAdapter); // Gắn adapter cho Spinner
                    } else {
                        showToast("Không có số tín chỉ nào trong danh sách!"); // Hiển thị thông báo nếu danh sách trống
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> showToast("Lỗi tải danh sách số tín chỉ!")); // Hiển thị lỗi
                Log.e("SpinnerData", "Lỗi: ", e);
            }
        }).start();
    }

    private void clearInputFields() {
        eTxtSubjectName.setText(""); // Xóa nội dung trường nhập tên môn học
        if (spinnerCreditNumbers.getAdapter() != null) {
            spinnerCreditNumbers.setSelection(0); // Đặt Spinner về vị trí đầu tiên
        }
    }

    public void onSubjectDelete(SubjectDTO subjectDTO, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa môn học")
                .setMessage("Bạn có chắc chắn muốn xóa môn học " + subjectDTO.getSubject().getSubjectName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            // Xóa môn học khỏi cơ sở dữ liệu
                            appDatabase.subjectDao().deleteSubject(subjectDTO.getSubject());

                            requireActivity().runOnUiThread(() -> {
                                if (subjectList != null && position < subjectList.size()) {
                                    subjectList.remove(position); // Xóa môn khỏi danh sách
                                    adapter.notifyItemRemoved(position); // Cập nhật RecyclerView
                                    showToast("Đã xóa môn học: " + subjectDTO.getSubject().getSubjectName());
                                } else {
                                    showToast("Xóa thất bại do danh sách bị lỗi!");
                                }
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() -> showToast("Lỗi khi xóa môn học!"));
                            Log.e("DeleteSubject", "Lỗi khi xóa môn học: ", e);
                        }
                    }).start();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()) // Xử lý nút hủy
                .setCancelable(false)
                .show();
    }
}