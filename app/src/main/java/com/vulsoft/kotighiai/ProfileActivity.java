package com.vulsoft.kotighiai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView profileName = findViewById(R.id.profileName);
        TextView profileRole = findViewById(R.id.profileRole);
        TextView infoLogin = findViewById(R.id.infoLogin);
        TextView infoRole = findViewById(R.id.infoRole);

        btnBack.setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("kotighi", MODE_PRIVATE);
        String nom = prefs.getString("nom", "Utilisateur");
        String role = prefs.getString("role", "Invite");
        String login = prefs.getString("login", "non défini");

        profileName.setText(nom);
        profileRole.setText(role);
        infoLogin.setText(login);
        infoRole.setText(role);
    }
}
