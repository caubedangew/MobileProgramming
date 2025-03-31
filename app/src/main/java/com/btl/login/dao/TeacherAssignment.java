package com.btl.login.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface TeacherAssignment {
    @Query("SELECT * FROM teacherAssignment")
    List<TeacherAssignment> getAllTeachers();

    @Query("SELECT * FROM teacherAssignment WHERE id=:teacherAssignmentId")
    TeacherAssignment getTeacherAssignmentById(int teacherAssignmentId);

    @Insert
    void addTeacherAssignments(TeacherAssignment... teacherAssignments);

    @Delete
    void deleteTeacherAssignment(TeacherAssignment teacherAssignment);
}
