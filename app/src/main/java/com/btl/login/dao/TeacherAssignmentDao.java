package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.TeacherAssignment;

import java.util.List;

@Dao
public interface TeacherAssignmentDao {
    @Query("SELECT * FROM teacherAssignment")
    List<TeacherAssignment> getAllTeacherAssignments();

    @Query("SELECT * FROM teacherAssignment WHERE id=:teacherAssignmentId")
    TeacherAssignment getTeacherAssignmentById(int teacherAssignmentId);

    @Insert
    void addTeacherAssignments(TeacherAssignment... teacherAssignmentDaos);

    @Delete
    void deleteTeacherAssignment(TeacherAssignment teacherAssignmentDao);

}
