package com.btl.login.dto;

public class StatisticsScoreDTO {
    private int subjectId;
    private double avarageScore;

    public StatisticsScoreDTO(int subjectId, double avarageScore) {
        this.subjectId = subjectId;
        this.avarageScore = avarageScore;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public double getAvarageScore() {
        return avarageScore;
    }

    public void setAvarageScore(double avarageScore) {
        this.avarageScore = avarageScore;
    }
}
