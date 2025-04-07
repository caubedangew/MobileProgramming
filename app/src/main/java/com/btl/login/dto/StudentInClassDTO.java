package com.btl.login.dto;

public class StudentInClassDTO {
    private final int id;
    private final String fullName;

    public StudentInClassDTO(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }
}
