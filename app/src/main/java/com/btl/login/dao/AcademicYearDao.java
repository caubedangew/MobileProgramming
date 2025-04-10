package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.AcademicYear;

import java.util.List;

@Dao
public interface AcademicYearDao {
    @Query("SELECT * FROM academicYear")
    List<AcademicYear> getAllAcademicYears();

    @Query("SELECT * FROM academicYear WHERE id=:academicYearId")
    AcademicYear getAcademicYearById(int academicYearId);

    @Insert
    void addAcademicYears(AcademicYear... academicYears);

    @Delete
    void deleteAcademicYear(AcademicYear academicYear);

    @Query("DELETE FROM academicYear")
    void deleteAllAcademicYears();
}
