// ============================================================
//  KOTIGHI AI — Ecran Sante Android
//  Compatible FastAPI — 14 symptomes — 8 diagnostics
//  Fichier : app/src/main/java/com/kotighi/ai/SanteActivity.java
// ============================================================

package com.vulsoft.kotighiai;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SanteActivity extends AppCompatActivity {

    // ── Checkbox symptomes (14 au total) ─────────────────────
    private SwitchCompat cbFievre, cbToux, cbFatigue, cbTete, cbGorge;
    private SwitchCompat cbNausees, cbThorax, cbEssoufl, cbDiarrhee, cbFrissons;
    private SwitchCompat cbOdorat, cbMusculaire, cbPalpitations, cbVertiges;

    // ── Nouveaux Elements Blessures & Vitaux ─────────────────
    private Button btnModeSymptomes, btnModeBlessures;
    private LinearLayout containerSymptomes, containerBlessures;
    private EditText editPouls, editSpO2;
    private Spinner spinnerTypeBlessure, spinnerLocalisation;
    private SwitchCompat cbSaignement, cbDeformation, cbPerteConnaissance;

    private Button btnAnalyser;
    private LinearLayout layoutProgress;
    private ProgressBar progressBarHorizontal;
    private TextView txtProgressStatus;

    private boolean modeBlessure = false;

    // Resultats
    private LinearLayout layoutResultat, layoutConseils;
    private TextView txtDiagnostic, txtConfiance, txtUrgence, txtProbas;

    private static final String[] LABELS = {
            "COVID-19", "Grippe", "Problème cardiaque", "Gastro-entérite",
            "Migraine", "Angine", "Asthme/Stress", "Symptômes non spécifiques"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sante);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Mode Switcher
        btnModeSymptomes = findViewById(R.id.btnModeSymptomes);
        btnModeBlessures = findViewById(R.id.btnModeBlessures);
        containerSymptomes = findViewById(R.id.containerSymptomes);
        containerBlessures = findViewById(R.id.containerBlessures);

        btnModeSymptomes.setOnClickListener(v -> switchMode(false));
        btnModeBlessures.setOnClickListener(v -> switchMode(true));

        // Vitaux
        editPouls = findViewById(R.id.editPouls);
        editSpO2 = findViewById(R.id.editSpO2);

        // Groupe 1
        cbFievre = findViewById(R.id.cbFievre);
        cbToux = findViewById(R.id.cbToux);
        cbFatigue = findViewById(R.id.cbFatigue);
        cbTete = findViewById(R.id.cbTete);
        cbGorge = findViewById(R.id.cbGorge);
        // Groupe 2
        cbNausees = findViewById(R.id.cbNausees);
        cbThorax = findViewById(R.id.cbThorax);
        cbEssoufl = findViewById(R.id.cbEssoufl);
        cbDiarrhee = findViewById(R.id.cbDiarrhee);
        cbFrissons = findViewById(R.id.cbFrissons);
        // Groupe 3
        cbOdorat = findViewById(R.id.cbOdorat);
        cbMusculaire = findViewById(R.id.cbMusculaire);
        cbPalpitations = findViewById(R.id.cbPalpitations);
        cbVertiges = findViewById(R.id.cbVertiges);

        // Blessures
        spinnerTypeBlessure = findViewById(R.id.spinnerTypeBlessure);
        spinnerLocalisation = findViewById(R.id.spinnerLocalisation);
        cbSaignement = findViewById(R.id.cbSaignement);
        cbDeformation = findViewById(R.id.cbDeformation);
        cbPerteConnaissance = findViewById(R.id.cbPerteConnaissance);

        btnAnalyser = findViewById(R.id.btnAnalyser);
        layoutProgress = findViewById(R.id.layoutProgress);
        progressBarHorizontal = findViewById(R.id.progressBarHorizontal);
        txtProgressStatus = findViewById(R.id.txtProgressStatus);
        layoutResultat = findViewById(R.id.layoutResultat);
        layoutConseils = findViewById(R.id.layoutConseils);
        txtDiagnostic = findViewById(R.id.txtDiagnostic);
        txtConfiance = findViewById(R.id.txtConfiance);
        txtUrgence = findViewById(R.id.txtUrgence);
        txtProbas = findViewById(R.id.txtProbas);

        layoutResultat.setVisibility(View.GONE);
        btnAnalyser.setOnClickListener(v -> lancerAnalyse());
    }

    private void switchMode(boolean blessure) {
        modeBlessure = blessure;
        if (blessure) {
            containerSymptomes.setVisibility(View.GONE);
            containerBlessures.setVisibility(View.VISIBLE);
            btnModeBlessures.setBackgroundResource(R.drawable.bg_button_primary);
            btnModeBlessures.setTextColor(Color.WHITE);
            btnModeSymptomes.setBackgroundResource(android.R.color.transparent);
            btnModeSymptomes.setTextColor(Color.GRAY);
            btnAnalyser.setText("ANALYSER LA BLESSURE");
        } else {
            containerSymptomes.setVisibility(View.VISIBLE);
            containerBlessures.setVisibility(View.GONE);
            btnModeSymptomes.setBackgroundResource(R.drawable.bg_button_primary);
            btnModeSymptomes.setTextColor(Color.WHITE);
            btnModeBlessures.setBackgroundResource(android.R.color.transparent);
            btnModeBlessures.setTextColor(Color.GRAY);
            btnAnalyser.setText("LANCER L'ANALYSE DIAGNOSTIQUE");
        }
        layoutResultat.setVisibility(View.GONE);
    }

    private void lancerAnalyse() {
        if (modeBlessure) {
            lancerAnalyseBlessure();
        } else {
            lancerAnalyseSymptomes();
        }
    }

    private void lancerAnalyseBlessure() {
        String type = spinnerTypeBlessure.getSelectedItem().toString();
        boolean saignement = cbSaignement.isChecked();
        boolean deformation = cbDeformation.isChecked();
        boolean perteConnaissance = cbPerteConnaissance.isChecked();

        // Simulation d'analyse locale
        JSONObject r = new JSONObject();
        try {
            boolean urgent = false;
            String diagnostic = type;
            double confiance = 85.0;
            JSONArray conseils = new JSONArray();

            if (perteConnaissance || deformation || (saignement && type.contains("Coupure"))) {
                urgent = true;
                confiance = 95.0;
            }

            if (perteConnaissance) {
                diagnostic = "Traumatisme crânien / Choc sévère";
                conseils.put("Appelez immédiatement les secours (SAMU/15).");
                conseils.put("Ne déplacez pas la victime sauf danger immédiat.");
                conseils.put("Vérifiez la respiration en continu.");
            } else if (deformation) {
                diagnostic = "Fracture probable avec déplacement";
                conseils.put("Immobilisez la zone touchée.");
                conseils.put("Ne tentez pas de remettre l'os en place.");
                conseils.put("Appliquez du froid si possible (sac de glace).");
            } else if (saignement) {
                conseils.put("Appliquez une pression directe sur la plaie.");
                conseils.put("Surélevez le membre si possible.");
                conseils.put("Si le sang traverse le pansement, ajoutez-en un autre par-dessus.");
            } else {
                conseils.put("Nettoyez la zone à l'eau claire.");
                conseils.put("Appliquez un antiseptique léger.");
                conseils.put("Surveillez l'apparition de rougeur ou chaleur.");
            }

            // Vérification des vitaux
            verifierVitaux(conseils);

            r.put("diagnostic", diagnostic);
            r.put("confiance", confiance);
            r.put("urgent", urgent);
            r.put("conseils", conseils);

            afficherResultat(r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifierVitaux(JSONArray conseils) {
        String poulsStr = editPouls.getText().toString();
        String spo2Str = editSpO2.getText().toString();

        if (!poulsStr.isEmpty()) {
            int pouls = Integer.parseInt(poulsStr);
            if (pouls > 100)
                conseils.put("Note : Tachycardie détectée (" + pouls + " BPM). Repos suggéré.");
            if (pouls < 50)
                conseils.put("Note : Bradycardie détectée (" + pouls + " BPM).");
        }
        if (!spo2Str.isEmpty()) {
            int spo2 = Integer.parseInt(spo2Str);
            if (spo2 < 94)
                conseils.put("ALERTE : SpO2 faible (" + spo2 + "%). Risque d'hypoxie.");
        }
    }

    private void lancerAnalyseSymptomes() {
        // Verifier au moins 1 symptome coche
        boolean aucun = !cbFievre.isChecked() && !cbToux.isChecked() && !cbFatigue.isChecked()
                && !cbTete.isChecked() && !cbGorge.isChecked() && !cbNausees.isChecked()
                && !cbThorax.isChecked() && !cbEssoufl.isChecked() && !cbDiarrhee.isChecked()
                && !cbFrissons.isChecked() && !cbOdorat.isChecked() && !cbMusculaire.isChecked()
                && !cbPalpitations.isChecked() && !cbVertiges.isChecked();

        if (aucun) {
            Toast.makeText(this, "Selectionnez au moins un symptome.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject symptomes = new JSONObject();
        try {
            symptomes.put("fievre", cbFievre.isChecked() ? 1 : 0);
            symptomes.put("toux", cbToux.isChecked() ? 1 : 0);
            symptomes.put("fatigue", cbFatigue.isChecked() ? 1 : 0);
            symptomes.put("maux_tete", cbTete.isChecked() ? 1 : 0);
            symptomes.put("douleur_gorge", cbGorge.isChecked() ? 1 : 0);
            symptomes.put("nausees", cbNausees.isChecked() ? 1 : 0);
            symptomes.put("douleur_thorax", cbThorax.isChecked() ? 1 : 0);
            symptomes.put("essoufflement", cbEssoufl.isChecked() ? 1 : 0);
            symptomes.put("diarrhee", cbDiarrhee.isChecked() ? 1 : 0);
            symptomes.put("frissons", cbFrissons.isChecked() ? 1 : 0);
            symptomes.put("perte_odorat", cbOdorat.isChecked() ? 1 : 0);
            symptomes.put("douleurs_musculaires", cbMusculaire.isChecked() ? 1 : 0);
            symptomes.put("palpitations", cbPalpitations.isChecked() ? 1 : 0);
            symptomes.put("vertiges", cbVertiges.isChecked() ? 1 : 0);
        } catch (JSONException e) {
            Toast.makeText(this, "Erreur interne.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAnalyser.setEnabled(false);
        layoutProgress.setVisibility(View.VISIBLE);
        layoutResultat.setVisibility(View.GONE);
        progressBarHorizontal.setProgress(0);

        simulateProgress(() -> executeSanteApi(symptomes));
    }

    private void simulateProgress(Runnable onComplete) {
        new Thread(() -> {
            String[] steps = {
                    "Initialisation des modules...",
                    "Analyse des signes vitaux...",
                    "Traitement des symptômes...",
                    "Calcul des probabilités...",
                    "Génération du verdict final..."
            };
            for (int i = 0; i <= 100; i += 5) {
                final int progress = i;
                final String status = steps[Math.min(i / 20, steps.length - 1)];
                runOnUiThread(() -> {
                    progressBarHorizontal.setProgress(progress);
                    txtProgressStatus.setText(status);
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            runOnUiThread(onComplete);
        }).start();
    }

    private void executeSanteApi(JSONObject symptomes) {
        ApiClient.getInstance().analyserSante(symptomes,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject r) {
                        layoutProgress.setVisibility(View.GONE);
                        btnAnalyser.setEnabled(true);

                        // Ajouter les conseils vitaux si presents
                        try {
                            JSONArray conseils = r.optJSONArray("conseils");
                            if (conseils == null)
                                conseils = new JSONArray();
                            verifierVitaux(conseils);
                            r.put("conseils", conseils);
                        } catch (JSONException e) {
                        }

                        afficherResultat(r);
                    }

                    @Override
                    public void onError(String e) {
                        layoutProgress.setVisibility(View.GONE);
                        btnAnalyser.setEnabled(true);
                        Toast.makeText(SanteActivity.this,
                                "Erreur API : " + e, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void afficherResultat(JSONObject r) {
        String diagnostic = r.optString("diagnostic", "Inconnu");
        double confiance = r.optDouble("confiance", 0);
        boolean urgent = r.optBoolean("urgent", false);

        int couleur = urgent
                ? Color.parseColor("#ff4757")
                : Color.parseColor("#00f5c4");

        txtDiagnostic.setText("Verdict : " + diagnostic);
        txtDiagnostic.setTextColor(couleur);
        txtConfiance.setText(String.format("Indice de confiance : %.1f%%", confiance));

        if (urgent) {
            txtUrgence.setText("URGENCE MÉDICALE — Contactez les secours immédiatement");
            txtUrgence.setTextColor(Color.parseColor("#ff4757"));
            txtUrgence.setVisibility(View.VISIBLE);
        } else {
            txtUrgence.setVisibility(View.GONE);
        }

        // Top 3 probas (uniquement en mode symptomes via API)
        JSONObject probas = r.optJSONObject("toutes_probabilites");
        if (probas != null && !modeBlessure) {
            StringBuilder sb = new StringBuilder("Probabilités :\n");
            double[] vals = new double[LABELS.length];
            for (int i = 0; i < LABELS.length; i++) {
                vals[i] = probas.optDouble(LABELS[i], 0);
            }
            int shown = 0;
            while (shown < 3) {
                int maxIdx = 0;
                for (int i = 1; i < vals.length; i++) {
                    if (vals[i] > vals[maxIdx])
                        maxIdx = i;
                }
                if (vals[maxIdx] <= 0)
                    break;
                sb.append(String.format("  • %s : %.1f%%\n", LABELS[maxIdx], vals[maxIdx]));
                vals[maxIdx] = -1;
                shown++;
            }
            txtProbas.setText(sb.toString().trim());
            txtProbas.setVisibility(View.VISIBLE);
        } else {
            txtProbas.setVisibility(View.GONE);
        }

        // Conseils
        layoutConseils.removeAllViews();
        JSONArray conseilsArr = r.optJSONArray("conseils");
        if (conseilsArr != null && conseilsArr.length() > 0) {
            for (int i = 0; i < conseilsArr.length(); i++) {
                String c = conseilsArr.optString(i, "").trim();
                if (c.isEmpty())
                    continue;
                TextView tv = new TextView(this);
                tv.setText("• " + c);
                tv.setTextColor(Color.parseColor("#e8e8f0"));
                tv.setTextSize(13);
                tv.setPadding(0, 8, 0, 0);
                layoutConseils.addView(tv);
            }
        }

        if (urgent) {
            NotificationUtil.notify(
                    this,
                    "Alerte Santé Critique",
                    "Alerte: " + diagnostic + " — Confiance " + String.format("%.1f%%", confiance),
                    2001);
        }

        layoutResultat.setVisibility(View.VISIBLE);
    }
}
