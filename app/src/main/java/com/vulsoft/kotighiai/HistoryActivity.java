package com.vulsoft.kotighiai;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private ProgressBar progress;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recycler = findViewById(R.id.recyclerHistory);
        progress = new ProgressBar(this);
        adapter = new HistoryAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        chargerHistorique();
    }

    private void chargerHistorique() {
        // Afficher un léger indicateur (facultatif)
        // Ici, on peut ajouter un SwipeRefreshLayout pour améliorer l'UX.
        ApiClient.getInstance().getHistorique(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray arr = response.optJSONArray("items");
                if (arr == null) arr = new JSONArray();
                adapter.setData(arr);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(HistoryActivity.this, "Erreur: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
