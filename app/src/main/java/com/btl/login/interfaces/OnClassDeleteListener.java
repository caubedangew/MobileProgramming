package com.btl.login.interfaces;

import com.btl.login.dto.StudentClassWithMajorDTO;

public interface OnClassDeleteListener {
    void onClassDelete(StudentClassWithMajorDTO studentClass, int position);
}