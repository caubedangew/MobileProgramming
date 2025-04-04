package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
}
