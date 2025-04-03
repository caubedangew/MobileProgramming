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

    @Insert
    void addSemesters(Semester... semesters);

    @Delete
    void deleteSemester(Semester semester);

    @Query("DELETE FROM semester")
    void deleteAllSemesters();
}
