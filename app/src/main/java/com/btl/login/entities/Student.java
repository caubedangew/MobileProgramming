package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = StudentClass.class,
                parentColumns = "id",
                childColumns = "studentClassId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)
}, indices = {@Index(value="email", unique = true)})
public class Student extends BaseInformation {
    private int studentClassId;

    public Student(@NotNull String firstName, @NotNull String lastName, String email, int studentClassId) {
        super(firstName, lastName, email);
        this.studentClassId = studentClassId;
    }

    public int getStudentClassId() {
        return studentClassId;
    }

    public void setStudentClassId(int studentClassId) {
        this.studentClassId = studentClassId;
    }
}
