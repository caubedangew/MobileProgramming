package com.btl.login.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

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
    private Long startDate;
    @NotNull
    private Long endDate;

    private int academicYearId;

    public Semester(@NotNull String semesterName, @NotNull Long startDate, @NotNull Long endDate, int academicYearId) {
        super();
        this.semesterName = semesterName;
        this.academicYearId = academicYearId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public @NotNull String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(@NotNull String semesterName) {
        this.semesterName = semesterName;
    }

    public @NotNull Long getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull Long startDate) {
        this.startDate = startDate;
    }

    public @NotNull Long getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull Long endDate) {
        this.endDate = endDate;
    }

    public int getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(int academicYearId) {
        this.academicYearId = academicYearId;
    }
}
