package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import org.jetbrains.annotations.NotNull;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Major.class,
                parentColumns = "id",
                childColumns = "majorId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class StudentClass extends BaseProperties {
    @NotNull
    private String className;

    private int majorId;

    public StudentClass(@NotNull String className, int majorId) {
        super();
        this.className = className;
        this.majorId = majorId;
    }

    public @NotNull String getClassName() {
        return className;
    }

    public void setClassName(@NotNull String className) {
        this.className = className;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }
}
