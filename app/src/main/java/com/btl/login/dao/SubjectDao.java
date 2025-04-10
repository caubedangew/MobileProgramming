package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.dto.SubjectsTaughtByTeacherDTO;
import com.btl.login.entities.Subject;

import java.util.List;

@Dao
public interface SubjectDao {
    @Query("SELECT * FROM subject")
    List<Subject> getAllSubjects();

    @Query("SELECT * FROM subject WHERE id=:subjectId")
    Subject getSubjectById(int subjectId);

    @Insert
    void addSubject(Subject... subjects);

    @Delete
    void deleteSubject(Subject subject);

    @Query("DELETE FROM subject")
    void deleteAllSubjects();

    @Query("SELECT subject.id, subject.subjectName, subject.creditNumber, COUNT(*) as assignmentCount FROM teacherAssignment " +
            "JOIN openClass ON teacherAssignment.openClassId = openClass.id " +
            "JOIN subject ON subject.id = openClass.subjectId " +
            "WHERE teacherAssignment.teacherId=:teacherId AND subject.subjectName LIKE '%' || :subjectName || '%'" +
            "GROUP BY subject.id, subject.subjectName, subject.creditNumber")
    List<SubjectsTaughtByTeacherDTO> getSubjectsTaughtByTeacherLogin(int teacherId, String subjectName);
}
