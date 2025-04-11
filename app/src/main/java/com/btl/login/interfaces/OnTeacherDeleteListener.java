package com.btl.login.interfaces;

import com.btl.login.dto.TeacherDTO;

public interface OnTeacherDeleteListener {
    void onTeacherDelete(TeacherDTO teacher, int position);
}