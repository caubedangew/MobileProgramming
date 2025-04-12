package com.btl.login.entities;

import androidx.room.Entity;

import org.jetbrains.annotations.NotNull;

@Entity
public class AcademicYear extends BaseProperties {
    @NotNull
    private String academicYearName;

    public AcademicYear(@NotNull String academicYearName) {
        super();
        this.academicYearName = academicYearName;
    }

    public @NotNull String getAcademicYearName() {
        return academicYearName;
    }

    public void setAcademicYearName(@NotNull String academicYearName) {
        this.academicYearName = academicYearName;
    }
}
