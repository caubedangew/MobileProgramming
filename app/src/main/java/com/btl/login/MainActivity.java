package com.btl.login;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.btl.login.configurations.AppDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_bar);

        Fragment userFragement = new UserFragment();
//        Fragment homeFragment = new HomeFragment();
//        Fragment feature1Fragment = new Feature1Fragment();
        setCurrentFragment(userFragement);

        defineBottomNavigationTab(bottomNavigationView, userFragement);

        AppDatabase appDatabase = Room.databaseBuilder(this, AppDatabase.class, "studentManagementDatabase").build();
    }

    private void defineBottomNavigationTab(BottomNavigationView bottomNavigationView, Fragment loginFragment) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (R.id.home == id) {
                setCurrentFragment(loginFragment);
            } else if (R.id.login == id) {
            } else if (R.id.feature1 == id) {
            } else return false;
            return true; // Return true to indicate that the item was handled
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, fragment)
                .commit();
    }
}