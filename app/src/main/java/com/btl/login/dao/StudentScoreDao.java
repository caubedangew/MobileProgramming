package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.btl.login.dto.ScoreDTO;
import com.btl.login.dto.StudentInClassDTO;
import com.btl.login.dto.StudentScoreDTO;
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

    @Query("SELECT subjectRegistration.studentId as id, (student.firstName || ' ' || student.lastName) as fullName, COUNT(*) " +
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
    @Query("SELECT s.firstName || ' ' || s.lastName AS studentFullName, "
            + "t.firstName || ' ' || t.lastName AS teacherFullName, "
            + "sub.subjectName AS subjectName, "
            + "sem.semesterName AS semesterName, "
            + "ay.academicYearName AS academicYearName, "
            + "(SELECT ss.score FROM StudentScore ss JOIN SubjectScore subj ON ss.subjectScoreId = subj.id WHERE subj.scoreType = 'Quá trình' AND ss.studentId = s.id AND ss.openClassId = oc.id) AS processScore, "
            + "(SELECT ss.score FROM StudentScore ss JOIN SubjectScore subj ON ss.subjectScoreId = subj.id WHERE subj.scoreType = 'Giữa kỳ' AND ss.studentId = s.id AND ss.openClassId = oc.id) AS midtermScore, "
            + "(SELECT ss.score FROM StudentScore ss JOIN SubjectScore subj ON ss.subjectScoreId = subj.id WHERE subj.scoreType = 'Cuối kỳ' AND ss.studentId = s.id AND ss.openClassId = oc.id) AS finalScore "
            + "FROM Student s "
            + "JOIN StudentClass c ON c.id = s.studentClassId "
            + "JOIN OpenClass oc ON oc.classId = c.id "
            + "JOIN TeacherAssignment ta ON ta.openClassId = oc.id "
            + "JOIN Teacher t ON t.id = ta.teacherId "
            + "JOIN Subject sub ON sub.id = oc.subjectId "
            + "JOIN Semester sem ON sem.id = oc.semesterId "
            + "JOIN AcademicYear ay ON ay.id = sem.academicYearId "
            + "WHERE c.className = :className")
    List<ScoreDTO> getScoresByClassName(String className);

    @Query("SELECT className FROM StudentClass")
    List<String> getAllClassNames();

}

