package com.btl.login.dto;

import androidx.room.Embedded;

import com.btl.login.entities.StudentClass;

public class StudentClassDTO {
    @Embedded
    private StudentClass studentClass; // Đối tượng lớp học
    private String majorName;          // Tên ngành học

    // Constructor
    public StudentClassDTO(StudentClass studentClass, String majorName) {
        if (studentClass == null) {
            throw new IllegalArgumentException("StudentClass cannot be null");
        }
        this.studentClass = studentClass;
        this.majorName = (majorName != null) ? majorName : "Chưa xác định";
    }

    // Getter & Setter
    public StudentClass getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(StudentClass studentClass) {
        if (studentClass == null) {
            throw new IllegalArgumentException("StudentClass cannot be null");
        }
        this.studentClass = studentClass;
    }

    public String getMajorName() {
        return (majorName != null) ? majorName : "Chưa xác định";
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    // Utility Method
    public String getClassDetails() {
        return "Lớp: " + studentClass.getClassName() + ", Ngành: " + getMajorName();
    }
}