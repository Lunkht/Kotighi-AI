package com.vulsoft.kotighiai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class VulnerabilitiesAdapter extends RecyclerView.Adapter<VulnerabilitiesAdapter.ViewHolder> {

    private List<VulnerabilityFragment.VulnInfo> vulnerabilities = new ArrayList<>();

    public void updateVulnerabilities(List<VulnerabilityFragment.VulnInfo> newVulns) {
        vulnerabilities.clear();
        vulnerabilities.addAll(newVulns);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vulnerability, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VulnerabilityFragment.VulnInfo vuln = vulnerabilities.get(position);
        holder.txtVulnName.setText(vuln.name);
        holder.txtVulnLocation.setText(vuln.location);
        holder.txtVulnDescription.setText(vuln.description);
        holder.chipSeverity.setText(vuln.severity);

        // Couleur selon la sévérité
        int color, bgColor;
        switch (vuln.severity) {
            case "CRITIQUE":
                color = 0xFFD63031; // accent_red
                bgColor = 0x1AD63031; // accent_red_alpha
                break;
            case "ÉLEVÉ":
                color = 0xFFE17055; // accent_orange
                bgColor = 0x1AE17055;
                break;
            default:
                color = 0xFF00B894; // accent_cyan
                bgColor = 0x1A00B894; // accent_cyan_alpha
                break;
        }
        holder.chipSeverity.setTextColor(color);
        holder.chipSeverity.setChipBackgroundColorResource(android.R.color.transparent);
    }

    @Override
    public int getItemCount() {
        return vulnerabilities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVulnName, txtVulnLocation, txtVulnDescription;
        Chip chipSeverity;

        ViewHolder(View itemView) {
            super(itemView);
            txtVulnName = itemView.findViewById(R.id.txtVulnName);
            txtVulnLocation = itemView.findViewById(R.id.txtVulnLocation);
            txtVulnDescription = itemView.findViewById(R.id.txtVulnDescription);
            chipSeverity = itemView.findViewById(R.id.chipSeverity);
        }
    }
}
