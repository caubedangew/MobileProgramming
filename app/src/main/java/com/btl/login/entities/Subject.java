package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Subject extends BaseProperties {

    @NotNull
    private String subjectName;

    private float creditNumber;

    public String getSubjectName() {
        return subjectName;
    }

    public float getCreditNumber() {
        return creditNumber;
    }

    public void setCreditNumber(float creditNumber) {
        this.creditNumber = creditNumber;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
