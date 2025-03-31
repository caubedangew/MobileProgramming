package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.AcademicYear;
import com.btl.login.entities.StudentScore;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface StudentScoreDao {
    @Query("SELECT * FROM studentScore")
    List<StudentScore> getAllStudentScores();

    @Query("SELECT * FROM studentScore WHERE id=:studentScoreId")
    StudentScore getStudentScoreById(int studentScoreId);

    @Insert
    void addStudentScores(StudentScore... studentScores);

    @Delete
    void deleteStudentScore(StudentScore studentScore);
}
