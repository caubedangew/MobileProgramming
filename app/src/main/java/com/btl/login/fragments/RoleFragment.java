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
import androidx.fragment.app.Fragment;

import com.btl.login.R;
import com.btl.login.adapter.RoleAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleFragment extends Fragment {
    private FirebaseFirestore db;
    private EditText eTxtEmail, eTxtPassword;
    private Spinner spinnerRoles;
    private Button btnAdd, btnEdit;
    private ListView listViewRoles;
    private RoleAdapter adapter;
    private List<Map<String, Object>> userList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_role, container, false);

        // Ánh xạ giao diện
        eTxtEmail = view.findViewById(R.id.eTxtEmail);
        eTxtPassword = view.findViewById(R.id.eTxtPassword);
        spinnerRoles = view.findViewById(R.id.spinnerRoles);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnEdit = view.findViewById(R.id.btnEdit);
        listViewRoles = view.findViewById(R.id.listViewRoles);

        db = FirebaseFirestore.getInstance();

        // Tạo danh sách Role cho Spinner
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.roles_array,
                android.R.layout.simple_spinner_item
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoles.setAdapter(roleAdapter);

        // Danh sách người dùng
        userList = new ArrayList<>();
        adapter = new RoleAdapter(getContext(), userList);
        listViewRoles.setAdapter(adapter);

        // Hiển thị danh sách người dùng từ Firestore
        loadUsersFromFirestore();

        // Xử lý click vào ListView để chỉnh sửa
        listViewRoles.setOnItemClickListener((parent, view1, position, id) -> {
            Map<String, Object> selectedUser = userList.get(position);
            eTxtEmail.setText((String) selectedUser.get("email"));
            eTxtPassword.setText((String) selectedUser.get("password")); // Hiển thị mật khẩu
            spinnerRoles.setSelection(roleAdapter.getPosition((String) selectedUser.get("role")));
        });

        // Nút thêm người dùng mới
        btnAdd.setOnClickListener(v -> addUserToFirestore());

        // Nút sửa thông tin người dùng
        btnEdit.setOnClickListener(v -> updateUserInFirestore());

        return view;
    }

    // Tải danh sách người dùng từ Firestore
    private void loadUsersFromFirestore() {
        db.collection("users").get()
                .addOnSuccessListener(querySnapshot -> {
                    userList.clear(); // Xóa danh sách cũ
                    if (!querySnapshot.isEmpty()) { // Kiểm tra nếu có dữ liệu
                        for (DocumentSnapshot document : querySnapshot) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", document.getString("email")); // Lấy email từ Firestore
                            user.put("role", document.getString("role"));   // Lấy role từ Firestore
                            userList.add(user);
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật danh sách
                    } else {
                        Toast.makeText(getContext(), "Không có người dùng trong hệ thống.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tải người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Thêm người dùng mới
    private void addUserToFirestore() {
        String email = eTxtEmail.getText().toString().trim();
        String password = eTxtPassword.getText().toString().trim();
        String role = spinnerRoles.getSelectedItem().toString();

        if (email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);
                        userData.put("password", password);
                        userData.put("role", role);

                        db.collection("users").add(userData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getContext(), "Người dùng đã được thêm!", Toast.LENGTH_SHORT).show();
                                    userList.add(userData);
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Lỗi khi thêm người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(getContext(), "Email này đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Cập nhật thông tin người dùng
    private void updateUserInFirestore() {
        String email = eTxtEmail.getText().toString().trim();
        String newPassword = eTxtPassword.getText().toString().trim();
        String newRole = spinnerRoles.getSelectedItem().toString();

        if (email.isEmpty() || newPassword.isEmpty() || newRole.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot) {
                            document.getReference().update("password", newPassword, "role", newRole)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show();
                                        loadUsersFromFirestore();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Lỗi khi cập nhật thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy người dùng với email này.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}