package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Teacher.class,
                parentColumns = "id",
                childColumns = "teacherId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = OpenClass.class,
                parentColumns = "id",
                childColumns = "openClassId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class TeacherAssignment extends BaseProperties{
    private int teacherId;

    private int openClassId;

    public TeacherAssignment(int teacherId, int openClassId) {
        this.teacherId = teacherId;
        this.openClassId = openClassId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getOpenClassId() {
        return openClassId;
    }

    public void setOpenClassId(int openClassId) {
        this.openClassId = openClassId;
    }
}
