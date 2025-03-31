package com.btl.login.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(indices = {@Index(value="subjectName", unique = true)})
public class Subject extends BaseProperties {

    @NotNull
    private String subjectName;

    @ColumnInfo(defaultValue = "0")
    private float creditNumber;

    @Ignore
    public Subject() {
        this.subjectName = "Example";
    }

    public Subject(@NotNull String subjectName, float creditNumber) {
        this.creditNumber = creditNumber;
        this.subjectName = subjectName;
    }

    @NonNull
    public String getSubjectName() {
        return subjectName;
    }

    public float getCreditNumber() {
        return creditNumber;
    }

    public void setCreditNumber(float creditNumber) {
        this.creditNumber = creditNumber;
    }

    public void setSubjectName(@NonNull String subjectName) {
        this.subjectName = subjectName;
    }
}
