package com.btl.login.interfaces;

import com.btl.login.dto.TeacherDTO;

public interface OnTeacherActionListener {
    void onTeacherClick(TeacherDTO teacherDTO); // Khi người dùng click vào item

    void onTeacherDelete(TeacherDTO teacherDTO, int position); // Khi người dùng xóa giáo viên

    void onTeacherTagForEdit(TeacherDTO teacherDTO); // Khi người dùng chọn giáo viên để chỉnh sửa
}
