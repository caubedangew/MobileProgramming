package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Semester.class,
                parentColumns = "id",
                childColumns = "semesterId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = SubjectScore.class,
                parentColumns = "id",
                childColumns = "subjectScoreId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class OpenClass extends BaseProperties{
    private int semesterId;

    private int subjectScoreId;

    @ColumnInfo(defaultValue = "0")
    private int capacity;

    @NotNull
    private String openClassName;

    public OpenClass(int semesterId, int subjectScoreId, int capacity, @NotNull String openClassName) {
        this.semesterId = semesterId;
        this.subjectScoreId = subjectScoreId;
        this.capacity = capacity;
        this.openClassName = openClassName;
    }


    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public int getSubjectScoreId() {
        return subjectScoreId;
    }

    public void setSubjectScoreId(int subjectScoreId) {
        this.subjectScoreId = subjectScoreId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public @NotNull String getOpenClassName() {
        return openClassName;
    }

    public void setOpenClassName(@NotNull String openClassName) {
        this.openClassName = openClassName;
    }
}
