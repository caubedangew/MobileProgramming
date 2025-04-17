package com.btl.login.dto;

public class StatisticsStudentNumberDTO {
    private int subjectId;
    private int studentNumber;

    public StatisticsStudentNumberDTO(int subjectId, int studentNumber) {
        this.subjectId = subjectId;
        this.studentNumber = studentNumber;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }
}
