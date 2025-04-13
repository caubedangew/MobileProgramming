package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

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
}, indices = {@Index(value = {"openClassId", "studentId", "subjectScoreId"}, unique = true)})
public class StudentScore extends BaseProperties {
    @ColumnInfo(defaultValue = "0")
    private double score;
    private int openClassId;
    private int studentId;
    private int subjectScoreId;

    public StudentScore(double score, int openClassId, int studentId, int subjectScoreId) {
        super();
        this.score = score;
        this.openClassId = openClassId;
        this.studentId = studentId;
        this.subjectScoreId = subjectScoreId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
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
