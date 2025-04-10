package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.Semester;

import java.util.List;

@Dao
public interface SemesterDao {
    @Query("SELECT * FROM semester")
    List<Semester> getAllSemesters();

    @Query("SELECT * FROM semester WHERE id=:semesterId")
    Semester getSemesterById(int semesterId);

    @Query("SELECT * FROM semester WHERE semesterName LIKE '%' || :semesterName || '%' AND academicYearId=:academicYearId")
    Semester getSemesterByNameAndAcademicYearId(String semesterName, int academicYearId);

    @Query("SELECT semesterName || ' ' ||  academicYearName " +
            "FROM Semester " +
            "JOIN AcademicYear ON AcademicYear.id = Semester.academicYearId " +
            "WHERE AcademicYear.id < 5")
    List<String> getSemesterNameAndAcademicYearName();

    @Insert
    void addSemesters(Semester... semesters);

    @Delete
    void deleteSemester(Semester semester);

    @Query("DELETE FROM semester")
    void deleteAllSemesters();
}
