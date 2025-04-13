package com.btl.login.interfaces;

import com.btl.login.dto.StudentClassDTO;

public interface OnClassActionListener {
    void onClassClick(StudentClassDTO classDTO); // Phương thức trừu tượng cần được triển khai
    void onClassDelete(StudentClassDTO classDTO, int position);
    void onClassTagForEdit(StudentClassDTO classDTO);
}