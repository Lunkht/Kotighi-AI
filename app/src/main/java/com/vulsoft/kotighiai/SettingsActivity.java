package com.vulsoft.kotighiai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupTheme, radioGroupInterface;
    private RadioButton radioLight, radioDark, radioSystem;
    private RadioButton radioModern, radioClassic;
    private ImageButton btnBack;
    private Button btnLogout;
    private SharedPreferences settingsPrefs;
    private SharedPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsPrefs = getSharedPreferences("kotighi_settings", MODE_PRIVATE);
        userPrefs = getSharedPreferences("kotighi", MODE_PRIVATE);

        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        radioLight = findViewById(R.id.radioLight);
        radioDark = findViewById(R.id.radioDark);
        radioSystem = findViewById(R.id.radioSystem);
        radioGroupInterface = findViewById(R.id.radioGroupInterface);
        radioModern = findViewById(R.id.radioModern);
        radioClassic = findViewById(R.id.radioClassic);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(v -> finish());

        // Logout
        btnLogout.setOnClickListener(v -> {
            ApiClient.getInstance().logout();
            userPrefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Charger le mode actuel
        int mode = settingsPrefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
            radioLight.setChecked(true);
        } else if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
            radioDark.setChecked(true);
        } else {
            radioSystem.setChecked(true);
        }

        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int newMode;
            if (checkedId == R.id.radioLight) {
                newMode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radioDark) {
                newMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            settingsPrefs.edit().putInt("theme_mode", newMode).apply();
            AppCompatDelegate.setDefaultNightMode(newMode);
        });

        // Charger l'interface préférée
        boolean useModern = settingsPrefs.getBoolean("use_modern_interface", true);
        if (useModern) {
            radioModern.setChecked(true);
        } else {
            radioClassic.setChecked(true);
        }

        // Gérer le changement d'interface
        radioGroupInterface.setOnCheckedChangeListener((group, checkedId) -> {
            boolean modern = checkedId == R.id.radioModern;
            settingsPrefs.edit().putBoolean("use_modern_interface", modern).apply();
        });
    }
}
