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
}, indices = {@Index(value="majorName", unique = true)})
public class Major extends BaseProperties {
    @NotNull
    private String majorName;

    private int departmentId;

    public Major(@NotNull String majorName, int departmentId) {
        super();
        this.majorName = majorName;
        this.departmentId = departmentId;
    }

    public @NotNull String getMajorName() {
        return majorName;
    }

    public void setMajorName(@NotNull String majorName) {
        this.majorName = majorName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
}
