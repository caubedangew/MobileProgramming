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
            "GROUP BY OpenClass.subjectId")
    List<Double> getAverageScoreOfOneStudent(int semesterId, int teacherId);

    @Query("SELECT OpenClass.subjectId, COUNT(*) as studentNumber " +
                "FROM SubjectRegistration " +
                "JOIN OpenClass ON OpenClass.id = SubjectRegistration.openClassId " +
                "JOIN TeacherAssignment ON TeacherAssignment.openClassId = openClass.id " +
                "WHERE semesterId=:semesterId AND TeacherAssignment.teacherId=:teacherId " +
            "GROUP BY OpenClass.subjectId")
    List<Integer> numberStudentInClass(int semesterId, int teacherId);
}
