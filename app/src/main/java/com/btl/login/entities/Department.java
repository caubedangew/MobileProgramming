package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(indices = {@Index(value="departmentName", unique = true)})
public class Department extends BaseProperties{
    @NotNull
    private String departmentName;

    public Department(@NotNull String departmentName) {
        super();
        this.departmentName = departmentName;
    }

    public @NotNull String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(@NotNull String departmentName) {
        this.departmentName = departmentName;
    }
}
