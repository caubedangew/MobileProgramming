package com.btl.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Objects;

public class InputScorePage extends Activity {

    ArrayList<Objects> subjects = new ArrayList<>();
    ArrayList<Objects> classes = new ArrayList<>();
    ArrayList<Objects> students = new ArrayList<>();

    Spinner listSubjects, listClasses, listStudents;

    Button btnSave, btnSaveAndContinue;

    EditText eTxtMiddleSemesterScore, eTxtFinalSemesterScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_score_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listSubjects = findViewById(R.id.listSubjects);
        listClasses = findViewById(R.id.listClasses);
        listStudents = findViewById(R.id.listStudents);
        btnSave = findViewById(R.id.btnSave);
        btnSaveAndContinue = findViewById(R.id.btnSaveAndContinue);
        eTxtMiddleSemesterScore = findViewById(R.id.eTxtMiddleSemesterScore);
        eTxtFinalSemesterScore = findViewById(R.id.eTxtFinalSemesterScore);

        try {
            ArrayAdapter<Objects> array = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, subjects);

            listSubjects.setAdapter(array);
            listSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btnSave.setOnClickListener(v -> {

            });

            btnSaveAndContinue.setOnClickListener(v -> {

            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
    }
}