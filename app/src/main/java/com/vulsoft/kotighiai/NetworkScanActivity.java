package com.vulsoft.kotighiai;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;
import java.util.Locale;

/**
 * Activity pour le scan automatique des réseaux de proximité
 */
public class NetworkScanActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnStartScan, btnQuickScan;
    private SwitchMaterial switchDeepScan;
    private LinearLayout layoutProgress;
    private ProgressBar progressBarHorizontal;
    private TextView txtProgressStatus;
    private MaterialCardView cardSummary;
    private TextView txtTotalNetworks, txtCritical, txtHigh, txtMedium, txtAverageScore;
    private Chip chipOverallRisk;
    private RecyclerView recyclerNetworks;
    private LinearLayout layoutResults, layoutEmpty;

    private NetworkScanService scanService;
    private boolean serviceBound = false;
    private NetworkScanAdapter adapter;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NetworkScanService.LocalBinder binder = (NetworkScanService.LocalBinder) service;
            scanService = binder.getService();
            serviceBound = true;

            scanService.setScanCallback(new NetworkScanService.ScanCallback() {
                @Override
                public void onScanStarted() {
                    runOnUiThread(() -> {
                        layoutProgress.setVisibility(View.VISIBLE);
                        progressBarHorizontal.setProgress(0);
                        txtProgressStatus.setText("Scan en cours...");
                        btnStartScan.setEnabled(false);
                        btnQuickScan.setEnabled(false);
                        layoutResults.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onScanProgress(int progress, String message) {
                    runOnUiThread(() -> {
                        progressBarHorizontal.setProgress(progress);
                        txtProgressStatus.setText(message);
                    });
                }

                @Override
                public void onScanCompleted(List<NetworkScanService.NetworkResult> networks,
                        NetworkScanService.ScanSummary summary) {
                    runOnUiThread(() -> {
                        layoutProgress.setVisibility(View.GONE);
                        btnStartScan.setEnabled(true);
                        btnQuickScan.setEnabled(true);

                        if (networks.isEmpty()) {
                            layoutEmpty.setVisibility(View.VISIBLE);
                            layoutResults.setVisibility(View.GONE);
                        } else {
                            displayResults(networks, summary);
                        }
                    });
                }

                @Override
                public void onScanError(String error) {
                    runOnUiThread(() -> {
                        layoutProgress.setVisibility(View.GONE);
                        btnStartScan.setEnabled(true);
                        btnQuickScan.setEnabled(true);
                        Toast.makeText(NetworkScanActivity.this, "Erreur: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_scan);

        initViews();
        setupRecyclerView();
        setupListeners();

        // Bind au service
        Intent intent = new Intent(this, NetworkScanService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnStartScan = findViewById(R.id.btnStartScan);
        btnQuickScan = findViewById(R.id.btnQuickScan);
        switchDeepScan = findViewById(R.id.switchDeepScan);
        layoutProgress = findViewById(R.id.layoutProgress);
        progressBarHorizontal = findViewById(R.id.progressBarHorizontal);
        txtProgressStatus = findViewById(R.id.txtProgressStatus);
        cardSummary = findViewById(R.id.cardSummary);
        txtTotalNetworks = findViewById(R.id.txtTotalNetworks);
        txtCritical = findViewById(R.id.txtCritical);
        txtHigh = findViewById(R.id.txtHigh);
        txtMedium = findViewById(R.id.txtMedium);
        txtAverageScore = findViewById(R.id.txtAverageScore);
        chipOverallRisk = findViewById(R.id.chipOverallRisk);
        recyclerNetworks = findViewById(R.id.recyclerNetworks);
        layoutResults = findViewById(R.id.layoutResults);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new NetworkScanAdapter(network -> {
            // Afficher les détails du réseau
            showNetworkDetails(network);
        });
        recyclerNetworks.setLayoutManager(new LinearLayoutManager(this));
        recyclerNetworks.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnStartScan.setOnClickListener(v -> {
            if (serviceBound && scanService != null) {
                boolean deepScan = switchDeepScan.isChecked();
                scanService.startProximityScan(deepScan);
            }
        });

        btnQuickScan.setOnClickListener(v -> {
            if (serviceBound && scanService != null) {
                scanService.startQuickScan();
            }
        });
    }

    private void displayResults(List<NetworkScanService.NetworkResult> networks,
            NetworkScanService.ScanSummary summary) {
        layoutResults.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        // Afficher le résumé
        cardSummary.setVisibility(View.VISIBLE);
        txtTotalNetworks.setText(String.valueOf(summary.totalNetworks));
        txtCritical.setText(String.valueOf(summary.criticalCount));
        txtHigh.setText(String.valueOf(summary.highCount));
        txtMedium.setText(String.valueOf(summary.mediumCount));
        txtAverageScore.setText(String.format(Locale.getDefault(), "%.1f/100", summary.averageScore));

        // Niveau de risque global
        String overallRisk = summary.getOverallRisk();
        chipOverallRisk.setText(overallRisk);

        int riskColor;
        switch (overallRisk) {
            case "CRITIQUE":
                riskColor = 0xFFD63031;
                break;
            case "ÉLEVÉ":
                riskColor = 0xFFE17055;
                break;
            case "MOYEN":
                riskColor = 0xFFFFB900;
                break;
            default:
                riskColor = 0xFFFF8C00;
                break;
        }
        chipOverallRisk.setTextColor(riskColor);
        chipOverallRisk.setChipBackgroundColorResource(android.R.color.transparent);

        // Afficher les réseaux
        adapter.updateNetworks(networks);

        Toast.makeText(this,
                summary.totalNetworks + " réseau(x) détecté(s)",
                Toast.LENGTH_SHORT).show();
    }

    private void showNetworkDetails(NetworkScanService.NetworkResult network) {
        // Créer un dialog avec les détails complets
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

        StringBuilder details = new StringBuilder();
        details.append("SSID: ").append(network.ssid).append("\n");
        details.append("BSSID: ").append(network.bssid).append("\n");
        details.append("Signal: ").append(network.signalStrength).append(" dBm\n");
        details.append("Chiffrement: ").append(network.encryption).append("\n");
        details.append("Fréquence: ").append(network.frequency).append("\n");
        details.append("Canal: ").append(network.channel).append("\n");
        details.append("\nScore de vulnérabilité: ").append(
                String.format(Locale.getDefault(), "%.1f/100", network.vulnerabilityScore)).append("\n");
        details.append("Niveau: ").append(network.vulnerabilityLevel).append("\n");

        if (network.vulnerabilities != null && !network.vulnerabilities.isEmpty()) {
            details.append("\nVulnérabilités:\n");
            for (String vuln : network.vulnerabilities) {
                details.append("• ").append(vuln).append("\n");
            }
        }

        if (network.ipRange != null) {
            details.append("\nPlage IP: ").append(network.ipRange).append("\n");
        }

        if (network.gateway != null) {
            details.append("Passerelle: ").append(network.gateway).append("\n");
        }

        if (network.devicesCount > 0) {
            details.append("Appareils: ").append(network.devicesCount).append("\n");
        }

        if (network.openPorts != null && !network.openPorts.isEmpty()) {
            details.append("\nPorts ouverts: ");
            for (int i = 0; i < network.openPorts.size(); i++) {
                details.append(network.openPorts.get(i));
                if (i < network.openPorts.size() - 1) {
                    details.append(", ");
                }
            }
            details.append("\n");
        }

        builder.setTitle("Détails du réseau")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }
}
