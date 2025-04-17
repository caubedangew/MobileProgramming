package com.btl.login.dto;

public class SemesterDTO {
    private int semesterId;
    private String fullSemesterName;

    public SemesterDTO(int semesterId, String fullSemesterName) {
        this.semesterId = semesterId;
        this.fullSemesterName = fullSemesterName;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getFullSemesterName() {
        return fullSemesterName;
    }

    public void setFullSemesterName(String fullSemesterName) {
        this.fullSemesterName = fullSemesterName;
    }
}
