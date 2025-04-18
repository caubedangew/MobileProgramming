package com.btl.login.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.btl.login.entities.Department;

import java.util.List;

@Dao
public interface DepartmentDao {
    @Query("SELECT * FROM department")
    List<Department> getAllDepartments();

    @Query("SELECT * FROM department WHERE id=:departmentId")
    Department getDepartmentById(int departmentId);

    @Insert
    void addDepartment(Department... departments);

    @Delete
    void deleteDepartment(Department department);

    @Query("DELETE FROM department")
    void deleteAllDepartments();

    @Query("SELECT departmentName FROM department")
    List<String> getAllDepartmentNames();

    @Query("SELECT id FROM department WHERE departmentName = :departmentName")
    int getDepartmentIdByName(String departmentName);
}
