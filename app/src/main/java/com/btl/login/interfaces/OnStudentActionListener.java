package com.btl.login.interfaces;

import com.btl.login.dto.StudentDTO;

public interface OnStudentActionListener {
    void onStudentDelete(StudentDTO student, int position); // Đã có

    void onStudentSelect(StudentDTO student); // Mới thêm
}