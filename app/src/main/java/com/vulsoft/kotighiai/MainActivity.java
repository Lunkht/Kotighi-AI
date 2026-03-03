package com.vulsoft.kotighiai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txtBienvenue, txtRole;
    private View cardCyber, cardSante;
    private ImageButton btnSettings, btnHistorique, btnNotification, btnProfile;
    private ImageView fabChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        txtBienvenue = findViewById(R.id.txtBienvenue);
        txtRole = findViewById(R.id.txtRole);
        cardCyber = findViewById(R.id.cardCyber);
        cardSante = findViewById(R.id.cardSante);
        btnSettings = findViewById(R.id.settings);
        fabChat = findViewById(R.id.fabChat);
        btnHistorique = findViewById(R.id.btnHistorique);
        btnNotification = findViewById(R.id.btnNotification);
        btnProfile = findViewById(R.id.btnProfile);

        // Charger infos utilisateur
        SharedPreferences prefs = getSharedPreferences("kotighi", MODE_PRIVATE);
        String nom = prefs.getString("nom", "Utilisateur");
        String role = prefs.getString("role", "Invite");

        txtBienvenue.setText("Bonjour, " + nom);
        txtRole.setText(role);

        // Charger la préférence d'interface
        SharedPreferences settingsPrefs = getSharedPreferences("kotighi_settings", MODE_PRIVATE);
        boolean useModern = settingsPrefs.getBoolean("use_modern_interface", true);

        // Navigation - Interface selon préférence
        cardCyber.setOnClickListener(v -> {
            if (useModern) {
                startActivity(new Intent(this, CyberActivityModern.class));
            } else {
                startActivity(new Intent(this, CyberActivity.class));
            }
        });
        cardSante.setOnClickListener(v -> startActivity(new Intent(this, SanteActivity.class)));
        fabChat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        btnHistorique.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}
