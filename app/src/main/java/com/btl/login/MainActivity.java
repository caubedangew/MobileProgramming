package com.btl.login;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.btl.login.configurations.AppDatabase;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    public Toolbar toolbar;
    FirebaseAuth mAuth;

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
        Fragment teachingSubjectsFragment = new TeachingSubjectsFragment();
        Fragment statisticsFragment = new UserStatisticsFragment();
        Fragment loginFragment = new LoginFragment();
        Fragment registerFragment = new RegisterFragment();

        setCurrentFragment(userFragment);

        defineNavigationTab(navigationView, new Fragment[]{userFragment, teachingSubjectsFragment, statisticsFragment, loginFragment, registerFragment});

        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());

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
            } else if (R.id.logout == id) {
                mAuth.signOut();
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
}