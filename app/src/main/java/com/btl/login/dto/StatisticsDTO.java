package com.btl.login.dto;

public class StatisticsDTO {
    private final String subjectName;
    private final double avarageScore;

    public StatisticsDTO(String subjectName, double avarageScore) {
        this.subjectName = subjectName;
        this.avarageScore = avarageScore;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public double getAvarageScore() {
        return avarageScore;
    }
}
