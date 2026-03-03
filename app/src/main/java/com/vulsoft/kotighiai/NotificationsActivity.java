package com.vulsoft.kotighiai;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerNotifications;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerNotifications = findViewById(R.id.recyclerNotifications);
        adapter = new NotificationAdapter();
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        // On utilise l'historique et on filtre pour simuler les notifications
        ApiClient.getInstance().getHistorique(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray items = response.optJSONArray("items");
                if (items != null) {
                    List<JSONObject> notifs = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.optJSONObject(i);
                        // Filtre: On garde tout pour l'instant, ou on peut filtrer sur "Attaque" / "Urgent"
                        // Pour la démo, on montre tout comme "Notification"
                        notifs.add(item);
                    }
                    adapter.updateList(notifs);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(NotificationsActivity.this, "Erreur chargement: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
