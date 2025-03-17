package com.btl.login.entities;

import androidx.room.PrimaryKey;

import java.sql.Date;

public abstract class BaseProperties {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private Date createdBy;
    private Date updatedBy;

    public Date getCreatedBy() {
        return createdBy;
    }

    public int getId() {
        return id;
    }

    public Date getUpdatedBy() {
        return updatedBy;
    }

    public void setCreatedBy(Date createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(Date updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setId(int id) {
        this.id = id;
    }
}
