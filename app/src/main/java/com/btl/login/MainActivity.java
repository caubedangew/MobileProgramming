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
import com.btl.login.entities.Semester;
import com.btl.login.entities.Subject;
import com.btl.login.entities.Teacher;
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
                    new Teacher("Hữu", "Lâm", "lam.hu@university.com", 10)
            };
            appDatabase.teacherDao().addTeachers(listTeacher);
        }
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
    }
}