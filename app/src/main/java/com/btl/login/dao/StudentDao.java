package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.Student;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM student")
    List<Student> getAllStudents();

    @Query("SELECT * FROM student WHERE id=:studentId")
    Student getStudentById(int studentId);

    @Insert
    void addStudents(Student... students);

    @Delete
    void deleteStudent(Student student);
}
