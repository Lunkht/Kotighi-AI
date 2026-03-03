package com.vulsoft.kotighiai;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de scan automatique des réseaux de proximité
 * Détecte et analyse les réseaux WiFi avec classification par vulnérabilité
 */
public class NetworkScanService extends Service {

    private final IBinder binder = new LocalBinder();
    private boolean isScanning = false;
    private ScanCallback callback;

    public class LocalBinder extends Binder {
        NetworkScanService getService() {
            return NetworkScanService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public interface ScanCallback {
        void onScanStarted();
        void onScanProgress(int progress, String message);
        void onScanCompleted(List<NetworkResult> networks, ScanSummary summary);
        void onScanError(String error);
    }

    public void setScanCallback(ScanCallback callback) {
        this.callback = callback;
    }

    /**
     * Démarre un scan des réseaux de proximité
     */
    public void startProximityScan(boolean deepScan) {
        if (isScanning) {
            if (callback != null) {
                callback.onScanError("Un scan est déjà en cours");
            }
            return;
        }

        isScanning = true;
        if (callback != null) {
            callback.onScanStarted();
        }

        // Appel API en arrière-plan
        new Thread(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("deep_scan", deepScan);
                requestBody.put("scan_type", "proximity");

                ApiClient.getInstance().scanProximityNetworks(requestBody, new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        isScanning = false;
                        try {
                            List<NetworkResult> networks = parseNetworks(response.getJSONArray("networks"));
                            ScanSummary summary = parseSummary(response.getJSONObject("summary"));

                            if (callback != null) {
                                callback.onScanCompleted(networks, summary);
                            }

                            // Notification si réseaux critiques
                            if (summary.criticalCount > 0) {
                                NotificationUtil.notify(
                                    NetworkScanService.this,
                                    "Réseaux vulnérables détectés",
                                    summary.criticalCount + " réseau(x) critique(s) à proximité",
                                    2001
                                );
                            }

                        } catch (JSONException e) {
                            if (callback != null) {
                                callback.onScanError("Erreur de parsing: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        isScanning = false;
                        if (callback != null) {
                            callback.onScanError(error);
                        }
                    }
                });

            } catch (JSONException e) {
                isScanning = false;
                if (callback != null) {
                    callback.onScanError("Erreur de requête: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Scan rapide (sans analyse approfondie)
     */
    public void startQuickScan() {
        if (isScanning) {
            if (callback != null) {
                callback.onScanError("Un scan est déjà en cours");
            }
            return;
        }

        isScanning = true;
        if (callback != null) {
            callback.onScanStarted();
        }

        ApiClient.getInstance().quickNetworkScan(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                isScanning = false;
                try {
                    JSONArray topVulnerable = response.getJSONArray("top_vulnerable");
                    List<NetworkResult> networks = parseNetworks(topVulnerable);
                    
                    JSONObject summaryObj = response.getJSONObject("summary");
                    ScanSummary summary = parseSummary(summaryObj);

                    if (callback != null) {
                        callback.onScanCompleted(networks, summary);
                    }

                } catch (JSONException e) {
                    if (callback != null) {
                        callback.onScanError("Erreur de parsing: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String error) {
                isScanning = false;
                if (callback != null) {
                    callback.onScanError(error);
                }
            }
        });
    }

    /**
     * Parse les résultats réseau
     */
    private List<NetworkResult> parseNetworks(JSONArray networksArray) throws JSONException {
        List<NetworkResult> networks = new ArrayList<>();

        for (int i = 0; i < networksArray.length(); i++) {
            JSONObject netObj = networksArray.getJSONObject(i);
            
            NetworkResult network = new NetworkResult();
            network.ssid = netObj.optString("ssid", "Unknown");
            network.bssid = netObj.optString("bssid", "00:00:00:00:00:00");
            network.signalStrength = netObj.optInt("signal_strength", -100);
            network.channel = netObj.optInt("channel", 0);
            network.encryption = netObj.optString("encryption", "UNKNOWN");
            network.frequency = netObj.optString("frequency", "Unknown");
            network.vulnerabilityScore = (float) netObj.optDouble("vulnerability_score", 0.0);
            network.ipRange = netObj.optString("ip_range", null);
            network.gateway = netObj.optString("gateway", null);
            network.devicesCount = netObj.optInt("devices_count", 0);

            // Parse vulnerabilities
            JSONArray vulnsArray = netObj.optJSONArray("vulnerabilities");
            if (vulnsArray != null) {
                network.vulnerabilities = new ArrayList<>();
                for (int j = 0; j < vulnsArray.length(); j++) {
                    network.vulnerabilities.add(vulnsArray.getString(j));
                }
            }

            // Parse open ports
            JSONArray portsArray = netObj.optJSONArray("open_ports");
            if (portsArray != null) {
                network.openPorts = new ArrayList<>();
                for (int j = 0; j < portsArray.length(); j++) {
                    network.openPorts.add(portsArray.getInt(j));
                }
            }

            // Déterminer le niveau
            network.vulnerabilityLevel = getVulnerabilityLevel(network.vulnerabilityScore);

            networks.add(network);
        }

        return networks;
    }

    /**
     * Parse le résumé du scan
     */
    private ScanSummary parseSummary(JSONObject summaryObj) throws JSONException {
        ScanSummary summary = new ScanSummary();
        summary.totalNetworks = summaryObj.optInt("total_networks", 0);
        summary.criticalCount = summaryObj.optInt("critical", 0);
        summary.highCount = summaryObj.optInt("high", 0);
        summary.mediumCount = summaryObj.optInt("medium", 0);
        summary.lowCount = summaryObj.optInt("low", 0);
        summary.minimalCount = summaryObj.optInt("minimal", 0);
        summary.averageScore = (float) summaryObj.optDouble("average_score", 0.0);
        return summary;
    }

    /**
     * Détermine le niveau de vulnérabilité
     */
    private String getVulnerabilityLevel(float score) {
        if (score >= 80) return "CRITIQUE";
        if (score >= 60) return "ÉLEVÉ";
        if (score >= 40) return "MOYEN";
        if (score >= 20) return "FAIBLE";
        return "MINIMAL";
    }

    public boolean isScanning() {
        return isScanning;
    }

    /**
     * Classe pour stocker les résultats d'un réseau
     */
    public static class NetworkResult {
        public String ssid;
        public String bssid;
        public int signalStrength;
        public int channel;
        public String encryption;
        public String frequency;
        public float vulnerabilityScore;
        public String vulnerabilityLevel;
        public String ipRange;
        public String gateway;
        public int devicesCount;
        public List<String> vulnerabilities;
        public List<Integer> openPorts;

        public int getSignalBars() {
            if (signalStrength >= -50) return 4;
            if (signalStrength >= -60) return 3;
            if (signalStrength >= -70) return 2;
            if (signalStrength >= -80) return 1;
            return 0;
        }

        public int getVulnerabilityColor() {
            switch (vulnerabilityLevel) {
                case "CRITIQUE": return 0xFFD63031; // accent_red
                case "ÉLEVÉ": return 0xFFE17055; // accent_orange
                case "MOYEN": return 0xFFFFB900; // yellow
                case "FAIBLE": return 0xFF00B894; // accent_cyan
                default: return 0xFF95A5A6; // gray
            }
        }
    }

    /**
     * Classe pour le résumé du scan
     */
    public static class ScanSummary {
        public int totalNetworks;
        public int criticalCount;
        public int highCount;
        public int mediumCount;
        public int lowCount;
        public int minimalCount;
        public float averageScore;

        public String getOverallRisk() {
            if (criticalCount > 0) return "CRITIQUE";
            if (highCount > 2) return "ÉLEVÉ";
            if (highCount > 0 || mediumCount > 3) return "MOYEN";
            return "FAIBLE";
        }
    }
}
