package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.btl.login.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class RoleAdapter extends ArrayAdapter<Map<String, Object>> {

    private final Context context;
    private final List<Map<String, Object>> userList;
    private final FirebaseFirestore db;

    public RoleAdapter(@NonNull Context context, List<Map<String, Object>> userList) {
        super(context, R.layout.custom_list_role, userList);
        this.context = context;
        this.userList = userList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_list_role, parent, false);
        }

        // Liên kết các thành phần giao diện
        TextView tvEmail = convertView.findViewById(R.id.tv_email);
        TextView tvRole = convertView.findViewById(R.id.tv_role);
        ImageView imgDelete = convertView.findViewById(R.id.img_delete);

        // Lấy thông tin người dùng từ danh sách
        Map<String, Object> user = userList.get(position);
        String email = (String) user.get("email");
        String role = (String) user.get("role");

        // Hiển thị thông tin
        tvEmail.setText(email);
        tvRole.setText(role);

        // Xử lý sự kiện xóa người dùng
        imgDelete.setOnClickListener(v -> {
            db.collection("users").whereEqualTo("email", email).get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Người dùng đã bị xóa!", Toast.LENGTH_SHORT).show();
                                        userList.remove(position);
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Lỗi khi xóa document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi truy vấn Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        return convertView;
    }
}