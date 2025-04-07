package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.btl.login.dto.StudentInClassDTO;
import com.btl.login.dto.StudentScoreDTO;
import com.btl.login.entities.Student;
import com.btl.login.entities.StudentScore;

import java.util.List;

@Dao
public interface StudentScoreDao {
    @Query("SELECT * FROM studentScore")
    List<StudentScore> getAllStudentScores();

    @Query("SELECT * FROM studentScore WHERE id=:studentScoreId")
    StudentScore getStudentScoreById(int studentScoreId);

    @Query("SELECT * FROM studentScore WHERE subjectScoreId=:subjectScoreId AND studentId=:studentId")
    StudentScore getStudentScoreBySubjectScoreAndStudent(int subjectScoreId, int studentId);

    @Insert
    void addStudentScores(StudentScore... studentScores);

    @Delete
    void deleteStudentScore(StudentScore studentScore);

    @Query("SELECT subjectRegistration.studentId as id, (student.firstName || ' ' || student.lastName) as fullName " +
            "FROM student " +
            "JOIN subjectRegistration ON subjectRegistration.studentId = student.id " +
            "WHERE subjectRegistration.openClassId=:openClassId " +
            "GROUP BY student.id, fullName")
    List<StudentInClassDTO> getStudentsInOpenClass(int openClassId);

    @Query("SELECT studentScore.score, subjectScore.id as subjectScoreId " +
            "FROM studentScore " +
            "JOIN subjectScore ON studentScore.subjectScoreId = subjectScore.id " +
            "WHERE studentScore.studentId=:studentId AND studentScore.openClassId=:openClassId")
    List<StudentScoreDTO> getStudentScoreAndSubjectScoreByStudentId(int studentId, int openClassId);

    @Update
    void updateStudentScore(StudentScore studentScore);
}
