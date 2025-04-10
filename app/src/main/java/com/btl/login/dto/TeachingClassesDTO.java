package com.btl.login.dto;

public class TeachingClassesDTO {
    private final int openClassId;
    private final int subjectId;
    private final String className;
    private final int numberSubjectRegistration;
    private final int numberStudentHaveScore;

    public TeachingClassesDTO(int openClassId, int subjectId, String className, int numberSubjectRegistration, int numberStudentHaveScore) {
        this.openClassId = openClassId;
        this.subjectId = subjectId;
        this.className = className;
        this.numberSubjectRegistration = numberSubjectRegistration;
        this.numberStudentHaveScore = numberStudentHaveScore;
    }

    public String getClassName() {
        return className;
    }

    public int getNumberSubjectRegistration() {
        return numberSubjectRegistration;
    }

    public int getNumberStudentHaveScore() {
        return numberStudentHaveScore;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public int getOpenClassId() {
        return openClassId;
    }
}
