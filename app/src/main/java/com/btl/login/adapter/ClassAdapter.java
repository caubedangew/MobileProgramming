package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.btl.login.R;
import com.btl.login.dto.StudentClassWithMajorDTO;
import com.btl.login.interfaces.OnClassDeleteListener;

import java.util.List;

public class ClassAdapter extends ArrayAdapter<StudentClassWithMajorDTO> {

    private final Context context;
    private final List<StudentClassWithMajorDTO> classList;
    private final OnClassDeleteListener listener;

    public ClassAdapter(@NonNull Context context, List<StudentClassWithMajorDTO> classList, OnClassDeleteListener listener) {
        super(context, R.layout.custom_list_class, classList);
        this.context = context;
        this.classList = classList;
        this.listener = listener; // Gán giá trị cho listener
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_list_class, parent, false);
        }

        // Liên kết các thành phần giao diện
        TextView tvClassName = convertView.findViewById(R.id.tv_classname);
        TextView tvMajor = convertView.findViewById(R.id.tv_major);
        ImageView imgDelete = convertView.findViewById(R.id.img_delete);

        // Lấy dữ liệu lớp học tại vị trí hiện tại
        StudentClassWithMajorDTO classWithMajor = classList.get(position);

        // Hiển thị dữ liệu vào giao diện
        tvClassName.setText("Tên lớp: " + classWithMajor.getClassName());
        tvMajor.setText("Ngành: " + classWithMajor.getMajorName());

        // Xử lý sự kiện xóa lớp học
        imgDelete.setOnClickListener(v -> {
            if (listener != null){
                listener.onClassDelete(classWithMajor, position);
            }
        });

        return convertView;
    }
}