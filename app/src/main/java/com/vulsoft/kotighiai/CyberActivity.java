package com.vulsoft.kotighiai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CyberActivity extends AppCompatActivity {

    private EditText editRequetes, editPorts, editErreur, editIpInput;
    private Button btnLancerScan, btnAjouterSurveillance, btnAjouterIp, btnRetirerIp;
    private Button tabScan, tabSurveillance;
    private ImageButton btnBack, btnSwitchToModern;
    private TextView txtResultat;
    private RecyclerView recyclerIps;
    private IpTargetAdapter adapter;
    private LinearLayout layoutProgress;
    private ProgressBar progressBarHorizontal;
    private TextView txtProgressStatus;

    // Surveillance UI
    private LinearLayout containerScan, containerSurveillance;
    private TextView txtSurvCount, txtSurvAlerts, txtSurvTraffic, txtEventsLog;
    private RecyclerView recyclerSurveillance;
    private ProgressBar progressCpu, progressRam;
    private SurveillanceAdapter survAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pollingRunnable;
    private boolean isSurveillanceActive = false;

    // Simulation de données
    private int ipCounter = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyber);

        btnBack = findViewById(R.id.btnBack);
        btnSwitchToModern = findViewById(R.id.btnSwitchToModern);
        editIpInput = findViewById(R.id.editIpInput);
        editRequetes = findViewById(R.id.editRequetes);
        editPorts = findViewById(R.id.editPorts);
        editErreur = findViewById(R.id.editErreur);
        btnLancerScan = findViewById(R.id.btnLancerScan);
        btnAjouterSurveillance = findViewById(R.id.btnAjouterSurveillance);
        btnAjouterIp = findViewById(R.id.btnAjouterIp);
        btnRetirerIp = findViewById(R.id.btnRetirerIp);
        txtResultat = findViewById(R.id.txtResultat);
        recyclerIps = findViewById(R.id.recyclerIps);
        layoutProgress = findViewById(R.id.layoutProgress);
        progressBarHorizontal = findViewById(R.id.progressBarHorizontal);
        txtProgressStatus = findViewById(R.id.txtProgressStatus);

        btnBack.setOnClickListener(v -> finish());

        // Bouton pour basculer vers l'interface moderne
        btnSwitchToModern.setOnClickListener(v -> {
            Intent intent = new Intent(this, CyberActivityModern.class);
            startActivity(intent);
            finish();
        });

        // Tabs & Containers
        tabScan = findViewById(R.id.tabScan);
        tabSurveillance = findViewById(R.id.tabSurveillance);
        containerScan = findViewById(R.id.containerScan);
        containerSurveillance = findViewById(R.id.containerSurveillance);

        // Surveillance Widgets
        txtSurvCount = findViewById(R.id.txtSurvCount);
        txtSurvAlerts = findViewById(R.id.txtSurvAlerts);
        txtSurvTraffic = findViewById(R.id.txtSurvTraffic);
        txtEventsLog = findViewById(R.id.txtEventsLog);
        recyclerSurveillance = findViewById(R.id.recyclerSurveillance);
        progressCpu = findViewById(R.id.progressCpu);
        progressRam = findViewById(R.id.progressRam);

        // Configuration RecyclerView Scan
        adapter = new IpTargetAdapter(ip -> Toast.makeText(this, "IP: " + ip, Toast.LENGTH_SHORT).show());
        recyclerIps.setLayoutManager(new LinearLayoutManager(this));
        recyclerIps.setAdapter(adapter);

        // Configuration RecyclerView Surveillance
        survAdapter = new SurveillanceAdapter(this::stopSurveillanceTarget);
        recyclerSurveillance.setLayoutManager(new LinearLayoutManager(this));
        recyclerSurveillance.setAdapter(survAdapter);

        // Donnée initiale
        adapter.addIp("192.168.1.45");

        // Listeners
        btnAjouterIp.setOnClickListener(v -> {
            String input = editIpInput.getText().toString().trim();
            if (!input.isEmpty()) {
                adapter.addIp(input);
                editIpInput.setText("");
            } else {
                // Fallback si vide: simulation
                String newIp = "192.168.1." + (ipCounter++);
                adapter.addIp(newIp);
                Toast.makeText(this, "IP auto-générée (champ vide)", Toast.LENGTH_SHORT).show();
            }
        });

        btnRetirerIp.setOnClickListener(v -> {
            List<String> ips = adapter.getIps();
            if (!ips.isEmpty()) {
                adapter.removeIp(ips.get(ips.size() - 1));
            }
        });

        btnLancerScan.setOnClickListener(v -> lancerScanBatch());
        btnAjouterSurveillance.setOnClickListener(v -> lancerSurveillance());

        // Gestion des onglets
        tabScan.setOnClickListener(v -> switchTab(true));
        tabSurveillance.setOnClickListener(v -> switchTab(false));
    }

    private void switchTab(boolean showScan) {
        if (showScan) {
            containerScan.setVisibility(View.VISIBLE);
            containerSurveillance.setVisibility(View.GONE);

            // UI Update pour les onglets
            tabScan.setTextColor(0xFFFFFFFF);
            tabScan.setBackgroundResource(R.drawable.bg_button_primary);

            tabSurveillance.setTextColor(0xFF7F8C8D); // text_secondary
            tabSurveillance.setBackgroundResource(0);

            stopPolling();
        } else {
            containerScan.setVisibility(View.GONE);
            containerSurveillance.setVisibility(View.VISIBLE);

            tabSurveillance.setTextColor(0xFFFFFFFF);
            tabSurveillance.setBackgroundResource(R.drawable.bg_button_primary);

            tabScan.setTextColor(0xFF7F8C8D);
            tabScan.setBackgroundResource(0);

            startPolling();
        }
    }

    private void startPolling() {
        if (isSurveillanceActive)
            return;
        isSurveillanceActive = true;
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                fetchSurveillanceData();
                if (isSurveillanceActive) {
                    handler.postDelayed(this, 3000); // Refresh toutes les 3 sec
                }
            }
        };
        handler.post(pollingRunnable);
    }

    private void stopPolling() {
        isSurveillanceActive = false;
        if (pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
        }
    }

    private void fetchSurveillanceData() {
        ApiClient.getInstance().getSurveillanceData(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isSurveillanceActive)
                    return;

                // Update Metrics
                JSONObject metrics = response.optJSONObject("metrics");
                if (metrics != null) {
                    txtSurvCount.setText(String.valueOf(metrics.optInt("targets_count")));
                    txtSurvAlerts.setText(String.valueOf(metrics.optInt("alerts_count")));
                    txtSurvTraffic.setText(metrics.optString("traffic_total"));
                }

                // Update List
                JSONArray targets = response.optJSONArray("targets");
                if (targets != null) {
                    List<JSONObject> list = new ArrayList<>();
                    for (int i = 0; i < targets.length(); i++)
                        list.add(targets.optJSONObject(i));
                    survAdapter.updateList(list);
                }

                // Update Events
                JSONArray events = response.optJSONArray("events");
                if (events != null && events.length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < Math.min(events.length(), 5); i++) {
                        sb.append(events.optString(i)).append("\n\n");
                    }
                    txtEventsLog.setText(sb.toString().trim());
                }

                // Update System
                JSONObject sys = response.optJSONObject("system");
                if (sys != null) {
                    progressCpu.setProgress(sys.optInt("cpu"));
                    progressRam.setProgress(sys.optInt("ram"));
                }
            }

            @Override
            public void onError(String error) {
                // Silent fail on polling error
            }
        });
    }

    private void stopSurveillanceTarget(String ip) {
        ApiClient.getInstance().stopSurveillance(ip, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(CyberActivity.this, "Arrêt: " + ip, Toast.LENGTH_SHORT).show();
                fetchSurveillanceData(); // Force refresh
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CyberActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPolling();
    }

    private void lancerScanBatch() {
        List<String> ips = adapter.getIps();
        if (ips.isEmpty()) {
            Toast.makeText(this, "Aucune cible définie", Toast.LENGTH_SHORT).show();
            return;
        }

        txtResultat.setText("");
        btnLancerScan.setEnabled(false);

        simulateProgress(() -> executeScanBatch(ips));
    }

    private void simulateProgress(Runnable onComplete) {
        layoutProgress.setVisibility(View.VISIBLE);
        progressBarHorizontal.setProgress(0);

        new Thread(() -> {
            String[] steps = {
                    "Initialisation des vecteurs d'attaque...",
                    "Injection des paquets test...",
                    "Analyse des réponses réseaux...",
                    "Calcul des probabilités de menace...",
                    "Finalisation du rapport..."
            };

            for (int i = 0; i < steps.length; i++) {
                final int progress = (i + 1) * (100 / steps.length);
                final String status = steps[i];

                runOnUiThread(() -> {
                    progressBarHorizontal.setProgress(progress);
                    txtProgressStatus.setText(status);
                });

                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                layoutProgress.setVisibility(View.GONE);
                onComplete.run();
            });
        }).start();
    }

    private void executeScanBatch(List<String> ips) {
        // Récupérer paramètres
        int req = parseIntOr(editRequetes.getText().toString(), 150);
        int ports = parseIntOr(editPorts.getText().toString(), 5);
        double erreur = parseDoubleOr(editErreur.getText().toString(), 0.01);

        // Construire le payload batch
        JSONArray batch = new JSONArray();
        for (String ip : ips) {
            try {
                JSONObject input = new JSONObject();
                input.put("ip_source", ip);
                input.put("requetes_min", req);
                input.put("duree", 30); // Fixe pour démo
                input.put("octets", 1500); // Fixe pour démo
                input.put("ports_scanes", ports);
                input.put("taux_erreur", erreur);
                input.put("flag_suspect", 0);
                batch.put(input);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Appel API Batch
        ApiClient.getInstance().analyserCyberBatch(batch, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                btnLancerScan.setEnabled(true);
                JSONArray results = response.optJSONArray("batch_results");
                if (results != null) {
                    int attaques = 0;
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject res = results.optJSONObject(i);
                        if ("Attaque".equals(res.optString("verdict"))) {
                            attaques++;
                        }
                    }
                    String msg = "Scan terminé : " + attaques + " attaque(s) détectée(s) sur " + results.length()
                            + " cibles.";
                    txtResultat.setText(msg);
                    if (attaques > 0) {
                        NotificationUtil.notify(
                                CyberActivity.this,
                                "Alerte Cybersécurité",
                                attaques + " menaces détectées lors du scan batch",
                                1002);
                    }
                } else {
                    txtResultat.setText("Aucun résultat retourné.");
                }
            }

            @Override
            public void onError(String error) {
                btnLancerScan.setEnabled(true);
                txtResultat.setText("Erreur : " + error);
            }
        });
    }

    private void lancerSurveillance() {
        List<String> ips = adapter.getIps();
        if (ips.isEmpty()) {
            Toast.makeText(this, "Aucune cible pour la surveillance", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pour la démo, on lance la surveillance sur la première IP
        String target = ips.get(0);
        btnAjouterSurveillance.setEnabled(false);
        txtResultat.setText("Activation surveillance...");

        ApiClient.getInstance().startSurveillance(target, 60, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                btnAjouterSurveillance.setEnabled(true);
                String msg = response.optString("message", "Surveillance active");
                txtResultat.setText(msg);
                Toast.makeText(CyberActivity.this, "Surveillance démarrée sur " + target, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                btnAjouterSurveillance.setEnabled(true);
                txtResultat.setText("Erreur surveillance : " + error);
            }
        });
    }

    private int parseIntOr(String s, int d) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return d;
        }
    }

    private double parseDoubleOr(String s, double d) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return d;
        }
    }
}
