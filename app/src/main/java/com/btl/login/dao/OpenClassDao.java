package com.btl.login.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.OpenClass;

import java.util.List;

public interface OpenClassDao {
    @Query("SELECT * FROM openClass")
    List<OpenClass> getAllOpenClasses();

    @Query("SELECT * FROM openClass WHERE id=:openClassId")
    OpenClass getOpenClassById(int openClassId);

    @Insert
    void addOpenClasses(OpenClass... openClasses);

    @Delete
    void deleteOpenClass(OpenClass openClass);
}
