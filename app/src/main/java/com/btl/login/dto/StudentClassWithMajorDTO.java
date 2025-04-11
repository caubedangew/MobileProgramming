package com.btl.login.dto;

public class StudentClassWithMajorDTO {
    private int id;               // ID của lớp học
    private String className;     // Tên lớp học
    private String majorName;     // Tên ngành học

    // Constructor với ba tham số
    public StudentClassWithMajorDTO(int id, String className, String majorName) {
        this.id = id;
        this.className = className;
        this.majorName = majorName != null ? majorName : "Chưa xác định";
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }
}