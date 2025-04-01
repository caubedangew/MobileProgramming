package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.Major;

import java.util.List;

@Dao
public interface MajorDao {
    @Query("SELECT * FROM major")
    List<Major> getAllMajors();

    @Query("SELECT * FROM major WHERE id=:majorId")
    Major getMajorById(int majorId);

    @Insert
    void addMajors(Major... majors);

    @Delete
    void deleteMajor(Major major);
}
