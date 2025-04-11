package com.btl.login.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.dto.StudentDTO;

import java.util.List;

public class MultipleStudentsAdapter extends RecyclerView.Adapter<MultipleStudentsAdapter.StudentInputViewHolder> {

    private final List<StudentDTO> studentList;

    public MultipleStudentsAdapter(List<StudentDTO> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentInputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout custom_student_input.xml cho từng item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_students_input, parent, false);
        return new StudentInputViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentInputViewHolder holder, int position) {
        StudentDTO student = studentList.get(position);

        // Liên kết dữ liệu hiện tại (nếu có)
        holder.eTxtFirstName.setText(student.getFirstName());
        holder.eTxtLastName.setText(student.getLastName());
        holder.eTxtEmail.setText(student.getEmail());
        holder.eTxtInputClass.setText(student.getClassName());

        // Xử lý sự kiện thay đổi dữ liệu
        holder.eTxtFirstName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                student.setFirstName(s.toString());
            }
        });

        holder.eTxtLastName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                student.setLastName(s.toString());
            }
        });

        holder.eTxtEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                student.setEmail(s.toString());
            }
        });

        holder.eTxtInputClass.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                student.setClassName(s.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentInputViewHolder extends RecyclerView.ViewHolder {
        EditText eTxtFirstName, eTxtLastName, eTxtEmail, eTxtInputClass;

        public StudentInputViewHolder(@NonNull View itemView) {
            super(itemView);
            eTxtFirstName = itemView.findViewById(R.id.eTxtFirstName);
            eTxtLastName = itemView.findViewById(R.id.eTxtLastName);
            eTxtEmail = itemView.findViewById(R.id.eTxtEmail);
            eTxtInputClass = itemView.findViewById(R.id.eTxtInputClass);
        }
    }

    // Helper TextWatcher class để giảm lặp code
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    }
}