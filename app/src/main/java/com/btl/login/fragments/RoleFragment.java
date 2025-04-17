package com.btl.login.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.adapter.RoleAdapter;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.dto.TeacherInfo;
import com.btl.login.entities.Teacher;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class RoleFragment extends Fragment {
    private EditText eTxtEmail, eTxtPassword, eTxtFirstName, eTxtLastName;
    private Spinner spinnerRoles;
    private Button btnAdd, btnEdit;
    private RecyclerView recyclerViewRoles;
    private RoleAdapter adapter;
    private List<Map<String, Object>> userList;
    private AppDatabase appDatabase;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayAdapter<String> roleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo FirebaseAuth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_role, container, false);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ giao diện
        eTxtEmail = view.findViewById(R.id.eTxtEmail);
        eTxtPassword = view.findViewById(R.id.eTxtPassword);
        eTxtFirstName = view.findViewById(R.id.eTxtFirstName);
        eTxtLastName = view.findViewById(R.id.eTxtLastName);
        spinnerRoles = view.findViewById(R.id.spinnerRoles);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnEdit = view.findViewById(R.id.btnEdit);
        recyclerViewRoles = view.findViewById(R.id.recyclerViewRoles);

        // Kết nối SQLite
        appDatabase = AppDatabase.getDatabase(requireContext());

        // Cài đặt RecyclerView
        recyclerViewRoles.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tạo danh sách Role cho Spinner
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.roles_array,
                android.R.layout.simple_spinner_item
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoles.setAdapter(roleAdapter);

        // Khởi tạo danh sách người dùng và Adapter
        userList = new ArrayList<>();
        adapter = new RoleAdapter(this, userList);
        recyclerViewRoles.setAdapter(adapter);

        // Tải dữ liệu từ SQLite
        loadUsersFromFirebase();

        // Xử lý nút Thêm
        btnAdd.setOnClickListener(v -> addUserToFirestore());

        // Xử lý nút Sửa
        btnEdit.setOnClickListener(v -> updateUserRole());

        return view;
    }

    private void addUserToFirestore() {
        String email = eTxtEmail.getText().toString().trim();
        String password = eTxtPassword.getText().toString().trim();
        String firstName = eTxtFirstName.getText().toString().trim();
        String lastName = eTxtLastName.getText().toString().trim();
        String role = spinnerRoles.getSelectedItem().toString();

        // Kiểm tra đầu vào
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || role.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mã hóa mật khẩu
        String hashedPassword = hashPassword(password);

        // URL ảnh mặc định
        String defaultImageUrl = "https://res.cloudinary.com/dn84ltxow/image/upload/v1744003073/qpkpolc8gufh2pjnpdsx.jpg";

        // Tạo tài khoản Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Lưu vai trò vào Firestore
                        DocumentReference userRef = db.collection("users").document(user.getUid());
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);
                        userData.put("password", hashedPassword);
                        userData.put("role", role);
                        userData.put("imageUrl", defaultImageUrl); // Lưu ảnh mặc định

                        userRef.set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Người dùng đã lưu: " + email);
                                    loadUsersFromFirebase(); // Cập nhật danh sách
                                });

                        // Chỉ lưu vào SQLite nếu là giáo viên
                        saveTeacherToSQLite(email, firstName, lastName);

                        Toast.makeText(getContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "Email đã được sử dụng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadUsersFromFirebase() {
        db.collection("users").get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Log.d("Firestore", "Tổng số tài liệu: " + querySnapshot.size());

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        List<Map<String, Object>> tempUserList = Collections.synchronizedList(new ArrayList<>());

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String email = document.getString("email");
                            String role = document.getString("role");
                            String password = document.getString("password");
                            String profileImageUrl = document.getString("profileImageUrl");

                            if (email != null && role != null) {
                                executor.execute(() -> { // 🔥 Truy vấn SQLite trong Background Thread
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", email);
                                    user.put("role", role);
                                    user.put("password", password);
                                    user.put("profileImageUrl", profileImageUrl);

                                    TeacherInfo teacherData = appDatabase.teacherDao().getTeacherInfoByEmail(email);
                                    user.put("firstName", (teacherData != null) ? teacherData.firstName : "Không có");
                                    user.put("lastName", (teacherData != null) ? teacherData.lastName : "Không có");

                                    tempUserList.add(user);
                                });
                            }
                        }

                        executor.shutdown();
                        try {
                            executor.awaitTermination(3, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            Log.e("Thread Error", "Lỗi khi chờ ExecutorService kết thúc: " + e.getMessage());
                        }

                        requireActivity().runOnUiThread(() -> {
                            userList.clear();
                            userList.addAll(tempUserList);
                            adapter.notifyDataSetChanged();
                        });
                    } else {
                        Log.e("Firestore", "Không tìm thấy tài liệu nào trên Firestore.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi truy vấn Firestore: " + e.getMessage()));
    }
    public void showUserData(Map<String, Object> user) {
        eTxtEmail.setText(user.get("email").toString());
        eTxtFirstName.setText(user.get("firstName").toString());
        eTxtLastName.setText(user.get("lastName").toString());

        // 🔥 Kiểm tra nếu roleAdapter đã khởi tạo trước khi sử dụng
        if (roleAdapter != null) {
            String role = user.get("role").toString();
            int spinnerPosition = roleAdapter.getPosition(role);
            spinnerRoles.setSelection(spinnerPosition);
        } else {
            Log.e("Spinner", "roleAdapter chưa khởi tạo!");
        }
    }
    /** 📌 Cập nhật mật khẩu & vai trò */
    private void updateUserRole() {
        String email = eTxtEmail.getText().toString().trim(); // 🔥 Lấy email từ EditText
        String newRole = spinnerRoles.getSelectedItem().toString(); // 🔥 Lấy vai trò từ Spinner

        if (email.isEmpty() || newRole.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 Tìm tài liệu có email trong Firestore để lấy UID
        db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0); // 🔥 Lấy tài liệu đầu tiên
                        String userUID = document.getId(); // 🔥 Lấy UID của tài liệu

                        // 🔥 Cập nhật vai trò trên Firestore
                        db.collection("users").document(userUID).update("role", newRole)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Vai trò đã được cập nhật!", Toast.LENGTH_SHORT).show();
                                    loadUsersFromFirebase(); // 🔥 Cập nhật danh sách sau khi sửa
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Lỗi khi cập nhật vai trò trên Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy người dùng có email này!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tìm kiếm người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /** 📌 Mã hóa mật khẩu */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }
    public void confirmDeleteUser(String email) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa người dùng này? Hành động này không thể hoàn tác!")
                .setPositiveButton("Xác nhận", (dialog, which) -> deleteUser(email)) // Gọi hàm xóa
                .setNegativeButton("Hủy", null) // Không làm gì nếu chọn Hủy
                .show();
    }
    public void deleteUser(String email) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && currentUser.getEmail().equals(email)) {
            final String userIdFinal = currentUser.getUid();

            if (userIdFinal == null) {
                Log.e("Auth", "Lỗi: userIdFinal bị null, không thể xóa trên Firestore!");
                return;
            }

            ExecutorService executor = Executors.newFixedThreadPool(3);
            executor.execute(() -> {
                int deletedRowsTeacher = appDatabase.teacherDao().deleteTeacherByEmail(email);

                requireActivity().runOnUiThread(() -> {
                    if (deletedRowsTeacher > 0) {
                        Log.d("SQLite", "Xóa thành công trên SQLite: " + email);
                    }

                    db.collection("users").document(userIdFinal).delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Xóa thành công trên Firestore: " + userIdFinal);

                                currentUser.delete()
                                        .addOnSuccessListener(aVoid2 -> {
                                            Log.d("Auth", "Xóa tài khoản thành công!");

                                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                requireActivity().runOnUiThread(() -> {
                                                    Toast.makeText(getContext(), "Xóa thành công trên tất cả hệ thống!", Toast.LENGTH_SHORT).show();
                                                    loadUsersFromFirebase(); // 🔥 Gọi Firebase để cập nhật danh sách
                                                });
                                            }, 500);  // Đợi 500ms để Firestore hoàn tất xóa
                                        })
                                        .addOnFailureListener(e -> Log.e("Auth", "Lỗi khi xóa khỏi Authentication: " + e.getMessage()));
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi xóa trên Firestore: " + e.getMessage()));
                });
            });

        } else {
            Toast.makeText(getContext(), "Không tìm thấy người dùng trong Authentication!", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveTeacherToSQLite(String email, String firstName, String lastName) {
        Teacher teacher = new Teacher(firstName, lastName, email, 1);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(requireContext());
            db.teacherDao().addTeachers(teacher); // Thêm Teacher vào SQLite
            Log.d("SQLite", "Teacher saved to database: " + email);
        }).start();
    }
}