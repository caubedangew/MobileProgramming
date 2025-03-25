package com.btl.login;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.btl.login.configurations.AppDatabase;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        Fragment userFragment = new UserFragment();
//        Fragment homeFragment = new HomeFragment();
//        Fragment feature1Fragment = new Feature1Fragment();
        setCurrentFragment(userFragment);

        defineNavigationTab(navigationView, new Fragment[] {userFragment, null, null});

        AppDatabase appDatabase = Room.databaseBuilder(this, AppDatabase.class, "studentManagementDatabase").build();

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
            } else if (R.id.input_score == id) {
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
}