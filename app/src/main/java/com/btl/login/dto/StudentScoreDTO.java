package com.btl.login.dto;

public class StudentScoreDTO {
    private final double score;
    private final int subjectScoreId;

    public StudentScoreDTO(double score, int subjectScoreId) {
        this.score = score;
        this.subjectScoreId = subjectScoreId;
    }

    public double getScore() {
        return score;
    }

    public int getSubjectScoreId() {
        return subjectScoreId;
    }
}
