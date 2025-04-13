package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.btl.login.dto.StudentDTO;
import com.btl.login.entities.Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM student")
    List<Student> getAllStudents();

    @Query("SELECT * FROM student WHERE id=:studentId")
    Student getStudentById(int studentId);

    @Insert
    void addStudents(Student... students);

    @Delete
    void deleteStudent(Student student);

    @Query("SELECT s.id, s.firstName, s.lastName, s.email, c.className " +
            "FROM Student s INNER JOIN StudentClass c ON s.studentClassId = c.id")
    List<StudentDTO> getAllStudentsWithClassName();

    @Query("SELECT COUNT(*) FROM student WHERE email = :email")
    int checkEmailExists(String email);

    @Update
    void updateStudent(Student student);

    @Query("DELETE FROM student WHERE id = :studentId")
    void deleteStudentById(int studentId);

}
