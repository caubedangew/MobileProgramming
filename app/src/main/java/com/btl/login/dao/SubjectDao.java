package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.btl.login.dto.SubjectDTO;
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

    @Query("SELECT subject.id, subject.subjectName, subject.creditNumber, " +
            "(SELECT COUNT(numberStudent.numberStudents) " +
            "            FROM (SELECT COUNT(*) as numberStudents FROM subjectregistration " +
            "            JOIN openClass ON openClass.id = subjectRegistration.openClassId " +
            "            GROUP BY openClass.id) as numberStudent) as assignmentCount " +
            "FROM teacherAssignment " +
            "JOIN openClass ON teacherAssignment.openClassId = openClass.id " +
            "JOIN subjectregistration ON openClass.id = subjectRegistration.openClassId " +
            "JOIN subject ON subject.id = openClass.subjectId " +
            "WHERE teacherAssignment.teacherId=:teacherId AND subject.subjectName LIKE '%' || :subjectName || '%' AND semesterId=:semesterId " +
            "GROUP BY subject.id, subject.subjectName, subject.creditNumber")
    List<SubjectsTaughtByTeacherDTO> getSubjectsTaughtByTeacherLogin(int semesterId, int teacherId, String subjectName);

    @Query("SELECT COUNT(*) FROM subject WHERE subjectName = :subjectName")
    int checkSubjectExists(String subjectName);

    @Update
    void updateSubject(Subject subject);

    @Query("SELECT subject.*, COALESCE(subject.creditNumber, 0) AS creditNumber FROM subject")
    List<SubjectDTO> getSubjectsWithCreditNumber();
}
