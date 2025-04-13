package com.btl.login.configurations;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.btl.login.dao.AcademicYearDao;
import com.btl.login.dao.DepartmentDao;
import com.btl.login.dao.MajorDao;
import com.btl.login.dao.OpenClassDao;
import com.btl.login.dao.SemesterDao;
import com.btl.login.dao.StatisticsDao;
import com.btl.login.dao.StudentClassDao;
import com.btl.login.dao.StudentDao;
import com.btl.login.dao.StudentScoreDao;
import com.btl.login.dao.SubjectDao;
import com.btl.login.dao.SubjectRegistrationDao;
import com.btl.login.dao.SubjectScoreDao;
import com.btl.login.dao.TeacherAssignmentDao;
import com.btl.login.dao.TeacherDao;
import com.btl.login.entities.AcademicYear;
import com.btl.login.entities.Department;
import com.btl.login.entities.Major;
import com.btl.login.entities.OpenClass;
import com.btl.login.entities.Semester;
import com.btl.login.entities.Student;
import com.btl.login.entities.StudentClass;
import com.btl.login.entities.StudentScore;
import com.btl.login.entities.Subject;
import com.btl.login.entities.SubjectRegistration;
import com.btl.login.entities.SubjectScore;
import com.btl.login.entities.Teacher;
import com.btl.login.entities.TeacherAssignment;

@Database(entities = {
        AcademicYear.class,
        StudentClass.class,
        Department.class,
        Major.class,
        OpenClass.class,
        Semester.class,
        Student.class,
        StudentScore.class,
        Subject.class,
        SubjectRegistration.class,
        SubjectScore.class,
        Teacher.class,
        TeacherAssignment.class
}, version = 7, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "studentManagementDatabase")
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract SubjectDao subjectDao();

    public abstract AcademicYearDao academicYearDao();

    public abstract StudentClassDao studentClassDao();

    public abstract DepartmentDao departmentDao();

    public abstract MajorDao majorDao();

    public abstract SemesterDao semesterDao();

    public abstract StudentDao studentDao();

    public abstract StudentScoreDao studentScoreDao();

    public abstract SubjectScoreDao subjectScoreDao();

    public abstract TeacherDao teacherDao();

    public abstract OpenClassDao openClassDao();

    public abstract TeacherAssignmentDao teacherAssignmentDao();

    public abstract SubjectRegistrationDao subjectRegistrationDao();

    public abstract StatisticsDao statisticsDao();
}
