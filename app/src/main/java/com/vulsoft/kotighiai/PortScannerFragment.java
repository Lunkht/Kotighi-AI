package com.vulsoft.kotighiai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PortScannerFragment extends Fragment {

    private TextInputEditText editTargetIp, editPortStart, editPortEnd;
    private MaterialButton btnScanPorts;
    private Chip chipCommonPorts, chipWebPorts, chipDatabasePorts, chipOpenPorts;
    private LinearLayout layoutProgress;
    private TextView txtScanProgress, txtPortsScanned;
    private ProgressBar progressPortScan;
    private MaterialCardView cardPortResults;
    private RecyclerView recyclerOpenPorts;
    private OpenPortsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_port_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTargetIp = view.findViewById(R.id.editTargetIp);
        editPortStart = view.findViewById(R.id.editPortStart);
        editPortEnd = view.findViewById(R.id.editPortEnd);
        btnScanPorts = view.findViewById(R.id.btnScanPorts);
        chipCommonPorts = view.findViewById(R.id.chipCommonPorts);
        chipWebPorts = view.findViewById(R.id.chipWebPorts);
        chipDatabasePorts = view.findViewById(R.id.chipDatabasePorts);
        chipOpenPorts = view.findViewById(R.id.chipOpenPorts);
        layoutProgress = view.findViewById(R.id.layoutProgress);
        txtScanProgress = view.findViewById(R.id.txtScanProgress);
        txtPortsScanned = view.findViewById(R.id.txtPortsScanned);
        progressPortScan = view.findViewById(R.id.progressPortScan);
        cardPortResults = view.findViewById(R.id.cardPortResults);
        recyclerOpenPorts = view.findViewById(R.id.recyclerOpenPorts);

        adapter = new OpenPortsAdapter();
        recyclerOpenPorts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerOpenPorts.setAdapter(adapter);

        btnScanPorts.setOnClickListener(v -> startPortScan());
        
        chipCommonPorts.setOnClickListener(v -> {
            editPortStart.setText("1");
            editPortEnd.setText("1024");
        });
        
        chipWebPorts.setOnClickListener(v -> {
            editPortStart.setText("80");
            editPortEnd.setText("443");
        });
        
        chipDatabasePorts.setOnClickListener(v -> {
            editPortStart.setText("3306");
            editPortEnd.setText("5432");
        });
    }

    private void startPortScan() {
        String ip = editTargetIp.getText().toString().trim();
        if (ip.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez entrer une adresse IP", Toast.LENGTH_SHORT).show();
            return;
        }

        layoutProgress.setVisibility(View.VISIBLE);
        btnScanPorts.setEnabled(false);
        cardPortResults.setVisibility(View.GONE);

        // Simulation de scan (remplacer par appel API réel)
        simulatePortScan(ip);
    }

    private void simulatePortScan(String ip) {
        // Simulation - remplacer par ApiClient.getInstance().scanPorts(...)
        new android.os.Handler().postDelayed(() -> {
            List<PortInfo> openPorts = new ArrayList<>();
            openPorts.add(new PortInfo(80, "HTTP", "OUVERT"));
            openPorts.add(new PortInfo(443, "HTTPS", "OUVERT"));
            openPorts.add(new PortInfo(22, "SSH", "OUVERT"));
            
            adapter.updatePorts(openPorts);
            chipOpenPorts.setText(openPorts.size() + " trouvés");
            
            layoutProgress.setVisibility(View.GONE);
            cardPortResults.setVisibility(View.VISIBLE);
            btnScanPorts.setEnabled(true);
            
            Toast.makeText(getContext(), "Scan terminé", Toast.LENGTH_SHORT).show();
        }, 2000);
    }

    public static class PortInfo {
        public int port;
        public String service;
        public String status;

        public PortInfo(int port, String service, String status) {
            this.port = port;
            this.service = service;
            this.status = status;
        }
    }
}
