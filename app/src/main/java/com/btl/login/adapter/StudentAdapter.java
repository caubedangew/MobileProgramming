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

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private final Context context;
    private List<StudentDTO> studentList;
    private final OnStudentActionListener listener;
    private final List<StudentDTO> studentListFull; // Danh sách gốc

    public StudentAdapter(Context context, List<StudentDTO> studentList, OnStudentActionListener listener) {
        this.context = context;
        this.studentList = new ArrayList<>(studentList); // Sao lưu danh sách hiện tại
        this.studentListFull = new ArrayList<>(studentList); // Sao lưu danh sách gốc
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                listener.onStudentSelect(student);
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

    public void resetList() {
        if (studentListFull == null || studentListFull.isEmpty()) {
            return; // Không làm gì nếu danh sách gốc chưa được khởi tạo
        }
        studentList.clear();
        studentList.addAll(studentListFull); // Khôi phục từ danh sách gốc
        notifyDataSetChanged();
    }

    // Khôi phục danh sách gốc (khi xóa tìm kiếm)
    public void updateList(List<StudentDTO> newList) {
        if (newList == null || newList.isEmpty()) return; // Kiểm tra danh sách trước khi cập nhật

        studentList.clear();
        studentList.addAll(newList);
        notifyDataSetChanged();
    }
    public void updateFullList(List<StudentDTO> updatedList) {
        if (updatedList == null || updatedList.isEmpty()) return; // Kiểm tra danh sách trước khi cập nhật

        studentList.clear();
        studentList.addAll(updatedList);

        studentListFull.clear();
        studentListFull.addAll(updatedList); // Cập nhật danh sách gốc

        notifyDataSetChanged();
    }
    public void removeStudent(StudentDTO student) {
        studentList.remove(student); // Xóa khỏi danh sách hiển thị
        studentListFull.remove(student); // Xóa khỏi danh sách gốc
    }
    public List<StudentDTO> getStudentListFull() {
        return new ArrayList<>(studentListFull); // Trả về bản sao danh sách gốc để tránh sửa đổi trực tiếp
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