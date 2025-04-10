package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Department.class,
                parentColumns = "id",
                childColumns = "departmentId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
}, indices = {@Index(value="email", unique = true)})
public class Teacher extends BaseInformation {
    private int departmentId;

    public Teacher(@NotNull String firstName, @NotNull String lastName, String email, int departmentId) {
        super(firstName, lastName, email);
        this.departmentId = departmentId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
}
