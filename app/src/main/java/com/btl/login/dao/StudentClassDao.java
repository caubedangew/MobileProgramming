package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.StudentClass;

import java.util.List;

@Dao
public interface StudentClassDao {
    @Query("SELECT * FROM studentClass")
    List<StudentClass> getAllClasses();

    @Query("SELECT * FROM studentClass WHERE id=:studentClassId")
    StudentClass getClassById(int studentClassId);

    @Insert
    void addClasses(StudentClass... studentStudentClasses);

    @Delete
    void deleteClass(StudentClass studentClass);
}
