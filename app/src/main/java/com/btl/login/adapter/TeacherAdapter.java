package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.dto.TeacherDTO;
import com.btl.login.interfaces.OnTeacherActionListener;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private final Context context;
    private final List<TeacherDTO> teacherList;
    private final OnTeacherActionListener listener;

    public TeacherAdapter(Context context, List<TeacherDTO> teacherList, OnTeacherActionListener listener) {
        this.context = context;
        this.teacherList = teacherList;
        this.listener = listener; // Listener xử lý sự kiện xóa
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherDTO teacherDTO = teacherList.get(position);

        // Hiển thị thông tin giáo viên
        holder.tvFirstName.setText("Họ: " + teacherDTO.getTeacher().getFirstName());
        holder.tvLastName.setText("Tên: " + teacherDTO.getTeacher().getLastName());
        holder.tvEmail.setText("Email: " + teacherDTO.getTeacher().getEmail());
        holder.tvDepartment.setText(
                teacherDTO.getDepartmentName() != null ? teacherDTO.getDepartmentName() : "Chưa xác định"
        );

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTeacherClick(teacherDTO); // Truyền dữ liệu giáo viên khi click vào item

                // **Gán dữ liệu giáo viên được chọn vào nút chỉnh sửa (btnEditTeacher)**
                listener.onTeacherTagForEdit(teacherDTO); // Thêm phương thức xử lý gán tag
            }
        });

        // Xử lý sự kiện xóa giáo viên
        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTeacherDelete(teacherDTO, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    // ViewHolder class
    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvFirstName, tvLastName, tvEmail, tvDepartment;
        ImageView imgDelete;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFirstName = itemView.findViewById(R.id.tv_firstname);
            tvLastName = itemView.findViewById(R.id.tv_lastname);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvDepartment = itemView.findViewById(R.id.tv_department);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}