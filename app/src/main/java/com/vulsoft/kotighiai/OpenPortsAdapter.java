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

public class OpenPortsAdapter extends RecyclerView.Adapter<OpenPortsAdapter.ViewHolder> {

    private List<PortScannerFragment.PortInfo> ports = new ArrayList<>();

    public void updatePorts(List<PortScannerFragment.PortInfo> newPorts) {
        ports.clear();
        ports.addAll(newPorts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_open_port, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PortScannerFragment.PortInfo port = ports.get(position);
        holder.txtPortNumber.setText("Port " + port.port);
        holder.txtServiceName.setText(port.service);
        holder.chipStatus.setText(port.status);
    }

    @Override
    public int getItemCount() {
        return ports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPortNumber, txtServiceName;
        Chip chipStatus;

        ViewHolder(View itemView) {
            super(itemView);
            txtPortNumber = itemView.findViewById(R.id.txtPortNumber);
            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
