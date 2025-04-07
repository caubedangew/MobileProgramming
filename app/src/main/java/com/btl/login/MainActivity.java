package com.btl.login;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.btl.login.configurations.AppDatabase;
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
import com.btl.login.userViewModel.UserViewModel;
import com.btl.login.utils.DateUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    public Toolbar toolbar;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private UserViewModel userViewModel;
    NavigationView navView;
    Menu navMenu;
    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        init();

        userViewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                navMenu.findItem(R.id.login).setVisible(false);
                navMenu.findItem(R.id.register).setVisible(false);
                navMenu.findItem(R.id.user).setVisible(true);
                navMenu.findItem(R.id.logout).setVisible(true);
            } else {
                navMenu.findItem(R.id.login).setVisible(true);
                navMenu.findItem(R.id.register).setVisible(true);
                navMenu.findItem(R.id.user).setVisible(false);
                navMenu.findItem(R.id.logout).setVisible(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            appDatabase = AppDatabase.getDatabase(getApplicationContext());
            initData();
        });
    }

    private void init() {
        navView = findViewById(R.id.nav_view);
        navMenu = navView.getMenu();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        Fragment userFragment = new UserFragment();
        Fragment teachingSubjectsFragment = new TeachingSubjectsFragment();
        Fragment statisticsFragment = new UserStatisticsFragment();
        Fragment loginFragment = new LoginFragment();
        Fragment registerFragment = new RegisterFragment();

        setCurrentFragment(LoginFragment.newInstance("lttvu3003@gmail.com.vn"));

        defineNavigationTab(navView, new Fragment[]{userFragment, teachingSubjectsFragment, statisticsFragment, loginFragment, registerFragment, userFragment});

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            // Called when the back button is pressed.
            @Override
            public void handleOnBackPressed() {
                // Check if the drawer is open
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // Close the drawer if it's open
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Finish the activity if the drawer is closed
                    finish();
                }
            }
        });
    }

    private void defineNavigationTab(NavigationView navigationView, Fragment[] listFragment) {
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.close();
            toolbar.setTitle(item.getTitle());
            item.setChecked(!item.isChecked());
            int id = item.getItemId();
            if (R.id.home == id) {
                setCurrentFragment(listFragment[0]);
            } else if (R.id.statistics == id) {
                setCurrentFragment(listFragment[2]);
            } else if (R.id.inputScore == id) {
                setCurrentFragment(listFragment[1]);
            } else if (R.id.login == id) {
                setCurrentFragment(listFragment[3]);
            } else if (R.id.register == id) {
                setCurrentFragment(listFragment[4]);
            } else if (R.id.user == id) {
                setCurrentFragment(listFragment[5]);
            } else if (R.id.logout == id) {
                mAuth.signOut();
                userViewModel.setLoggedIn(false);
                setCurrentFragment(listFragment[3]);
            } else return false;
            return true; // Return true to indicate that the item was handled
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void initData() {
//        appDatabase.teacherDao().addTeachers(new Teacher("Le", "Vu", "lttvu3003@gmail.com.vn", 1));
        new Thread(() -> {
            List<Department> departments = appDatabase.departmentDao().getAllDepartments();
            if (departments.isEmpty()) {
                Department[] listDepartment = {
                        new Department("Khoa Công nghệ thông tin"),
                        new Department("Khoa Điện tử Viễn thông"),
                        new Department("Khoa Cơ khí"),
                        new Department("Khoa Xây dựng"),
                        new Department("Khoa Quản trị kinh doanh"),
                        new Department("Khoa Marketing"),
                        new Department("Khoa Tài chính Ngân hàng"),
                        new Department("Khoa Kinh tế học"),
                        new Department("Khoa Ngôn ngữ Anh"),
                        new Department("Khoa Vật lý")
                };
                appDatabase.departmentDao().addDepartment(listDepartment);
            }
        }).start();
        List<Major> majors = appDatabase.majorDao().getAllMajors();
        if (majors.isEmpty()) {
            Major[] listMajor = {
                    new Major("Công nghệ phần mềm", 1),
                    new Major("Trí tuệ nhân tạo", 1),
                    new Major("Mạng máy tính", 1),
                    new Major("Viễn thông", 2),
                    new Major("Điện tử học", 2),
                    new Major("Cơ khí chế tạo", 3),
                    new Major("Kỹ thuật cơ khí", 3),
                    new Major("Kỹ thuật xây dựng", 4),
                    new Major("Kỹ thuật xây dựng dân dụng", 4),
                    new Major("Quản trị kinh doanh quốc tế", 5),
                    new Major("Quản trị tài chính", 5),
                    new Major("Marketing", 6),
                    new Major("Marketing kỹ thuật số", 6),
                    new Major("Tài chính", 7),
                    new Major("Ngân hàng", 7),
                    new Major("Quản lý dự án", 8),
                    new Major("Kinh tế học ứng dụng", 8),
                    new Major("Ngôn ngữ Anh", 9),
                    new Major("Dịch thuật Anh - Việt", 9),
                    new Major("Vật lý học ứng dụng", 10),
                    new Major("Vật lý kỹ thuật", 10),
                    new Major("Công nghệ sinh học", 1),
                    new Major("Hệ thống thông tin", 1),
                    new Major("Thiết kế đồ họa", 1),
                    new Major("Kỹ thuật phần mềm", 1),
                    new Major("Kỹ thuật điện", 2),
                    new Major("Kỹ thuật viễn thông", 2),
                    new Major("Cơ khí tự động hóa", 3),
                    new Major("Kỹ thuật xây dựng cầu đường", 4),
                    new Major("Kinh tế học", 8)
            };
            appDatabase.majorDao().addMajors(listMajor);
        }
        List<Teacher> teachers = appDatabase.teacherDao().getAllTeachers();
        if (teachers.isEmpty()) {
            Teacher[] listTeacher = {
                    new Teacher("Nguyễn", "An", "an.nguyen@university.com", 1),
                    new Teacher("Trần", "Bình", "binh.tran@university.com", 2),
                    new Teacher("Lê", "Cường", "cuong.le@university.com", 3),
                    new Teacher("Phạm", "Duy", "duy.pham@university.com", 4),
                    new Teacher("Vũ", "Hà", "ha.vu@university.com", 5),
                    new Teacher("Đặng", "Hải", "hai.dang@university.com", 6),
                    new Teacher("Hoàng", "Khoa", "khoa.hoang@university.com", 7),
                    new Teacher("Bùi", "Linh", "linh.bui@university.com", 8),
                    new Teacher("Dương", "Minh", "minh.duong@university.com", 9),
                    new Teacher("Lý", "Nghĩa", "nghia.ly@university.com", 10),
                    new Teacher("Đoàn", "Oanh", "oanh.doan@university.com", 1),
                    new Teacher("Cao", "Phong", "phong.cao@university.com", 2),
                    new Teacher("Ngô", "Quang", "quang.ngo@university.com", 3),
                    new Teacher("Trương", "Quyền", "quyen.truong@university.com", 4),
                    new Teacher("Lê", "Sơn", "son.le@university.com", 5),
                    new Teacher("Võ", "Thảo", "thao.vo@university.com", 6),
                    new Teacher("Tạ", "Tùng", "tung.ta@university.com", 7),
                    new Teacher("Phan", "Tú", "tu.phan@university.com", 8),
                    new Teacher("Nguyễn", "Vân", "van.nguyen@university.com", 9),
                    new Teacher("Châu", "Yến", "yen.chau@university.com", 10),
                    new Teacher("Đỗ", "Bảo", "bao.do@university.com", 1),
                    new Teacher("Trịnh", "Cẩm", "cam.trinh@university.com", 2),
                    new Teacher("Hồ", "Đức", "duc.ho@university.com", 3),
                    new Teacher("Lâm", "Hương", "huong.lam@university.com", 4),
                    new Teacher("Nguyễn", "Mai", "mai.nguyen@university.com", 5),
                    new Teacher("Ngọc", "Mạnh", "manh.ngoc@university.com", 6),
                    new Teacher("Bạch", "Khang", "khang.bach@university.com", 7),
                    new Teacher("Hà", "Hoàng", "hoang.ha@university.com", 8),
                    new Teacher("Tú", "Hải", "hai.tu@university.com", 9),
                    new Teacher("Hữu", "Lâm", "lam.hu@university.com", 10),
                    new Teacher("Le", "Vu", "lttvu3003@gmail.com.vn", 1)
            };
            appDatabase.teacherDao().addTeachers(listTeacher);
        }
        new Thread(() -> {
            List<Subject> subjects = appDatabase.subjectDao().getAllSubjects();
            if (subjects.isEmpty()) {
                Subject[] listSubject = {
                        new Subject("Toán cao cấp", 3),
                        new Subject("Lập trình Java", 2.5),
                        new Subject("Hệ điều hành", 3),
                        new Subject("Cấu trúc dữ liệu", 2.5),
                        new Subject("Mạng máy tính", 3),
                        new Subject("Kỹ thuật lập trình", 2.5),
                        new Subject("Giải tích toán học", 3),
                        new Subject("Kỹ thuật phần mềm", 2.5),
                        new Subject("Cơ sở dữ liệu", 3),
                        new Subject("Lý thuyết đồ thị", 2.5),
                        new Subject("Học máy", 3),
                        new Subject("Trí tuệ nhân tạo", 3),
                        new Subject("Khoa học dữ liệu", 3),
                        new Subject("Phân tích thiết kế hệ thống", 2.5),
                        new Subject("Kỹ thuật số", 3),
                        new Subject("Mạng truyền thông", 2.5),
                        new Subject("Phương pháp nghiên cứu khoa học", 2),
                        new Subject("Hệ thống nhúng", 3),
                        new Subject("Lập trình Python", 2.5),
                        new Subject("Phân tích và thiết kế thuật toán", 3),
                        new Subject("Xử lý ảnh", 3),
                        new Subject("Lý thuyết xác suất", 2.5),
                        new Subject("Lý thuyết thông tin", 2.5),
                        new Subject("Hệ thống phân tán", 3),
                        new Subject("Lập trình Android", 2.5),
                        new Subject("Lập trình Web", 3),
                        new Subject("Cơ sở lý thuyết máy tính", 2.5),
                        new Subject("Ngôn ngữ lập trình C", 2.5),
                        new Subject("Lập trình hướng đối tượng", 3),
                        new Subject("Phát triển phần mềm Agile", 2.5),
                        new Subject("An toàn thông tin", 3),
                        new Subject("Quản trị hệ thống", 2.5),
                        new Subject("Công nghệ blockchain", 3),
                        new Subject("Công nghệ Web mới", 2.5),
                        new Subject("Tối ưu hóa hệ thống", 2.5),
                        new Subject("Điều khiển học", 2),
                        new Subject("Công nghệ AI trong game", 3),
                        new Subject("Đồ họa máy tính", 2.5),
                        new Subject("Hệ thống cơ sở dữ liệu phân tán", 3),
                        new Subject("Xử lý tín hiệu số", 3),
                        new Subject("Lập trình iOS", 2.5),
                        new Subject("Thực tập phần mềm", 3),
                        new Subject("Lý thuyết ngôn ngữ lập trình", 2.5),
                        new Subject("Phát triển ứng dụng di động", 3),
                        new Subject("Quản lý dự án phần mềm", 2.5),
                        new Subject("Kỹ thuật kiểm thử phần mềm", 3),
                        new Subject("Hệ thống thông tin", 2.5),
                        new Subject("Lý thuyết cơ sở dữ liệu", 3),
                        new Subject("Đảm bảo chất lượng phần mềm", 2.5),
                        new Subject("Hệ thống ERP", 2.5),
                        new Subject("Kỹ thuật lập trình nâng cao", 3),
                        new Subject("Công nghệ thực tế ảo", 2.5),
                        new Subject("Mô hình hóa hệ thống", 2.5)
                };
                appDatabase.subjectDao().addSubject(listSubject);
            }
        }).start();
        List<AcademicYear> academicYears = appDatabase.academicYearDao().getAllAcademicYears();
        if (academicYears.isEmpty()) {
            AcademicYear[] listAcademicYear = {
                    new AcademicYear("2021-2022"),
                    new AcademicYear("2022-2023"),
                    new AcademicYear("2023-2024"),
                    new AcademicYear("2024-2025"),
                    new AcademicYear("2025-2026"),
                    new AcademicYear("2026-2027"),
                    new AcademicYear("2027-2028"),
                    new AcademicYear("2028-2029"),
                    new AcademicYear("2029-2030"),
                    new AcademicYear("2030-2031")
            };
            appDatabase.academicYearDao().addAcademicYears(listAcademicYear);
        }
        List<Semester> semesters = appDatabase.semesterDao().getAllSemesters();
        if (semesters.isEmpty()) {
            Semester[] listSemester = {
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2021-09-01"), DateUtils.convertDateToTimestamp("2022-01-01"), 1),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2022-01-01"), DateUtils.convertDateToTimestamp("2022-05-01"), 1),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2022-05-01"), DateUtils.convertDateToTimestamp("2022-09-01"), 1),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2022-09-01"), DateUtils.convertDateToTimestamp("2023-01-01"), 2),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2023-01-01"), DateUtils.convertDateToTimestamp("2023-05-01"), 2),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2023-05-01"), DateUtils.convertDateToTimestamp("2023-09-01"), 2),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2023-09-01"), DateUtils.convertDateToTimestamp("2024-01-01"), 3),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2024-01-01"), DateUtils.convertDateToTimestamp("2024-05-01"), 3),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2024-05-01"), DateUtils.convertDateToTimestamp("2024-09-01"), 3),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2024-09-01"), DateUtils.convertDateToTimestamp("2025-01-01"), 4),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2025-01-01"), DateUtils.convertDateToTimestamp("2025-05-01"), 4),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2025-05-01"), DateUtils.convertDateToTimestamp("2025-09-01"), 4),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2025-09-01"), DateUtils.convertDateToTimestamp("2026-01-01"), 5),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2026-01-01"), DateUtils.convertDateToTimestamp("2026-05-01"), 5),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2026-05-01"), DateUtils.convertDateToTimestamp("2026-09-01"), 5),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2026-09-01"), DateUtils.convertDateToTimestamp("2027-01-01"), 6),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2027-01-01"), DateUtils.convertDateToTimestamp("2027-05-01"), 6),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2027-05-01"), DateUtils.convertDateToTimestamp("2027-09-01"), 6),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2027-09-01"), DateUtils.convertDateToTimestamp("2028-01-01"), 7),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2028-01-01"), DateUtils.convertDateToTimestamp("2028-05-01"), 7),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2028-05-01"), DateUtils.convertDateToTimestamp("2028-09-01"), 7),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2028-09-01"), DateUtils.convertDateToTimestamp("2029-01-01"), 8),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2029-01-01"), DateUtils.convertDateToTimestamp("2029-05-01"), 8),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2029-05-01"), DateUtils.convertDateToTimestamp("2029-09-01"), 8),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2029-09-01"), DateUtils.convertDateToTimestamp("2030-01-01"), 9),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2030-01-01"), DateUtils.convertDateToTimestamp("2030-05-01"), 9),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2030-05-01"), DateUtils.convertDateToTimestamp("2030-09-01"), 9),
                    new Semester("Học kì 1", DateUtils.convertDateToTimestamp("2030-09-01"), DateUtils.convertDateToTimestamp("2031-01-01"), 10),
                    new Semester("Học kì 2", DateUtils.convertDateToTimestamp("2031-01-01"), DateUtils.convertDateToTimestamp("2031-05-01"), 10),
                    new Semester("Học kì 3", DateUtils.convertDateToTimestamp("2031-05-01"), DateUtils.convertDateToTimestamp("2031-09-01"), 10)
            };
            appDatabase.semesterDao().addSemesters(listSemester);
        }
        List<StudentClass> listClass = appDatabase.studentClassDao().getAllClasses();
        if (listClass.isEmpty()) {
            StudentClass[] studentClasses = new StudentClass[90];
            int index = 0;
            for (Major major : majors) {
                StringBuilder abbreviation = new StringBuilder();
                String[] words = major.getMajorName().split("\\s+");
                for (String word : words) {
                    abbreviation.append(word.charAt(0)); // Lấy chữ cái đầu của mỗi từ
                }
                for (int i = 1; i <= 3; i++) {
                    String className = abbreviation + "0" + i;
                    studentClasses[index++] = new StudentClass(className.toUpperCase(), major.getId());
                }
            }
            appDatabase.studentClassDao().addClasses(studentClasses);
        }
        List<OpenClass> listOpenClass = appDatabase.openClassDao().getAllOpenClasses();
        if (listOpenClass.isEmpty()) {
            OpenClass[] openClasses = new OpenClass[30];
            for (int i = 0; i < 30; i++)
                openClasses[i] = new OpenClass("ABC", Integer.parseInt(String.valueOf(Math.round(Math.random() * 69 + 1))),
                        11, i == 0? 25 : Integer.parseInt(String.valueOf(Math.round(Math.random() * 49 + 1))), (i * 3) + 1);
            appDatabase.openClassDao().addOpenClasses(openClasses);
        }
        List<TeacherAssignment> listTeacherAssignment = appDatabase.teacherAssignmentDao().getAllTeacherAssignments();
        if (listTeacherAssignment.isEmpty()) {
            TeacherAssignment[] teacherAssignments = {
                    new TeacherAssignment(31, 1),
                    new TeacherAssignment(31, 10),
                    new TeacherAssignment(31, 4),
                    new TeacherAssignment(31, 18),
                    new TeacherAssignment(31, 5),
                    new TeacherAssignment(31, 9),
            };
            appDatabase.teacherAssignmentDao().addTeacherAssignments(teacherAssignments);
        }
        List<Student> listStudent = appDatabase.studentDao().getAllStudents();
        if (listStudent.isEmpty()) {
            Student[] students = {
                    new Student("Nguyen", "Anh Tu", "nguyen.anh.tu1@example.com", 1),
                    new Student("Tran", "Thao Nguyen", "tran.thao.nguyen2@example.com", 2),
                    new Student("Le", "Quang Hieu", "le.quang.hieu3@example.com", 3),
                    new Student("Pham", "Thi Thanh", "pham.thi.thanh4@example.com", 4),
                    new Student("Bui", "Minh Tuan", "bui.minh.tuan5@example.com", 5),
                    new Student("Nguyen", "Hoang Nam", "nguyen.hoang.nam6@example.com", 6),
                    new Student("Doan", "Thu Ha", "doan.thu.ha7@example.com", 7),
                    new Student("Mai", "Quoc Tuan", "mai.quoc.tuan8@example.com", 8),
                    new Student("Nguyen", "Phuc Anh", "nguyen.phuc.anh9@example.com", 9),
                    new Student("Tran", "Bao Ngoc", "tran.bao.ngoc10@example.com", 10),
                    new Student("Phan", "Quy Duy", "phan.quy.duy11@example.com", 11),
                    new Student("Le", "Mai Linh", "le.mai.linh12@example.com", 12),
                    new Student("Nguyen", "Tu Thanh", "nguyen.tu.thanh13@example.com", 13),
                    new Student("Hoang", "Anh Khoi", "hoang.anh.khoi14@example.com", 14),
                    new Student("Bui", "Chi Mai", "bui.chi.mai15@example.com", 15),
                    new Student("Vu", "Thien Tuan", "vu.thien.tuan16@example.com", 16),
                    new Student("Nguyen", "Gia Bao", "nguyen.gia.bao17@example.com", 17),
                    new Student("Doan", "Thanh Son", "doan.thanh.son18@example.com", 18),
                    new Student("Mai", "Thi Kim", "mai.thi.kim19@example.com", 19),
                    new Student("Phan", "Minh Son", "phan.minh.son20@example.com", 20),
                    new Student("Nguyen", "Viet Kien", "nguyen.viet.kien21@example.com", 21),
                    new Student("Tran", "Quyên Linh", "tran.quyen.linh22@example.com", 22),
                    new Student("Le", "Hong Thanh", "le.hong.thanh23@example.com", 23),
                    new Student("Pham", "Hong Lam", "pham.hong.lam24@example.com", 24),
                    new Student("Bui", "Thi Thu", "bui.thi.thu25@example.com", 25),
                    new Student("Nguyen", "Thi Kim", "nguyen.thi.kim26@example.com", 26),
                    new Student("Doan", "Hoang Tuan", "doan.hoang.tuan27@example.com", 27),
                    new Student("Mai", "Hong Anh", "mai.hong.anh28@example.com", 28),
                    new Student("Phan", "Lan Mai", "phan.lan.mai29@example.com", 29),
                    new Student("Nguyen", "Mai Quyen", "nguyen.mai.quyen30@example.com", 30),
                    new Student("Tran", "Phong Nam", "tran.phong.nam31@example.com", 31),
                    new Student("Le", "Thao Thi", "le.thao.thi32@example.com", 32),
                    new Student("Nguyen", "Duy Linh", "nguyen.duy.linh33@example.com", 33),
                    new Student("Hoang", "Minh Tuan", "hoang.minh.tuan34@example.com", 34),
                    new Student("Bui", "Thanh Son", "bui.thanh.son35@example.com", 35),
                    new Student("Vu", "Nguyen Kim", "vu.nguyen.kim36@example.com", 36),
                    new Student("Nguyen", "Thi Thi", "nguyen.thi.thi37@example.com", 37),
                    new Student("Doan", "Viet Thi", "doan.viet.thi38@example.com", 38),
                    new Student("Mai", "Quang Duong", "mai.quang.duong39@example.com", 39),
                    new Student("Phan", "Tu Anh", "phan.tu.anh40@example.com", 40),
                    new Student("Nguyen", "Hien Thi", "nguyen.hien.thi41@example.com", 41),
                    new Student("Tran", "Duy Thi", "tran.duy.thi42@example.com", 42),
                    new Student("Le", "Quoc Thu", "le.quoc.thu43@example.com", 43),
                    new Student("Pham", "Hong Thu", "pham.hong.thu44@example.com", 44),
                    new Student("Bui", "Bao Hong", "bui.bao.hong45@example.com", 45),
                    new Student("Nguyen", "Tien Thi", "nguyen.tien.thi46@example.com", 46),
                    new Student("Doan", "Thien Quang", "doan.thien.quang47@example.com", 47),
                    new Student("Mai", "Hieu Phu", "mai.hieu.phu48@example.com", 48),
                    new Student("Phan", "Minh Lan", "phan.minh.lan49@example.com", 49),
                    new Student("Nguyen", "Thao Tuan", "nguyen.thao.tuan50@example.com", 50),
                    new Student("Tran", "Ngoc Thi", "tran.ngoc.thi51@example.com", 1),
                    new Student("Le", "Thi Thanh", "le.thi.thanh52@example.com", 2),
                    new Student("Pham", "Quoc Phu", "pham.quoc.phu53@example.com", 3),
                    new Student("Bui", "Minh Thi", "bui.minh.thi54@example.com", 4),
                    new Student("Nguyen", "Thi Quyen", "nguyen.thi.quyen55@example.com", 5),
                    new Student("Doan", "Kim Thao", "doan.kim.thao56@example.com", 6),
                    new Student("Mai", "Thanh Nam", "mai.thanh.nam57@example.com", 7),
                    new Student("Phan", "Duy Thi", "phan.duy.thi58@example.com", 8),
                    new Student("Nguyen", "Ngoc Linh", "nguyen.ngoc.linh59@example.com", 9),
                    new Student("Tran", "Thien Tu", "tran.thien.tu60@example.com", 10),
                    new Student("Le", "Duy Hoa", "le.duy.hoa61@example.com", 11),
                    new Student("Pham", "Thu Thi", "pham.thu.thi62@example.com", 12),
                    new Student("Bui", "Quoc Tuan", "bui.quoc.tuan63@example.com", 13),
                    new Student("Nguyen", "Mai Quyen", "nguyen.mai.quyen64@example.com", 14),
                    new Student("Doan", "Hong Tuan", "doan.hong.tuan65@example.com", 15),
                    new Student("Mai", "Kim Tuan", "mai.kim.tuan66@example.com", 16),
                    new Student("Phan", "Bich Son", "phan.bich.son67@example.com", 17),
                    new Student("Nguyen", "Thi Thanh", "nguyen.thi.thanh68@example.com", 18),
                    new Student("Tran", "Lan Thi", "tran.lan.thi69@example.com", 19),
                    new Student("Le", "Thanh Thu", "le.thanh.thu70@example.com", 20),
                    new Student("Pham", "Quyen Tuan", "pham.quyen.tuan71@example.com", 21),
                    new Student("Bui", "Thi Lan", "bui.thi.lan72@example.com", 22),
                    new Student("Nguyen", "Mai Thi", "nguyen.mai.thi73@example.com", 23),
                    new Student("Doan", "Tuan Thi", "doan.tuan.thi74@example.com", 24),
                    new Student("Mai", "Son Tuan", "mai.son.tuan75@example.com", 25),
                    new Student("Phan", "Duy Thanh", "phan.duy.thanh76@example.com", 26),
                    new Student("Nguyen", "Linh Thi", "nguyen.linh.thi77@example.com", 27),
                    new Student("Tran", "Quyen Thi", "tran.quyen.thi78@example.com", 28),
                    new Student("Le", "Hieu Thi", "le.hieu.thi79@example.com", 29),
                    new Student("Pham", "Linh Tuan", "pham.linh.tuan80@example.com", 30),
                    new Student("Bui", "Thanh Minh", "bui.thanh.minh81@example.com", 31),
                    new Student("Nguyen", "Hong Thi", "nguyen.hong.thi82@example.com", 32),
                    new Student("Doan", "Tuan Thu", "doan.tuan.thu83@example.com", 33),
                    new Student("Mai", "Phuc Son", "mai.phuc.son84@example.com", 34),
                    new Student("Phan", "Thao Thi", "phan.thao.thi85@example.com", 35),
                    new Student("Nguyen", "Duy Tuan", "nguyen.duy.tuan86@example.com", 36),
                    new Student("Tran", "Lan Thi", "tran.lan.thi87@example.com", 37),
                    new Student("Le", "Quyen Minh", "le.quyen.minh88@example.com", 38),
                    new Student("Pham", "Hieu Thi", "pham.hieu.thi89@example.com", 39),
                    new Student("Bui", "Tuan Thi", "bui.tuan.thi90@example.com", 40),
                    new Student("Nguyen", "Quyen Linh", "nguyen.quyen.linh91@example.com", 41),
                    new Student("Doan", "Thien Thi", "doan.thien.thi92@example.com", 42),
                    new Student("Mai", "Tuan Quyen", "mai.tuan.quyen93@example.com", 43),
                    new Student("Phan", "Thanh Son", "phan.thanh.son94@example.com", 44),
                    new Student("Nguyen", "Kim Minh", "nguyen.kim.minh95@example.com", 45),
                    new Student("Tran", "Linh Thi", "tran.linh.thi96@example.com", 46),
                    new Student("Le", "Tuan Minh", "le.tuan.minh97@example.com", 47),
                    new Student("Pham", "Minh Thi", "pham.minh.thi98@example.com", 48),
                    new Student("Bui", "Thi Lan", "bui.thi.lan99@example.com", 49),
                    new Student("Nguyen", "Thu Thi", "nguyen.thu.thi100@example.com", 50)
            };
            appDatabase.studentDao().addStudents(students);
        }
        List<SubjectRegistration> listSubjectRegistration = appDatabase.subjectRegistrationDao().getAllSubjectRegistrations();
        if (listSubjectRegistration.isEmpty()) {
            SubjectRegistration[] subjectRegistrations = new SubjectRegistration[100];
            for (int i = 0; i < 100; i++) {
                subjectRegistrations[i] = new SubjectRegistration((i + 1) / 50 + 1, i + 1);
            }
            appDatabase.subjectRegistrationDao().addSubjectRegistrations(subjectRegistrations);
        }
        List<SubjectScore> listSubjectScore = appDatabase.subjectScoreDao().getAllSubjectScores();
        if (listSubjectScore.isEmpty()) {
            SubjectScore[] subjectScores = {
                    new SubjectScore(0.4, "Quá trình", "Điểm kiểm tra thường xuyên trong suốt khóa học, bao gồm bài tập, thảo luận nhóm và điểm tham gia", 25),
                    new SubjectScore(0.3, "Giữa kỳ", "Điểm thi giữa kỳ với hình thức tự luận và trắc nghiệm", 25),
                    new SubjectScore(0.3, "Cuối kỳ", "Điểm thi cuối kỳ với hình thức thi viết, tập trung vào kiến thức toàn bộ môn học", 25),

                    new SubjectScore(0.3, "Quá trình", "Điểm kiểm tra dựa trên các bài thuyết trình và báo cáo dự án", 4),
                    new SubjectScore(0.2, "Cuối kỳ", "Điểm thi cuối kỳ với hình thức thi trực tuyến và đề thi mở", 4),
                    new SubjectScore(0.5, "Giữa kỳ", "Điểm thi giữa kỳ bao gồm cả kiểm tra viết và thực hành", 4),

                    new SubjectScore(0.3, "Quá trình", "Điểm tham gia các buổi thảo luận nhóm và bài tập nhóm", 7),
                    new SubjectScore(0.3, "Giữa kỳ", "Điểm thi giữa kỳ với bài thi ngắn và kiểm tra thực hành", 7),
                    new SubjectScore(0.4, "Cuối kỳ", "Điểm thi cuối kỳ dạng trắc nghiệm và tự luận", 7),

                    new SubjectScore(0.3, "Quá trình", "Điểm kiểm tra trong các buổi học online và bài tập nhỏ hàng tuần", 27),
                    new SubjectScore(0.3, "Cuối kỳ", "Điểm thi cuối kỳ với các câu hỏi tổng hợp và đề thi dài", 27),
                    new SubjectScore(0.4, "Giữa kỳ", "Điểm giữa kỳ với các bài tập nhỏ và một bài kiểm tra", 27),

                    new SubjectScore(0.3, "Quá trình", "Điểm kiểm tra đánh giá mức độ hiểu bài qua các câu hỏi trên lớp", 33),
                    new SubjectScore(0.2, "Giữa kỳ", "Điểm kiểm tra giữa kỳ với các câu hỏi lý thuyết và thực hành", 33),
                    new SubjectScore(0.5, "Cuối kỳ", "Điểm thi cuối kỳ bao gồm bài thi viết và phần thuyết trình", 33),

                    new SubjectScore(0.4, "Cuối kỳ", "Điểm thi cuối kỳ bao gồm các câu hỏi lý thuyết và bài tập thực hành", 22),
                    new SubjectScore(0.2, "Giữa kỳ", "Điểm kiểm tra giữa kỳ gồm bài thi và bài tập nhóm", 22),
                    new SubjectScore(0.4, "Quá trình", "Điểm kiểm tra qua bài thuyết trình nhóm và bài tập thực hành", 22)
            };
            appDatabase.subjectScoreDao().addSubjectScores(subjectScores);
        }
        List<StudentScore> listStudentScore = appDatabase.studentScoreDao().getAllStudentScores();
        if (listStudentScore.isEmpty()) {
            StudentScore[] studentScores = new StudentScore[75];
            for (int i = 0; i < 25; i++) {
                for (int j = 0; j < 3; j++) {
                    studentScores[(i * 3) + j] = new StudentScore(Math.random() * 5 + 5, 1, i + 1, j + 1);
                }
            }
            appDatabase.studentScoreDao().addStudentScores(studentScores);
        }
    }
}