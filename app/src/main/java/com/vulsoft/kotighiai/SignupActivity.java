package com.vulsoft.kotighiai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private EditText editNom, editLogin, editPassword, editEmail, editPhone, editAddress, editCountry, editOrganisation;
    private Button btnInscription;
    private ProgressBar progressBar;
    private TextView txtErreur, txtConnexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editNom = findViewById(R.id.editNom);
        editLogin = findViewById(R.id.editLogin);
        editPassword = findViewById(R.id.editPassword);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        editCountry = findViewById(R.id.editCountry);
        editOrganisation = findViewById(R.id.editOrganisation);
        btnInscription = findViewById(R.id.btnInscription);
        progressBar = findViewById(R.id.progressBar);
        txtErreur = findViewById(R.id.txtErreur);
        txtConnexion = findViewById(R.id.txtConnexion);

        btnInscription.setOnClickListener(v -> tenterInscription());
        txtConnexion.setOnClickListener(v -> finish()); // Retour a la connexion
    }

    private void tenterInscription() {
        String nom = editNom.getText().toString().trim();
        String login = editLogin.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String country = editCountry.getText().toString().trim();
        String organisation = editOrganisation.getText().toString().trim();

        if (nom.isEmpty() || login.isEmpty() || password.isEmpty() || email.isEmpty()) {
            txtErreur.setText("Veuillez remplir au moins le nom, le login, l'email et le mot de passe.");
            txtErreur.setVisibility(View.VISIBLE);
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtErreur.setText("Veuillez entrer une adresse email valide.");
            txtErreur.setVisibility(View.VISIBLE);
            return;
        }

        if (password.length() < 6) {
            txtErreur.setText("Le mot de passe doit contenir au moins 6 caractères.");
            txtErreur.setVisibility(View.VISIBLE);
            return;
        }

        // Afficher le loader
        btnInscription.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        txtErreur.setVisibility(View.GONE);

        ApiClient.getInstance().signup(login, password, email, nom, address, phone, country, organisation,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignupActivity.this, "Compte cree avec succes !", Toast.LENGTH_LONG).show();

                        // Rediriger vers la connexion
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        btnInscription.setEnabled(true);
                        txtErreur.setText(errorMessage);
                        txtErreur.setVisibility(View.VISIBLE);
                    }
                });
    }
}
