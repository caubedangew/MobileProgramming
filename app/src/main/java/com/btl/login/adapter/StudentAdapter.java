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
import com.btl.login.dto.StudentDTO;
import com.btl.login.interfaces.OnStudentActionListener;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private final Context context;
    private final List<StudentDTO> studentList;
    private final OnStudentActionListener listener;

    public StudentAdapter(Context context, List<StudentDTO> studentList, OnStudentActionListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho một item của RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentDTO student = studentList.get(position);

        // Hiển thị thông tin sinh viên
        holder.tvStudentName.setText("Tên: " + student.getFirstName() + " " + student.getLastName());
        holder.tvStudentEmail.setText("Email: " + student.getEmail());
        holder.tvStudentClass.setText("Lớp: " + student.getClassName());

        // Sự kiện khi nhấn vào một mục
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStudentSelect(student); // Gọi đến sự kiện chọn sinh viên
            }
        });

        // Sự kiện xóa sinh viên
        holder.imgDeleteStudent.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStudentDelete(student, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentEmail, tvStudentClass;
        ImageView imgDeleteStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvStudentClass = itemView.findViewById(R.id.tvStudentClass);
            imgDeleteStudent = itemView.findViewById(R.id.imgDeleteStudent);
        }
    }
}