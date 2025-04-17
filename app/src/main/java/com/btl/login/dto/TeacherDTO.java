package com.btl.login.dto;

import androidx.room.Embedded;
import androidx.room.Ignore;

import com.btl.login.entities.Teacher;

public class TeacherDTO {
    @Embedded
    private Teacher teacher;
    private String departmentName;

    // Constructor
    public TeacherDTO(Teacher teacher, String departmentName) {
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher cannot be null");
        }
        this.teacher = teacher;
        this.departmentName = (departmentName != null) ? departmentName : "Chưa xác định";
    }

    // Getter & Setter
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher cannot be null");
        }
        this.teacher = teacher;
    }

    public String getDepartmentName() {
        return (departmentName != null) ? departmentName : "Chưa xác định";
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    // Utility Method
    public String getTeacherFullName() {
        return teacher.getFirstName() + " " + teacher.getLastName();
    }
    @Ignore // Thêm phương thức lấy email
    public String getEmail() {
        return teacher.getEmail();
    }
}