package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public abstract class BaseProperties {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(defaultValue = "true")
    private boolean active;
    private Long createdBy;
    private Long updatedBy;

    public BaseProperties() {
        this.updatedBy = System.currentTimeMillis();
        ;
        this.createdBy = System.currentTimeMillis();
        ;
        this.active = true;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
