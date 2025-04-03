package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.btl.login.entities.Teacher;

import java.util.List;

@Dao
public interface TeacherDao {
    @Query("SELECT * FROM teacher")
    List<Teacher> getAllTeachers();

    @Query("SELECT * FROM teacher WHERE id=:teacherId")
    Teacher getTeacherById(int teacherId);

    @Query("SELECT * FROM teacher WHERE email=:email")
    Teacher getTeacherByEmail(String email);

    @Insert
    void addTeachers(Teacher... teachers);

    @Delete
    void deleteTeacher(Teacher teacher);

    @Query("DELETE FROM teacher")
    void deleteAllTeachers();
}
