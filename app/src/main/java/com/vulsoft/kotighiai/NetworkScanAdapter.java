package com.vulsoft.kotighiai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter pour afficher les résultats de scan réseau
 */
public class NetworkScanAdapter extends RecyclerView.Adapter<NetworkScanAdapter.ViewHolder> {

    private List<NetworkScanService.NetworkResult> networks = new ArrayList<>();
    private OnNetworkClickListener listener;

    public interface OnNetworkClickListener {
        void onNetworkClick(NetworkScanService.NetworkResult network);
    }

    public NetworkScanAdapter(OnNetworkClickListener listener) {
        this.listener = listener;
    }

    public void updateNetworks(List<NetworkScanService.NetworkResult> newNetworks) {
        networks.clear();
        networks.addAll(newNetworks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_network_scan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NetworkScanService.NetworkResult network = networks.get(position);

        // SSID et BSSID
        holder.txtSsid.setText(network.ssid);
        holder.txtBssid.setText(network.bssid);

        // Signal
        holder.txtSignal.setText(network.signalStrength + " dBm");
        int signalBars = network.getSignalBars();
        holder.txtSignalBars.setText(getSignalBarsText(signalBars));

        // Chiffrement
        holder.txtEncryption.setText(network.encryption);

        // Score de vulnérabilité
        holder.txtVulnScore.setText(String.format(Locale.getDefault(), "%.1f/100", network.vulnerabilityScore));
        holder.chipVulnLevel.setText(network.vulnerabilityLevel);
        holder.chipVulnLevel.setChipBackgroundColorResource(android.R.color.transparent);
        holder.chipVulnLevel.setTextColor(network.getVulnerabilityColor());

        // Fréquence et canal
        holder.txtFrequency.setText(network.frequency + " • Canal " + network.channel);

        // Vulnérabilités
        if (network.vulnerabilities != null && !network.vulnerabilities.isEmpty()) {
            holder.layoutVulnerabilities.setVisibility(View.VISIBLE);
            holder.txtVulnerabilities.setText(formatVulnerabilities(network.vulnerabilities));
        } else {
            holder.layoutVulnerabilities.setVisibility(View.GONE);
        }

        // Informations supplémentaires (si scan approfondi)
        if (network.ipRange != null || network.devicesCount > 0) {
            holder.layoutExtraInfo.setVisibility(View.VISIBLE);
            
            if (network.ipRange != null) {
                holder.txtIpRange.setText("Plage IP: " + network.ipRange);
                holder.txtIpRange.setVisibility(View.VISIBLE);
            } else {
                holder.txtIpRange.setVisibility(View.GONE);
            }

            if (network.devicesCount > 0) {
                holder.txtDevices.setText(network.devicesCount + " appareil(s) détecté(s)");
                holder.txtDevices.setVisibility(View.VISIBLE);
            } else {
                holder.txtDevices.setVisibility(View.GONE);
            }

            if (network.openPorts != null && !network.openPorts.isEmpty()) {
                holder.txtOpenPorts.setText(network.openPorts.size() + " port(s) ouvert(s)");
                holder.txtOpenPorts.setVisibility(View.VISIBLE);
            } else {
                holder.txtOpenPorts.setVisibility(View.GONE);
            }
        } else {
            holder.layoutExtraInfo.setVisibility(View.GONE);
        }

        // Couleur de la carte selon le niveau
        int strokeColor = network.getVulnerabilityColor();
        holder.card.setStrokeColor(strokeColor);
        holder.card.setStrokeWidth(3);

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNetworkClick(network);
            }
        });
    }

    @Override
    public int getItemCount() {
        return networks.size();
    }

    private String getSignalBarsText(int bars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i < bars) {
                sb.append("▂");
            } else {
                sb.append("▁");
            }
        }
        return sb.toString();
    }

    private String formatVulnerabilities(List<String> vulnerabilities) {
        StringBuilder sb = new StringBuilder();
        int count = Math.min(vulnerabilities.size(), 3); // Max 3 vulnérabilités affichées
        for (int i = 0; i < count; i++) {
            sb.append("• ").append(vulnerabilities.get(i));
            if (i < count - 1) {
                sb.append("\n");
            }
        }
        if (vulnerabilities.size() > 3) {
            sb.append("\n• ... et ").append(vulnerabilities.size() - 3).append(" autre(s)");
        }
        return sb.toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView txtSsid, txtBssid, txtSignal, txtSignalBars;
        TextView txtEncryption, txtFrequency, txtVulnScore;
        Chip chipVulnLevel;
        LinearLayout layoutVulnerabilities, layoutExtraInfo;
        TextView txtVulnerabilities;
        TextView txtIpRange, txtDevices, txtOpenPorts;

        ViewHolder(View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            txtSsid = itemView.findViewById(R.id.txtSsid);
            txtBssid = itemView.findViewById(R.id.txtBssid);
            txtSignal = itemView.findViewById(R.id.txtSignal);
            txtSignalBars = itemView.findViewById(R.id.txtSignalBars);
            txtEncryption = itemView.findViewById(R.id.txtEncryption);
            txtFrequency = itemView.findViewById(R.id.txtFrequency);
            txtVulnScore = itemView.findViewById(R.id.txtVulnScore);
            chipVulnLevel = itemView.findViewById(R.id.chipVulnLevel);
            layoutVulnerabilities = itemView.findViewById(R.id.layoutVulnerabilities);
            txtVulnerabilities = itemView.findViewById(R.id.txtVulnerabilities);
            layoutExtraInfo = itemView.findViewById(R.id.layoutExtraInfo);
            txtIpRange = itemView.findViewById(R.id.txtIpRange);
            txtDevices = itemView.findViewById(R.id.txtDevices);
            txtOpenPorts = itemView.findViewById(R.id.txtOpenPorts);
        }
    }
}
