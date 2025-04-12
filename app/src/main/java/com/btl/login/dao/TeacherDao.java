package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.btl.login.dto.TeacherDTO;
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

    @Update
    void updateTeacher(Teacher teacher);

    @Query("SELECT COUNT(*) FROM teacher WHERE email = :email")
    int checkEmailExists(String email);

    @Query("SELECT teacher.id, teacher.firstName, teacher.lastName, teacher.email, " +
            "COALESCE(teacher.departmentId, 0) AS departmentId, " +
            "COALESCE(department.departmentName, 'Chưa xác định') AS departmentName " +
            "FROM teacher " +
            "LEFT JOIN department ON teacher.departmentId = department.id")
    List<TeacherDTO> getTeachersWithDepartmentName();

}
