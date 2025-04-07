package com.btl.login.dto;

public class SubjectsTaughtByTeacherDTO {
    private final int id;
    private final String subjectName;
    private final double creditNumber;
    private final Long assignmentCount;

    public SubjectsTaughtByTeacherDTO(int id, String subjectName, double creditNumber, Long assignmentCount) {
        this.subjectName = subjectName;
        this.creditNumber = creditNumber;
        this.assignmentCount = assignmentCount;
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public double getCreditNumber() {
        return creditNumber;
    }

    public Long getAssignmentCount() {
        return assignmentCount;
    }

    public int getId() {
        return id;
    }
}

