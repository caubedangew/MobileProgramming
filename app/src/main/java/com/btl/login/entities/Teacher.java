package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Major.class,
                parentColumns = "id",
                childColumns = "majorId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
}, indices = {@Index(value="email", unique = true)})
public class Teacher extends BaseInformation {
    private int majorId;

    public Teacher(@NotNull String firstName, @NotNull String lastName, int majorId) {
        super(firstName, lastName);
        this.majorId = majorId;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }
}
