package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.dto.TeachingClassesDTO;
import com.btl.login.entities.OpenClass;

import java.util.List;

@Dao
public interface OpenClassDao {
    @Query("SELECT * FROM openClass")
    List<OpenClass> getAllOpenClasses();

    @Query("SELECT * FROM openClass WHERE id=:openClassId")
    OpenClass getOpenClassById(int openClassId);

    @Insert
    void addOpenClasses(OpenClass... openClasses);

    @Delete
    void deleteOpenClass(OpenClass openClass);

    @Query("SELECT openClass.id as openClassId, subjectId, className, COUNT(*) as numberSubjectRegistration, " +
            "COUNT(*) as numberSubjectRegistration, " +
            "        (SELECT COUNT(DISTINCT studentScore.studentId) " +
            "        FROM studentScore " +
            "        INNER JOIN subjectScore ON subjectScore.id = studentScore.subjectScoreId " +
            "        WHERE studentScore.openClassId = openClass.id " +
            "        AND subjectScore.subjectId = :subjectId" +
            "        AND (SELECT COUNT(*) " +
            "            FROM subjectScore ss" +
            "            WHERE ss.subjectId = :subjectId" +
            "        ) = (SELECT COUNT(*) " +
            "            FROM studentScore ss2" +
            "            INNER JOIN subjectScore ON subjectScore.id = ss2.subjectScoreId" +
            "            WHERE ss2.studentId = studentScore.studentId " +
            "            AND ss2.openClassId = openClass.id " +
            "            AND subjectScore.subjectId = :subjectId)) as numberStudentHaveScore " +
            "FROM openClass " +
            "JOIN subjectRegistration ON openClass.id = subjectRegistration.openClassId " +
            "JOIN studentClass ON openClass.classId = studentClass.id " +
            "JOIN teacherAssignment ON openClass.id = teacherAssignment.openClassId " +
            "WHERE openClass.subjectId = :subjectId AND teacherAssignment.teacherId = :userId " +
            "GROUP BY openClass.id, subjectId, className")
    List<TeachingClassesDTO> getTeachingClassesByUserAndOpenClass(int userId, int subjectId);

    @Query("SELECT COUNT(SubjectRegistration.openClassId) FROM SubjectRegistration " + "")

//            "JOIN TeacherAssignment ON TeacherAssignment.openClassId = SubjectRegistration.openClassId " +
//            "WHERE SubjectRegistration.openClassId=:openClassId AND teacherId=:teacherId " +
//            "GROUP BY SubjectRegistration.openClassId")
    int countSubjectScoreBySubjectId();

}
