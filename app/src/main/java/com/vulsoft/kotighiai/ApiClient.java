// ============================================================
//  KOTIGHI AI — Client API Android (Java)
//  Compatible avec FastAPI (api_kotighi_fastapi.py)
//  Fichier : app/src/main/java/com/kotighi/ai/ApiClient.java
// ============================================================
//
//  DEPENDANCES build.gradle (app) :
//  implementation 'com.squareup.okhttp3:okhttp:4.12.0'
//  implementation 'com.google.code.gson:gson:2.10.1'
//
//  AndroidManifest.xml :
//  <uses-permission android:name="android.permission.INTERNET" />
//  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
// ============================================================

package com.vulsoft.kotighiai;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    // ── URL de l'API FastAPI ──────────────────────────────────
    // Definie dans build.gradle (BuildConfig.API_BASE_URL)
    // Local : http://10.0.2.2:8000
    // Prod : https://kotighi-api.onrender.com
    public static final String BASE_URL = BuildConfig.API_BASE_URL;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private String authToken = null;

    // Singleton
    private static ApiClient instance;

    public static ApiClient getInstance() {
        if (instance == null)
            instance = new ApiClient();
        return instance;
    }

    private ApiClient() {
        client = new OkHttpClient();
        executor = Executors.newCachedThreadPool();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // ── Interface callback ────────────────────────────────────
    public interface ApiCallback {
        void onSuccess(JSONObject response);

        void onError(String errorMessage);
    }

    public void setToken(String token) {
        this.authToken = token;
    }

    public boolean isLoggedIn() {
        return this.authToken != null && !this.authToken.isEmpty();
    }

    public void logout() {
        this.authToken = null;
    }

    // ── Helper : executer une requete en arriere-plan ─────────
    private void executeAsync(Request request, ApiCallback callback) {
        // Ajouter le token d'authentification si disponible
        Request.Builder builder = request.newBuilder(); // En OkHttp 4, newBuilder() clone la requete
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }
        Request finalRequest = builder.build();

        executor.execute(() -> {
            try (Response response = client.newCall(finalRequest).execute()) {
                final int code = response.code();
                String bodyStr = "{}";
                if (response.body() != null) {
                    bodyStr = response.body().string();
                }

                // Gestion des erreurs non-JSON (ex: 404 HTML, 500 HTML)
                if (bodyStr.trim().startsWith("<")) {
                    mainHandler.post(() -> callback.onError("Erreur serveur (" + code + ") : Reponse invalide"));
                    return;
                }

                final String finalBody = bodyStr;
                try {
                    String trimmed = finalBody.trim();
                    JSONObject json;
                    if (trimmed.startsWith("[")) {
                        json = new JSONObject();
                        json.put("items", new JSONArray(trimmed));
                    } else {
                        json = new JSONObject(trimmed);
                    }
                    if (response.isSuccessful()) {
                        mainHandler.post(() -> callback.onSuccess(json));
                    } else {
                        String err = json.optString("detail", "Erreur " + code);
                        mainHandler.post(() -> callback.onError(err));
                    }
                } catch (JSONException e) {
                    mainHandler.post(() -> callback.onError("Erreur format JSON (" + code + ")"));
                }
            } catch (IOException e) {
                mainHandler.post(() -> callback.onError("Erreur reseau : " + e.getMessage()));
            }
        });
    }

    // ════════════════════════════════════════════════════════
    // METHODES API (routes FastAPI)
    // ════════════════════════════════════════════════════════

    public void login(String username, String password, ApiCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);
            body.put("full_name", username); // Valeur par defaut pour API
            body.put("role", "Utilisateur"); // Valeur par defaut pour API

            Request request = new Request.Builder()
                    .url(BASE_URL + "/login")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            // On surcharge executeAsync pour intercepter le token
            executor.execute(() -> {
                try (Response response = client.newCall(request).execute()) {
                    String bodyStr = response.body() != null ? response.body().string() : "{}";
                    if (bodyStr.trim().startsWith("<")) {
                        mainHandler.post(() -> callback.onError("Erreur serveur (" + response.code() + ")"));
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(bodyStr);
                        if (response.isSuccessful()) {
                            // SAUVEGARDE DU TOKEN
                            String token = json.optString("access_token");
                            if (!token.isEmpty()) {
                                setToken(token);
                            }
                            mainHandler.post(() -> callback.onSuccess(json));
                        } else {
                            String err = json.optString("detail", "Erreur " + response.code());
                            mainHandler.post(() -> callback.onError(err));
                        }
                    } catch (JSONException e) {
                        mainHandler.post(() -> callback.onError("Erreur format JSON"));
                    }
                } catch (IOException e) {
                    mainHandler.post(() -> callback.onError("Erreur reseau : " + e.getMessage()));
                }
            });
        } catch (JSONException e) {
            callback.onError("Erreur construction requete : " + e.getMessage());
        }
    }

    public void signup(String username, String password, String email, String fullName,
            String address, String phone, String country, String organisation,
            ApiCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);
            body.put("email", email);
            body.put("full_name", fullName);
            body.put("address", address);
            body.put("phone", phone);
            body.put("country", country);
            body.put("organisation", organisation);
            body.put("role", "user");

            Request request = new Request.Builder()
                    .url(BASE_URL + "/signup")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            executor.execute(() -> {
                try (Response response = client.newCall(request).execute()) {
                    String bodyStr = response.body() != null ? response.body().string() : "{}";
                    try {
                        JSONObject json = new JSONObject(bodyStr);
                        if (response.isSuccessful()) {
                            String token = json.optString("access_token");
                            if (!token.isEmpty())
                                setToken(token);
                            mainHandler.post(() -> callback.onSuccess(json));
                        } else {
                            String err = json.optString("detail", "Erreur " + response.code());
                            mainHandler.post(() -> callback.onError(err));
                        }
                    } catch (JSONException e) {
                        mainHandler.post(() -> callback.onError("Erreur format JSON"));
                    }
                } catch (IOException e) {
                    mainHandler.post(() -> callback.onError("Erreur reseau : " + e.getMessage()));
                }
            });
        } catch (JSONException e) {
            callback.onError("Erreur construction requete : " + e.getMessage());
        }
    }

    /**
     * GET /historique
     * Recuperer l'historique des analyses.
     */
    public void getHistorique(ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/historique")
                .get()
                .build();
        executeAsync(request, callback);
    }

    /**
     * POST /predict/sante
     * Analyser des symptomes medicaux.
     *
     * Correspond au schema FastAPI SanteInput (14 symptomes) :
     * fievre, toux, fatigue, maux_tete, douleur_gorge,
     * nausees, douleur_thorax, essoufflement, diarrhee,
     * frissons, perte_odorat, douleurs_musculaires,
     * palpitations, vertiges
     * (tous 0 ou 1)
     *
     * Reponse :
     * prediction_id : int
     * diagnostic : String
     * urgent : boolean
     * confiance : double
     * toutes_probabilites : JSONObject { "Grippe": 45.2, ... }
     *
     * Exemple d'utilisation :
     * JSONObject symp = new JSONObject();
     * symp.put("fievre", 1);
     * symp.put("toux", 1);
     * symp.put("fatigue", 1);
     * // ... mettre 0 pour les autres
     *
     * ApiClient.getInstance().analyserSante(symp,
     * new ApiClient.ApiCallback() {
     * public void onSuccess(JSONObject r) {
     * String diag = r.optString("diagnostic");
     * boolean urgent = r.optBoolean("urgent");
     * double conf = r.optDouble("confiance");
     * }
     * public void onError(String e) { ... }
     * }
     * );
     */
    public void analyserSante(JSONObject symptomes, ApiCallback callback) {
        try {
            // S'assurer que les 14 champs sont presents (0 par defaut)
            String[] TOUS_SYMPTOMES = {
                    "fievre", "toux", "fatigue", "maux_tete", "douleur_gorge",
                    "nausees", "douleur_thorax", "essoufflement", "diarrhee",
                    "frissons", "perte_odorat", "douleurs_musculaires",
                    "palpitations", "vertiges"
            };
            JSONObject body = new JSONObject();
            for (String sym : TOUS_SYMPTOMES) {
                body.put(sym, symptomes.optInt(sym, 0));
            }

            Request request = new Request.Builder()
                    .url(BASE_URL + "/predict/sante")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();
            executeAsync(request, callback);
        } catch (JSONException e) {
            callback.onError("Erreur construction requete : " + e.getMessage());
        }
    }

    // ── METHODES CYBER ────────────────────────────────────────

    public void analyserCyber(
            int requetesMin, int duree, int octets,
            int portsScanes, double tauxErreur, int flagSuspect,
            ApiCallback callback) {
        analyserCyber("192.168.1.100", requetesMin, duree, octets, portsScanes, tauxErreur, flagSuspect, callback);
    }

    public void analyserCyber(
            String ip, int requetesMin, int duree, int octets,
            int portsScanes, double tauxErreur, int flagSuspect,
            ApiCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("ip_source", ip);
            body.put("requetes_min", requetesMin);
            body.put("duree", duree);
            body.put("octets", octets);
            body.put("ports_scanes", portsScanes);
            body.put("taux_erreur", tauxErreur);
            body.put("flag_suspect", flagSuspect);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/predict/cyber")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();
            executeAsync(request, callback);
        } catch (JSONException e) {
            callback.onError("Erreur construction requete : " + e.getMessage());
        }
    }

    public void analyserCyberBatch(JSONArray inputs, ApiCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("inputs", inputs);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/predict/cyber/batch")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();
            executeAsync(request, callback);
        } catch (JSONException e) {
            callback.onError("Erreur construction requete : " + e.getMessage());
        }
    }

    public void startSurveillance(String ipTarget, int durationMinutes, ApiCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("ip_target", ipTarget);
            body.put("duration_minutes", durationMinutes);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/surveillance")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();
            executeAsync(request, callback);
        } catch (JSONException e) {
            callback.onError("Erreur construction requete : " + e.getMessage());
        }
    }

    public void getSurveillanceData(ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/surveillance")
                .get()
                .build();
        executeAsync(request, callback);
    }

    public void stopSurveillance(String ip, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/surveillance/" + ip)
                .delete()
                .build();
        executeAsync(request, callback);
    }

    // ── CHATBOT ───────────────────────────────────────────────

    public void sendChat(String message, ApiCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("message", message);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/chat")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();
            executeAsync(request, callback);
        } catch (JSONException e) {
            callback.onError("Erreur construction requete : " + e.getMessage());
        }
    }

    // ── SCAN RÉSEAU DE PROXIMITÉ ──────────────────────────────

    /**
     * Scanne les réseaux WiFi de proximité avec analyse de vulnérabilités
     */
    public void scanProximityNetworks(JSONObject requestBody, ApiCallback callback) {
        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/network/scan-proximity")
                .post(body)
                .build();
        executeAsync(request, callback);
    }

    /**
     * Scan rapide des réseaux (sans analyse approfondie)
     */
    public void quickNetworkScan(ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/network/quick-scan")
                .post(RequestBody.create("", JSON))
                .build();
        executeAsync(request, callback);
    }

    /**
     * Récupère l'historique des scans réseau
     */
    public void getNetworkScanHistory(int limit, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/network/scan-history?limit=" + limit)
                .get()
                .build();
        executeAsync(request, callback);
    }

    /**
     * Récupère les statistiques de vulnérabilités
     */
    public void getVulnerabilityStats(ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/network/vulnerability-stats")
                .get()
                .build();
        executeAsync(request, callback);
    }
}
