package com.btl.login.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.SubjectRegistration;

import java.util.List;

public interface SubjectRegistrationDao {
    @Query("SELECT * FROM subjectRegistration")
    List<SubjectRegistration> getAllSubjectRegistrations();

    @Query("SELECT * FROM subjectRegistration WHERE id=:subjectRegistrationId")
    SubjectRegistration getSubjectRegistrationById(int subjectRegistrationId);

    @Insert
    void addSubjectRegistrations(SubjectRegistration... subjectRegistrations);

    @Delete
    void deleteSubjectRegistration(SubjectRegistration subjectRegistration);
}
