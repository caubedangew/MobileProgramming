package com.btl.login.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import java.util.List;
import java.util.Map;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.RoleViewHolder> {

    private final Fragment fragment; // Thay Context bằng Fragment để gọi phương thức từ RoleFragment
    private final List<Map<String, Object>> userList;

    // Constructor nhận danh sách người dùng và fragment
    public RoleAdapter(Fragment fragment, List<Map<String, Object>> userList) {
        this.fragment = fragment;
        this.userList = userList;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.custom_list_role, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        Map<String, Object> user = userList.get(position);

        // Lấy dữ liệu người dùng
        String email = (String) user.get("email");
        String password = (String) user.get("password");
        String firstName = (String) user.get("firstName");
        String lastName = (String) user.get("lastName");
        String role = (String) user.get("role");

        // Hiển thị thông tin lên ViewHolder
        holder.tvEmail.setText("Email: " + email);
        holder.tvPassword.setText("Mật khẩu: " + password);
        holder.tvFullName.setText("Họ và Tên: " + firstName + " " + lastName);
        holder.tvRole.setText("Vai trò: " + role);

        // 🔥 Xử lý sự kiện chọn người dùng để chỉnh sửa
        holder.itemView.setOnClickListener(v -> {
            if (fragment instanceof com.btl.login.fragments.RoleFragment) {
                ((com.btl.login.fragments.RoleFragment) fragment).showUserData(user); // Gửi dữ liệu lên EditText & Spinner
            } else {
                Toast.makeText(fragment.getContext(), "Lỗi: Không thể chỉnh sửa, Fragment không khả dụng.", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện xóa người dùng
        holder.imgDelete.setOnClickListener(v -> {
            if (fragment instanceof com.btl.login.fragments.RoleFragment) {
                ((com.btl.login.fragments.RoleFragment) fragment).confirmDeleteUser(email); // Gọi xác nhận trước khi xóa
            } else {
                Toast.makeText(fragment.getContext(), "Lỗi: Không thể xóa, Fragment không khả dụng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size(); // Trả về số lượng người dùng trong danh sách
    }

    // ViewHolder chứa các thành phần của từng mục trong danh sách
    static class RoleViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvPassword, tvFullName, tvRole;
        ImageView imgDelete;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvPassword = itemView.findViewById(R.id.tv_password);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvRole = itemView.findViewById(R.id.tv_role);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}