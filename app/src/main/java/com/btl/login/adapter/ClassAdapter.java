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
import com.btl.login.dto.StudentClassDTO;
import com.btl.login.interfaces.OnClassActionListener;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private final Context context;
    private final List<StudentClassDTO> classList;
    private final OnClassActionListener listener;

    public ClassAdapter(Context context, List<StudentClassDTO> classList, OnClassActionListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener; // Gán giá trị cho listener
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        // Lấy dữ liệu lớp học tại vị trí hiện tại
        StudentClassDTO studentClassDTO = classList.get(position);

        // Hiển thị dữ liệu vào giao diện
        holder.tvClassName.setText("Tên lớp: " + studentClassDTO.getStudentClass().getClassName());
        holder.tvMajor.setText(
                studentClassDTO.getMajorName() != null ? studentClassDTO.getMajorName() : "Chưa xác định"
        );
        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClassClick(studentClassDTO);

                listener.onClassTagForEdit(studentClassDTO);
            }
        });

        // Xử lý sự kiện xóa lớp học
        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClassDelete(studentClassDTO, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    // ViewHolder class
    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvMajor;
        ImageView imgDelete;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_classname);
            tvMajor = itemView.findViewById(R.id.tv_major);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}