package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.btl.login.dto.StatisticsDTO;

import java.util.List;

@Dao
public interface StatisticsDao {
    @Query("SELECT subject.subjectName, COUNT(*) as avarageScore FROM StudentScore " +
            "JOIN SubjectScore ON StudentScore.subjectScoreId = SubjectScore.id " +
            "JOIN Subject ON Subject.id = SubjectScore.subjectId " +
            "JOIN OpenClass ON StudentScore.openClassId = openClass.id " +
            "WHERE openClass.semesterId=:semesterId")
    List<StatisticsDTO> getStatisticsBySubject(int semesterId);
}
