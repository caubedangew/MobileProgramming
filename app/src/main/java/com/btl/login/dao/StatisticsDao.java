package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.btl.login.dto.StatisticsScoreDTO;
import com.btl.login.dto.StatisticsStudentNumberDTO;
import com.btl.login.dto.StatisticsSubjectDTO;

import java.util.List;

@Dao
public interface StatisticsDao {
    @Query("SELECT subjectId, subjectName " +
            "FROM Subject " +
            "JOIN OpenClass ON OpenClass.subjectId = Subject.id " +
            "JOIN TeacherAssignment ON TeacherAssignment.openClassId = openClass.id " +
            "WHERE openClass.semesterId=:semesterId AND teacherAssignment.teacherId=:teacherId " +
            "GROUP BY subjectName")
    List<StatisticsSubjectDTO> getStatisticsBySemester(int semesterId, int teacherId);

    @Query("SELECT OpenClass.subjectId, SUM(averageScore) as avarageScore " +
                "FROM (SELECT OpenClass.id as openClassId, SUM(SubjectScore.weight * StudentScore.score) " +
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
    List<StatisticsScoreDTO> getAverageScoreOfOneStudent(int semesterId, int teacherId);

    @Query("SELECT OpenClass.subjectId, SUM(studentNumber) as studentNumber FROM " +
                "(SELECT OpenClass.id as openClassId, COUNT(*) as studentNumber " +
                "FROM SubjectRegistration " +
                "JOIN OpenClass ON OpenClass.id = SubjectRegistration.openClassId " +
                "JOIN TeacherAssignment ON TeacherAssignment.openClassId = openClass.id " +
                "WHERE semesterId=:semesterId AND TeacherAssignment.teacherId=:teacherId " +
                "GROUP BY OpenClass.id) AS countByClass " +
            "JOIN OpenClass ON OpenClass.id = countByClass.openClassId " +
            "GROUP BY OpenClass.subjectId")
    List<StatisticsStudentNumberDTO> numberStudentInClass(int semesterId, int teacherId);

    @Query("SELECT StudentClass.className " +
            "FROM OpenClass " +
            "INNER JOIN StudentClass ON StudentClass.id = openClass.classId " +
            "INNER JOIN TeacherAssignment ON OpenClass.id = TeacherAssignment.openClassId " +
            "WHERE subjectId=:subjectId AND semesterId=:semesterId AND TeacherAssignment.teacherId=:teacherId")
    List<String> getStudentClassNameAndAverageScore(int subjectId, int semesterId, int teacherId);
}
