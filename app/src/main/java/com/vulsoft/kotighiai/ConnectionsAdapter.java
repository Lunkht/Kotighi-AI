package com.vulsoft.kotighiai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ViewHolder> {

    private List<RealtimeMonitorFragment.ConnectionInfo> connections = new ArrayList<>();

    public void updateConnections(List<RealtimeMonitorFragment.ConnectionInfo> newConnections) {
        connections.clear();
        connections.addAll(newConnections);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_connection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RealtimeMonitorFragment.ConnectionInfo conn = connections.get(position);
        holder.txtConnectionIp.setText(conn.ip);
        holder.txtConnectionPort.setText("Port " + conn.port + " • " + conn.protocol);
        holder.txtConnectionData.setText(conn.data);
    }

    @Override
    public int getItemCount() {
        return connections.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtConnectionIp, txtConnectionPort, txtConnectionData;

        ViewHolder(View itemView) {
            super(itemView);
            txtConnectionIp = itemView.findViewById(R.id.txtConnectionIp);
            txtConnectionPort = itemView.findViewById(R.id.txtConnectionPort);
            txtConnectionData = itemView.findViewById(R.id.txtConnectionData);
        }
    }
}
