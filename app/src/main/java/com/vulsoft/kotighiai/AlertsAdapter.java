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

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {

    private List<AlertInfo> alerts = new ArrayList<>();

    public void updateAlerts(List<AlertInfo> newAlerts) {
        alerts.clear();
        alerts.addAll(newAlerts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertInfo alert = alerts.get(position);
        holder.txtAlertTitle.setText(alert.title);
        holder.txtAlertTime.setText(alert.time);
        holder.chipAlertLevel.setText(alert.level);
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAlertTitle, txtAlertTime;
        Chip chipAlertLevel;

        ViewHolder(View itemView) {
            super(itemView);
            txtAlertTitle = itemView.findViewById(R.id.txtAlertTitle);
            txtAlertTime = itemView.findViewById(R.id.txtAlertTime);
            chipAlertLevel = itemView.findViewById(R.id.chipAlertLevel);
        }
    }

    public static class AlertInfo {
        public String title;
        public String time;
        public String level;

        public AlertInfo(String title, String time, String level) {
            this.title = title;
            this.time = time;
            this.level = level;
        }
    }
}
