package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.dto.SemesterDTO;
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

    @Query("SELECT semester.id as semesterId, semesterName || ' năm học ' ||  academicYearName as fullSemesterName " +
            "FROM Semester " +
            "JOIN AcademicYear ON AcademicYear.id = Semester.academicYearId " +
            "WHERE Semester.id <= (SELECT id " +
            "FROM Semester " +
            "WHERE :currentTime BETWEEN Semester.startDate AND Semester.endDate) " +
            "ORDER BY semester.id DESC")
    List<SemesterDTO> getSemesterNameAndAcademicYearName(Long currentTime);

    @Query("SELECT id " +
            "FROM Semester " +
            "WHERE :currentTime BETWEEN Semester.startDate AND Semester.endDate")
    int getSemesterIdAtCurrentTime(Long currentTime);

    @Insert
    void addSemesters(Semester... semesters);

    @Delete
    void deleteSemester(Semester semester);

    @Query("DELETE FROM semester")
    void deleteAllSemesters();
}
