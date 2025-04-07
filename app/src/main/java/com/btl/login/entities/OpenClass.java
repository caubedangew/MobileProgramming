package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

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
        ),
        @ForeignKey(
                entity = StudentClass.class,
                parentColumns = "id",
                childColumns = "classId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
}, indices = @Index(value = {"semesterId", "subjectId", "classId"}, unique = true))
public class OpenClass extends BaseProperties {
    private int semesterId;

    private int subjectId;

    private int classId;

    @ColumnInfo(defaultValue = "0")
    private int capacity;

    @NotNull
    private String openClassName;

    public OpenClass(@NotNull String openClassName, int capacity, int semesterId, int subjectId, int classId) {
        super();
        this.semesterId = semesterId;
        this.subjectId = subjectId;
        this.capacity = capacity;
        this.openClassName = openClassName;
        this.classId = classId;
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

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
