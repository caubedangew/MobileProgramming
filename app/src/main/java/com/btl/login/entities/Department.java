package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

@Entity
public class Department extends BaseProperties{
    @NotNull
    private String departmentName;


    @Ignore
    public Department() {
        departmentName = "";
    }

    public Department(@NotNull String departmentName) {
        this.departmentName = departmentName;
    }

    public @NotNull String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(@NotNull String departmentName) {
        this.departmentName = departmentName;
    }
}
