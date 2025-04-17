package com.btl.login.dto;

public class StudentInClassDTO {
    private final int id;
    private final String fullName;
    private final int numberScore;

    public StudentInClassDTO(int id, String fullName, int numberScore) {
        this.id = id;
        this.fullName = fullName;
        this.numberScore = numberScore;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public int getNumberScore() {
        return numberScore;
    }
}
