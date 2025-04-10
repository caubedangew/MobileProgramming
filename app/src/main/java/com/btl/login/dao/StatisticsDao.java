package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.btl.login.dto.StatisticsDTO;
import com.btl.login.dto.TestDTO;

import java.util.List;

@Dao
public interface StatisticsDao {
    @Query("SELECT subjectName FROM Subject " +
            "JOIN OpenClass ON OpenClass.subjectId = Subject.id " +
            "JOIN TeacherAssignment ON TeacherAssignment.openClassId = openClass.id " +
            "WHERE openClass.semesterId=:semesterId AND teacherAssignment.teacherId=:teacherId " +
            "GROUP BY subjectName")
    List<String> getStatisticsBySemester(int semesterId, int teacherId);

    @Query("SELECT SUM(averageScore) FROM (SELECT OpenClass.id as openClassId, SUM(SubjectScore.weight * StudentScore.score) " +
                " AS averageScore FROM StudentScore " +
                "JOIN SubjectScore ON StudentScore.subjectScoreId = SubjectScore.id " +
                "JOIN OpenClass ON StudentScore.openClassId = OpenClass.id " +
                "JOIN Student ON Student.id = StudentScore.studentId " +
                "JOIN TeacherAssignment ON TeacherAssignment.openClassId = openClass.id " +
                "JOIN Semester ON Semester.id = openClass.semesterId " +
                "WHERE Semester.id=:semesterId AND teacherAssignment.teacherId=:teacherId " +
                "GROUP BY StudentScore.studentId) as sumByClass " +
            "JOIN OpenClass ON OpenClass.id = sumByClass.openClassId " +
            "GROUP BY OpenClass.id")
    List<Double> getAverageScoreOfOneStudent(int semesterId, int teacherId);

    @Query("SELECT COUNT(*) " +
            "FROM SubjectRegistration " +
            "JOIN OpenClass ON OpenClass.id = SubjectRegistration.openClassId " +
            "JOIN TeacherAssignment ON TeacherAssignment.openClassId = openClass.id " +
            "WHERE subjectId = 25 AND TeacherAssignment.teacherId=:teacherId")
    int numberStudentInClass(int teacherId);

    @Query("SELECT COUNT(*) " +
            "FROM Subject " +
            "JOIN OpenClass ON OpenClass.subjectId = Subject.id " +
            "JOIN Semester On Semester.id = OpenClass.semesterId " +
            "JOIN TeacherAssignment ON TeacherAssignment.openClassId = openClass.id " +
            "WHERE SemesterId=11 AND SubjectId=25 AND TeacherAssignment.teacherId=:teacherId")
    int numberClassOfSubjectOnTheSemester(int teacherId);
}
