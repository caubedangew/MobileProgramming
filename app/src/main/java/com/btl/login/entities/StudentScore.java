package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import org.jetbrains.annotations.NotNull;

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
        ),
        @ForeignKey(
                entity = SubjectScore.class,
                parentColumns = "id",
                childColumns = "subjectScoreId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        ),
})
public class StudentScore extends BaseProperties {
    @ColumnInfo(defaultValue = "0")
    private float score;
    private int openClassId;
    private int studentId;
    private int subjectScoreId;

    public StudentScore(float score, int openClassId, int studentId, int subjectScoreId) {
        this.score = score;
        this.openClassId = openClassId;
        this.studentId = studentId;
        this.subjectScoreId = subjectScoreId;
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

    public int getSubjectScoreId() {
        return subjectScoreId;
    }

    public void setSubjectScoreId(int subjectScoreId) {
        this.subjectScoreId = subjectScoreId;
    }
}
