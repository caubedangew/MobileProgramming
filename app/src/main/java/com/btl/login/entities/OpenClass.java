package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

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
                entity = Subject.class,
                parentColumns = "id",
                childColumns = "subjectId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class OpenClass extends BaseProperties{
    private int semesterId;

    private int subjectId;

    @ColumnInfo(defaultValue = "0")
    private int capacity;

    @NotNull
    private String openClassName;

    public OpenClass(int semesterId, int subjectId, int capacity, @NotNull String openClassName) {
        this.semesterId = semesterId;
        this.subjectId = subjectId;
        this.capacity = capacity;
        this.openClassName = openClassName;
    }


    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
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
