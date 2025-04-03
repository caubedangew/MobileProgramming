package com.btl.login.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import org.jetbrains.annotations.NotNull;

@Entity( foreignKeys = {
        @ForeignKey(
                entity = Subject.class,
                parentColumns = "id",
                childColumns = "subjectId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )
})
public class SubjectScore extends BaseProperties {
    @ColumnInfo(defaultValue = "0")
    private float weight;

    @NotNull
    private String scoreType;

    @NotNull
    private String description;

    private int subjectId;

    public SubjectScore(float weight, @NotNull String scoreType, @NotNull String description, int subjectId) {
        super();
        this.weight = weight;
        this.scoreType = scoreType;
        this.description = description;
        this.subjectId = subjectId;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public @NotNull String getScoreType() {
        return scoreType;
    }

    public void setScoreType(@NotNull String scoreType) {
        this.scoreType = scoreType;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
}
