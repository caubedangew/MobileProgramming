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
import com.btl.login.entities.Subject;
import com.btl.login.interfaces.OnSubjectDeleteListener;

import java.util.List;

public class SubjectAdapter extends ArrayAdapter<Subject> {

    private final Context context;
    private final List<Subject> subjectList;
    private final OnSubjectDeleteListener listener;

    public SubjectAdapter(@NonNull Context context, List<Subject> subjectList, OnSubjectDeleteListener listener) {
        super(context, R.layout.custom_list_subject, subjectList);
        this.context = context;
        this.subjectList = subjectList;
        this.listener = listener; // Sử dụng interface listener
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_list_subject, parent, false);
        }

        // Liên kết các thành phần giao diện
        TextView tvSubjectName = convertView.findViewById(R.id.tv_subject);
        TextView tvCredit = convertView.findViewById(R.id.tv_credit);
        ImageView imgDelete = convertView.findViewById(R.id.img_delete);

        // Lấy dữ liệu môn học tại vị trí hiện tại
        Subject subject = getItem(position);

        // Hiển thị dữ liệu vào giao diện
        if (subject != null) {
            tvSubjectName.setText("Môn học: " + subject.getSubjectName());
            tvCredit.setText("Tín chỉ: " + subject.getCreditNumber());

            // Xử lý sự kiện xóa môn học thông qua listener
            imgDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubjectDelete(subject, position);
                }
            });
        }

        return convertView;
    }
}