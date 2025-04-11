package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.btl.login.R;
import com.btl.login.dto.TeacherDTO;
import com.btl.login.interfaces.OnTeacherDeleteListener;

import java.util.List;

public class TeacherAdapter extends ArrayAdapter<TeacherDTO> {

    private final Context context;
    private final List<TeacherDTO> teacherList;
    private final OnTeacherDeleteListener listener;

    public TeacherAdapter(@NonNull Context context, List<TeacherDTO> teacherList, OnTeacherDeleteListener listener) {
        super(context, R.layout.custom_list_teacher, teacherList);
        this.context = context;
        this.teacherList = teacherList;
        this.listener = listener; // Gán listener để xử lý sự kiện xóa
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_list_teacher, parent, false);
        }

        TeacherDTO teacherDTO = teacherList.get(position);

        // Liên kết các thành phần giao diện
        TextView tvFirstName = convertView.findViewById(R.id.tv_firstname);
        TextView tvLastName = convertView.findViewById(R.id.tv_lastname);
        TextView tvEmail = convertView.findViewById(R.id.tv_email);
        TextView tvDepartment = convertView.findViewById(R.id.tv_department);
        ImageView imgDelete = convertView.findViewById(R.id.img_delete);

        // Hiển thị thông tin giáo viên
        tvFirstName.setText(teacherDTO.getTeacher().getFirstName());
        tvLastName.setText(teacherDTO.getTeacher().getLastName());
        tvEmail.setText(teacherDTO.getTeacher().getEmail());
        tvDepartment.setText((teacherDTO.getDepartmentName() != null) ? teacherDTO.getDepartmentName() : "Chưa xác định");

        // Xử lý sự kiện xóa giáo viên
        imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTeacherDelete(teacherDTO, position);
            }
        });

        return convertView;
    }
}