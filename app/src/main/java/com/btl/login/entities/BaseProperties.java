package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.sql.Date;

public abstract class BaseProperties {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(defaultValue = "true")
    private boolean active;
    private Long createdBy;
    private Long updatedBy;

    public BaseProperties() {
        this.updatedBy = System.currentTimeMillis();;
        this.createdBy = System.currentTimeMillis();;
        this.active = true;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public int getId() {
        return id;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
