package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = OpenClass.class,
                parentColumns = "id",
                childColumns = "openClassId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Student.class,
                parentColumns = "id",
                childColumns = "studentId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class StudentScore extends BaseProperties {
    @ColumnInfo(defaultValue = "0")
    private float score;
    private int openClassId;
    private int studentId;

    public StudentScore(float score, int openClassId, int studentId) {
        this.score = score;
        this.openClassId = openClassId;
        this.studentId = studentId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getOpenClassId() {
        return openClassId;
    }

    public void setOpenClassId(int openClassId) {
        this.openClassId = openClassId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
}
