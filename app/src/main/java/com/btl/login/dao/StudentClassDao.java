package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.btl.login.dto.StudentClassDTO;
import com.btl.login.entities.StudentClass;

import java.util.List;

@Dao
public interface StudentClassDao {
    @Query("SELECT * FROM studentClass")
    List<StudentClass> getAllClasses();

    @Query("SELECT * FROM studentClass WHERE id=:studentClassId")
    StudentClass getClassById(int studentClassId);

    @Insert
    void addClasses(StudentClass... studentStudentClasses);

    @Delete
    void deleteClass(StudentClass studentClass);

    @Query("SELECT COUNT(*) FROM studentClass WHERE className = :className")
    int checkClassExists(String className);

    @Update
    void updateClass(StudentClass studentClass);

    @Query("SELECT className FROM studentclass")
    List<String> getAllClassNames();

    @Query("SELECT id FROM studentClass WHERE className = :className LIMIT 1")
    int getClassIdByName(String className);

    @Query("SELECT studentClass.id, studentClass.className, " +
            "COALESCE(studentClass.majorId, 0) AS majorId, " +
            "COALESCE(major.majorName, 'Chưa xác định') AS majorName " +
            "FROM studentClass " +
            "LEFT JOIN major ON studentClass.majorId = major.id")
    List<StudentClassDTO> getClassesWithMajorName();
}
