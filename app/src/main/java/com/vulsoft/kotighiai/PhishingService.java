package com.vulsoft.kotighiai;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import org.json.JSONObject;

public class PhishingService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if (packageName == null) return;

        // Écouter les mails (Gmail, Outlook) et SMS
        if (packageName.contains("gm") || packageName.contains("email") || packageName.contains("mms") || packageName.contains("messaging")) {
            CharSequence title = sbn.getNotification().extras.getCharSequence("android.title");
            CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");

            if (text != null) {
                analyzeContent(text.toString());
            }
        }
    }

    private void analyzeContent(String content) {
        // Envoi à l'IA pour détection de phishing
        ApiClient.getInstance().sendChat("Analyse ce message pour détecter du phishing ou une arnaque : " + content, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                String reply = response.optString("response", "").toLowerCase();
                if (reply.contains("phishing") || reply.contains("arnaque") || reply.contains("dangereux") || reply.contains("suspect")) {
                    NotificationUtil.notify(
                            getApplicationContext(),
                            "Alerte Phishing Détectée",
                            "Un message suspect a été intercepté. Prudence !",
                            5001
                    );
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("PhishingService", "Erreur analyse: " + errorMessage);
            }
        });
    }
}
