package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Student.class,
                parentColumns = "id",
                childColumns = "studentId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = OpenClass.class,
                parentColumns = "id",
                childColumns = "openClassId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ),
})
public class SubjectRegistration extends BaseProperties{
    private int openClassId;

    private int studentId;

    public SubjectRegistration(int openClassId, int studentId) {
        this.openClassId = openClassId;
        this.studentId = studentId;
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
