package com.btl.login.fragments;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.adapter.MultipleStudentsAdapter;
import com.btl.login.adapter.StudentAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.StudentDTO;
import com.btl.login.entities.Student;
import com.btl.login.interfaces.OnStudentActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.text.TextWatcher;
import android.text.Editable;

public class StudentFragment extends Fragment implements OnStudentActionListener {

    private final List<StudentDTO> studentList = new ArrayList<>();
    private final List<StudentDTO> multipleStudentsList = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private RecyclerView recyclerViewStudents, recyclerViewAddMultipleStudents;
    private Spinner spinnerClasses, spinnerSearchCategory;
    private EditText eTxtFirstName, eTxtLastName, eTxtEmail, eTxtStudentCount, editTextSearch;
    private Button btnAddStudent, btnEditStudent, btnAddMultipleStudents, btnSaveMultipleStudents, btnSearchStudent;
    private ProgressBar progressBarLoading;
    private StudentAdapter adapter;
    private AppDatabase appDatabase;
    private String selectedClassName;
    private StudentDTO selectedStudent;
    private MultipleStudentsAdapter multipleStudentsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_student, container, false);

        initViews(view);
        setupRecyclerView();
        loadSpinnerData();
        setupSearchSpinner(); // Khởi tạo `Spinner` tìm kiếm

        spinnerClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClassName = parent.getItemAtPosition(position).toString();
                if (selectedClassName == null || selectedClassName.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng chọn lớp học hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedClassName = null;
            }
        });

        loadStudentsFromDatabase(); // Tải danh sách sinh viên

        btnAddStudent.setOnClickListener(v -> handleAddStudent());
        btnEditStudent.setOnClickListener(v -> handleEditStudent());
        btnAddMultipleStudents.setOnClickListener(v -> handleAddMultipleStudents());
        btnSaveMultipleStudents.setOnClickListener(v -> handleSaveMultipleStudents());

        btnSearchStudent.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập thông tin cần tìm!", Toast.LENGTH_SHORT).show();
                return;
            }
            searchStudents(query);
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    adapter.resetList(); // Khôi phục danh sách gốc
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void initViews(View view) {
        recyclerViewStudents = view.findViewById(R.id.recyclerViewStudents);
        spinnerClasses = view.findViewById(R.id.spinnerClass);
        eTxtFirstName = view.findViewById(R.id.eTxtFirstName);
        eTxtLastName = view.findViewById(R.id.eTxtLastName);
        eTxtEmail = view.findViewById(R.id.eTxtEmail);
        btnAddStudent = view.findViewById(R.id.btnAddSingleStudent);
        btnEditStudent = view.findViewById(R.id.btnEditSingleStudent);
        appDatabase = AppDatabase.getDatabase(requireContext());
        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        eTxtStudentCount = view.findViewById(R.id.eTxtStudentCount);
        recyclerViewAddMultipleStudents = view.findViewById(R.id.recyclerViewAddMultipleStudents);
        btnAddMultipleStudents = view.findViewById(R.id.btnAddMultipleStudents);
        btnSaveMultipleStudents = view.findViewById(R.id.btnSaveMultipleStudents);

        editTextSearch = view.findViewById(R.id.editTextSearch);
        spinnerSearchCategory = view.findViewById(R.id.spinnerSearchCategory);
        btnSearchStudent = view.findViewById(R.id.btnSearchStudent);
    }

    private void setupRecyclerView() {
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new StudentAdapter(requireContext(), studentList, this);
        recyclerViewStudents.setAdapter(adapter);

        recyclerViewAddMultipleStudents.setLayoutManager(new LinearLayoutManager(requireContext())); // ✅ Đặt LayoutManager trước khi gán Adapter
        multipleStudentsAdapter = new MultipleStudentsAdapter(multipleStudentsList);
        recyclerViewAddMultipleStudents.setAdapter(multipleStudentsAdapter);
    }

    private void setupSearchSpinner() {
        String[] searchCategories = {"Họ", "Tên", "Lớp"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, searchCategories);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearchCategory.setAdapter(adapterSpinner);
    }

    private void searchStudents(String query) {
        if (query.isEmpty()) {
            adapter.resetList();
            return;
        }

        List<StudentDTO> filteredList = new ArrayList<>();

        for (StudentDTO student : adapter.getStudentListFull()) {
            if (student == null) continue;

            switch (spinnerSearchCategory.getSelectedItem().toString()) {
                case "Họ":
                    if (student.getFirstName() != null && student.getFirstName().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(student);
                    }
                    break;
                case "Tên":
                    if (student.getLastName() != null && student.getLastName().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(student);
                    }
                    break;
                case "Lớp":
                    if (student.getClassName() != null && student.getClassName().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(student);
                    }
                    break;
            }
        }

        adapter.updateList(filteredList);
    }

    private void loadSpinnerData() {
        executorService.execute(() -> {
            List<String> classNames = appDatabase.studentClassDao().getAllClassNames();
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, classNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClasses.setAdapter(spinnerAdapter);
            });
        });
    }

    private void loadStudentsFromDatabase() {
        requireActivity().runOnUiThread(() -> progressBarLoading.setVisibility(View.VISIBLE));

        executorService.execute(() -> {
            List<StudentDTO> updatedList = appDatabase.studentDao().getAllStudentsWithClassName();

            requireActivity().runOnUiThread(() -> {
                if (updatedList == null || updatedList.isEmpty()) {
                    Toast.makeText(requireContext(), "Không có sinh viên nào trong cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.updateFullList(updatedList); // Gọi từ Adapter thay vì cập nhật `studentListFull` từ `Fragment`
                }

                progressBarLoading.setVisibility(View.GONE);
            });
        });
    }

    @Override
    public void onStudentSelect(StudentDTO student) {
        if (student == null) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy sinh viên!", Toast.LENGTH_SHORT).show();
            return; // Không tiếp tục nếu student bị null
        }

        selectedStudent = student; // Lưu lại sinh viên được chọn
        eTxtFirstName.setText(student.getFirstName());
        eTxtLastName.setText(student.getLastName());
        eTxtEmail.setText(student.getEmail());

        // Cập nhật lớp học trong Spinner
        ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) spinnerClasses.getAdapter();
        if (spinnerAdapter != null) {
            int spinnerPosition = spinnerAdapter.getPosition(student.getClassName());
            if (spinnerPosition >= 0) {
                spinnerClasses.setSelection(spinnerPosition);
            }
        }
    }
    private void handleAddStudent() {
        executorService.execute(() -> {
            String firstName = eTxtFirstName.getText().toString().trim();
            String lastName = eTxtLastName.getText().toString().trim();
            String email = eTxtEmail.getText().toString().trim();
            int classId = appDatabase.studentClassDao().getClassIdByName(selectedClassName);

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || classId <= 0) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin hợp lệ!", Toast.LENGTH_SHORT).show());
                return;
            }

            int exists = appDatabase.studentDao().checkEmailExists(email);
            requireActivity().runOnUiThread(() -> {
                if (exists > 0) {
                    Toast.makeText(requireContext(), "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                } else {
                    addStudent(firstName, lastName, email, classId);
                }
            });
        });
    }

    private void addStudent(String firstName, String lastName, String email, int classId) {
        executorService.execute(() -> {
            Student newStudent = new Student(firstName, lastName, email, classId);
            appDatabase.studentDao().addStudents(newStudent);

            requireActivity().runOnUiThread(() -> {
                loadStudentsFromDatabase();
                Toast.makeText(requireContext(), "Đã thêm sinh viên!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            });
        });
    }

    private void handleEditStudent() {
        if (selectedStudent == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn một sinh viên để chỉnh sửa!", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            String firstName = eTxtFirstName.getText().toString().trim();
            String lastName = eTxtLastName.getText().toString().trim();
            String email = eTxtEmail.getText().toString().trim();
            int classId = appDatabase.studentClassDao().getClassIdByName(selectedClassName);

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || classId <= 0) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin hợp lệ!", Toast.LENGTH_SHORT).show());
                return;
            }

            int exists = appDatabase.studentDao().checkEmailExists(email);
            requireActivity().runOnUiThread(() -> {
                if (exists > 0 && !selectedStudent.getEmail().equals(email)) {
                    Toast.makeText(requireContext(), "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                } else {
                    updateStudent(firstName, lastName, email, classId);
                }
            });
        });
    }

    private void updateStudent(String firstName, String lastName, String email, int classId) {
        executorService.execute(() -> { // Chạy trên Background Thread
            Student updatedStudent = new Student(firstName, lastName, email, classId);
            updatedStudent.setId(selectedStudent.getId());
            appDatabase.studentDao().updateStudent(updatedStudent);

            List<StudentDTO> updatedList = appDatabase.studentDao().getAllStudentsWithClassName(); // Truy vấn trên Background Thread

            requireActivity().runOnUiThread(() -> {
                adapter.updateFullList(updatedList); // Cập nhật danh sách gốc
                loadStudentsFromDatabase(); // Làm mới danh sách hiển thị
                Toast.makeText(requireContext(), "Thông tin sinh viên đã được cập nhật!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            });
        });
    }

    @Override
    public void onStudentDelete(StudentDTO student, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa sinh viên")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + student.getFirstName() + " " + student.getLastName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> executorService.execute(() -> {
                    appDatabase.studentDao().deleteStudentById(student.getId());

                    requireActivity().runOnUiThread(() -> {
                        adapter.removeStudent(student); // Gọi phương thức xóa từ Adapter
                        adapter.notifyDataSetChanged();

                        Toast.makeText(requireContext(), "Đã xóa sinh viên!", Toast.LENGTH_SHORT).show();
                    });
                }))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void handleAddMultipleStudents() {
        String countText = eTxtStudentCount.getText().toString().trim();
        if (countText.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập số lượng sinh viên!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int studentCount = Integer.parseInt(countText);
            if (studentCount <= 0) {
                Toast.makeText(requireContext(), "Số lượng phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo danh sách sinh viên rỗng
            multipleStudentsList.clear();
            for (int i = 0; i < studentCount; i++) {
                multipleStudentsList.add(new StudentDTO()); // Thêm sinh viên rỗng
            }

            if (multipleStudentsAdapter == null) {
                multipleStudentsAdapter = new MultipleStudentsAdapter(multipleStudentsList);
                recyclerViewAddMultipleStudents.setAdapter(multipleStudentsAdapter);
            }

            recyclerViewAddMultipleStudents.setVisibility(View.VISIBLE); // Hiển thị danh sách nhập liệu
            btnSaveMultipleStudents.setVisibility(View.VISIBLE); // Hiển thị nút lưu
            multipleStudentsAdapter.notifyDataSetChanged();
            recyclerViewAddMultipleStudents.scrollToPosition(0); // Cuộn đến đầu danh sách nhập liệu

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Vui lòng nhập một số hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSaveMultipleStudents() {
        executorService.execute(() -> {
            boolean allValid = true; // Kiểm tra toàn bộ danh sách có hợp lệ

            // Kiểm tra từng sinh viên trong danh sách
            for (StudentDTO student : multipleStudentsList) {
                // Kiểm tra email trùng lặp
                int exists = appDatabase.studentDao().checkEmailExists(student.getEmail());
                if (exists > 0) { // Email đã tồn tại
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Email \"" + student.getEmail() + "\" đã tồn tại! Vui lòng sửa lại.", Toast.LENGTH_SHORT).show());
                    allValid = false;
                    break; // Ngừng kiểm tra
                }

                // Kiểm tra tên lớp có tồn tại không
                int classId = appDatabase.studentClassDao().getClassIdByName(student.getClassName());
                if (classId <= 0) { // Lớp không tồn tại
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Lớp \"" + student.getClassName() + "\" không tồn tại! Vui lòng sửa lại.", Toast.LENGTH_SHORT).show());
                    allValid = false;
                    break; // Ngừng kiểm tra
                }
            }

            // Nếu tất cả dữ liệu hợp lệ, bắt đầu lưu vào cơ sở dữ liệu
            if (allValid) {
                try {
                    for (StudentDTO student : multipleStudentsList) {
                        int classId = appDatabase.studentClassDao().getClassIdByName(student.getClassName());
                        Student newStudent = new Student(student.getFirstName(), student.getLastName(), student.getEmail(), classId);
                        appDatabase.studentDao().addStudents(newStudent); // Lưu sinh viên
                    }

                    // Làm mới giao diện sau khi lưu thành công
                    requireActivity().runOnUiThread(() -> {
                        loadStudentsFromDatabase(); // Làm mới danh sách chính
                        recyclerViewAddMultipleStudents.setVisibility(View.GONE); // Ẩn danh sách nhập liệu
                        btnSaveMultipleStudents.setVisibility(View.GONE); // Ẩn nút lưu
                        Toast.makeText(requireContext(), "Đã lưu tất cả sinh viên hợp lệ!", Toast.LENGTH_SHORT).show();
                    });
                } catch (SQLiteConstraintException e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Lỗi UNIQUE: Email trùng lặp! Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void clearInputFields() {
        eTxtFirstName.setText("");
        eTxtLastName.setText("");
        eTxtEmail.setText("");
        if (spinnerClasses.getAdapter() != null && spinnerClasses.getAdapter().getCount() > 0) {
            spinnerClasses.setSelection(0);
        }
    }
}