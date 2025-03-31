package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.SubjectScore;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SubjectScoreDao {
    @Query("SELECT * FROM subjectScore")
    List<SubjectScore> getAllSubjectScores();

    @Query("SELECT * FROM subjectScore WHERE id=:subjectScoreId")
    SubjectScore getSubjectScoreById(int subjectScoreId);

    @Insert
    void addSubjectScores(SubjectScore... subjectScores);

    @Delete
    void deleteSubjectScore(SubjectScore subjectScore);
}
