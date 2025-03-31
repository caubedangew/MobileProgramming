package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = AcademicYear.class,
                parentColumns = "id",
                childColumns = "academicYearId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )
})
public class Semester extends BaseProperties {
    @NotNull
    private String semesterName;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;

    private int academicYearId;

    @Ignore
    public Semester() {
        semesterName = "";
        startDate = "";
        endDate = "";
    }

    public Semester(@NotNull String semesterName, @NotNull String startDate, @NotNull String endDate, int academicYearId) {
        this.semesterName = semesterName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.academicYearId = academicYearId;
    }

    public @NotNull String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(@NotNull String semesterName) {
        this.semesterName = semesterName;
    }

    public @NotNull String getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull String startDate) {
        this.startDate = startDate;
    }

    public @NotNull String getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull String endDate) {
        this.endDate = endDate;
    }

    public int getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(int academicYearId) {
        this.academicYearId = academicYearId;
    }
}
