package com.btl.login.dto;

public class TestDTO {
    private final String studentName;
    private final double averageScore;

    public TestDTO(String studentName, double averageScore) {
        this.studentName = studentName;
        this.averageScore = averageScore;
    }

    public String getStudentName() {
        return studentName;
    }

    public double getAverageScore() {
        return averageScore;
    }
}
