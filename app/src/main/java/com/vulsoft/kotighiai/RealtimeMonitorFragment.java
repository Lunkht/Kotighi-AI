package com.vulsoft.kotighiai;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RealtimeMonitorFragment extends Fragment {

    private SwitchMaterial switchMonitoring;
    private TextView txtPacketsPerSec, txtBandwidth;
    private RecyclerView recyclerConnections, recyclerAlerts;
    private Chip chipAlertCount;
    private ConnectionsAdapter connectionsAdapter;
    private AlertsAdapter alertsAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;
    private boolean isMonitoring = false;
    private Random random = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_realtime_monitor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchMonitoring = view.findViewById(R.id.switchMonitoring);
        txtPacketsPerSec = view.findViewById(R.id.txtPacketsPerSec);
        txtBandwidth = view.findViewById(R.id.txtBandwidth);
        recyclerConnections = view.findViewById(R.id.recyclerConnections);
        recyclerAlerts = view.findViewById(R.id.recyclerAlerts);
        chipAlertCount = view.findViewById(R.id.chipAlertCount);

        connectionsAdapter = new ConnectionsAdapter();
        recyclerConnections.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerConnections.setAdapter(connectionsAdapter);

        alertsAdapter = new AlertsAdapter();
        recyclerAlerts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAlerts.setAdapter(alertsAdapter);

        switchMonitoring.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startMonitoring();
            } else {
                stopMonitoring();
            }
        });
    }

    private void startMonitoring() {
        isMonitoring = true;
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateMetrics();
                if (isMonitoring) {
                    handler.postDelayed(this, 2000);
                }
            }
        };
        handler.post(updateRunnable);
    }

    private void stopMonitoring() {
        isMonitoring = false;
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    private void updateMetrics() {
        // Simulation de données en temps réel
        int packets = random.nextInt(1000) + 100;
        float bandwidth = random.nextFloat() * 10;
        
        txtPacketsPerSec.setText(String.valueOf(packets));
        txtBandwidth.setText(String.format("%.1f Mbps", bandwidth));

        // Mise à jour des connexions
        List<ConnectionInfo> connections = new ArrayList<>();
        connections.add(new ConnectionInfo("192.168.1.100", "443", "HTTPS", "2.4 MB"));
        connections.add(new ConnectionInfo("192.168.1.105", "80", "HTTP", "1.2 MB"));
        connectionsAdapter.updateConnections(connections);

        // Mise à jour des alertes
        chipAlertCount.setText("2");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopMonitoring();
    }

    public static class ConnectionInfo {
        public String ip;
        public String port;
        public String protocol;
        public String data;

        public ConnectionInfo(String ip, String port, String protocol, String data) {
            this.ip = ip;
            this.port = port;
            this.protocol = protocol;
            this.data = data;
        }
    }
}
