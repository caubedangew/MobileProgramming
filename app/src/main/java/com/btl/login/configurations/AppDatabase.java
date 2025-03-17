package com.btl.login.configurations;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.btl.login.dao.SubjectDao;
import com.btl.login.entities.Subject;

@Database(entities = {
        Subject.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract SubjectDao subjectDao();
}
