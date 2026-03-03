package com.vulsoft.kotighiai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class NetworkScanFragment extends Fragment {

    private AutoCompleteTextView spinnerScanType;
    private TextInputEditText editIpAddress;
    private MaterialButton btnAdvancedOptions, btnStartScan;
    private MaterialCardView cardScanResults;
    private RecyclerView recyclerScanResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerScanType = view.findViewById(R.id.spinnerScanType);
        editIpAddress = view.findViewById(R.id.editIpAddress);
        btnAdvancedOptions = view.findViewById(R.id.btnAdvancedOptions);
        btnStartScan = view.findViewById(R.id.btnStartScan);
        cardScanResults = view.findViewById(R.id.cardScanResults);
        recyclerScanResults = view.findViewById(R.id.recyclerScanResults);

        // Configuration du dropdown
        String[] scanTypes = {"Scan Complet", "Scan Rapide", "Scan Furtif", "Scan UDP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, scanTypes);
        spinnerScanType.setAdapter(adapter);

        recyclerScanResults.setLayoutManager(new LinearLayoutManager(getContext()));

        btnStartScan.setOnClickListener(v -> startNetworkScan());
        btnAdvancedOptions.setOnClickListener(v -> showAdvancedOptions());
    }

    private void startNetworkScan() {
        String ip = editIpAddress.getText().toString().trim();
        if (ip.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez entrer une adresse IP ou réseau", Toast.LENGTH_SHORT).show();
            return;
        }

        btnStartScan.setEnabled(false);
        Toast.makeText(getContext(), "Scan en cours...", Toast.LENGTH_SHORT).show();

        // Simulation - remplacer par appel API
        new android.os.Handler().postDelayed(() -> {
            cardScanResults.setVisibility(View.VISIBLE);
            btnStartScan.setEnabled(true);
            Toast.makeText(getContext(), "Scan terminé", Toast.LENGTH_SHORT).show();
        }, 3000);
    }

    private void showAdvancedOptions() {
        Toast.makeText(getContext(), "Options avancées (à implémenter)", Toast.LENGTH_SHORT).show();
    }
}
