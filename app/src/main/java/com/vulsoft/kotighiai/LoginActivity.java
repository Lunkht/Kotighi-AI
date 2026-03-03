// ============================================================
//  KOTIGHI AI — Ecran de connexion Android
//  Fichier : app/src/main/java/com/kotighi/ai/LoginActivity.java
// ============================================================

package com.vulsoft.kotighiai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editLogin, editPassword;
    private Button btnConnexion;
    private ProgressBar progressBar;
    private TextView txtErreur, txtInscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Tentative de restauration de session
        SharedPreferences prefs = getSharedPreferences("kotighi", MODE_PRIVATE);
        String savedToken = prefs.getString("token", null);
        if (savedToken != null) {
            ApiClient.getInstance().setToken(savedToken);
        }

        // Si deja connecte, aller directement au dashboard
        if (ApiClient.getInstance().isLoggedIn()) {
            goToDashboard();
            return;
        }

        editLogin = findViewById(R.id.editLogin);
        editPassword = findViewById(R.id.editPassword);
        btnConnexion = findViewById(R.id.btnConnexion);
        progressBar = findViewById(R.id.progressBar);
        txtErreur = findViewById(R.id.txtErreur);
        txtInscription = findViewById(R.id.txtInscription);

        btnConnexion.setOnClickListener(v -> tenterConnexion());
        txtInscription.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void tenterConnexion() {
        String login = editLogin.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            txtErreur.setText("Veuillez remplir tous les champs.");
            txtErreur.setVisibility(View.VISIBLE);
            return;
        }

        // Afficher le loader
        btnConnexion.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        txtErreur.setVisibility(View.GONE);

        ApiClient.getInstance().login(login, password, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                String token = response.optString("access_token");
                String nom = response.optString("full_name");
                String role = response.optString("role");

                // Sauvegarder le token
                ApiClient.getInstance().setToken(token);

                // Sauvegarder le token, nom et role en SharedPreferences
                getSharedPreferences("kotighi", MODE_PRIVATE).edit()
                        .putString("token", token)
                        .putString("nom", nom)
                        .putString("role", role)
                        .apply();

                Toast.makeText(LoginActivity.this,
                        "Bienvenue " + nom, Toast.LENGTH_SHORT).show();
                goToDashboard();
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnConnexion.setEnabled(true);
                txtErreur.setText(errorMessage);
                txtErreur.setVisibility(View.VISIBLE);
            }
        });
    }

    private void goToDashboard() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
