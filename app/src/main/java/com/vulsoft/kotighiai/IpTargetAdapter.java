package com.vulsoft.kotighiai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class IpTargetAdapter extends RecyclerView.Adapter<IpTargetAdapter.IpViewHolder> {

    private final List<String> ips = new ArrayList<>();
    private final OnIpClickListener listener;

    public interface OnIpClickListener {
        void onIpClick(String ip);
    }

    public IpTargetAdapter(OnIpClickListener listener) {
        this.listener = listener;
    }

    public void addIp(String ip) {
        if (!ips.contains(ip)) {
            ips.add(ip);
            notifyItemInserted(ips.size() - 1);
        }
    }

    public void removeIp(String ip) {
        int index = ips.indexOf(ip);
        if (index != -1) {
            ips.remove(index);
            notifyItemRemoved(index);
        }
    }

    public List<String> getIps() {
        return ips;
    }

    @NonNull
    @Override
    public IpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ip_target, parent, false);
        return new IpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IpViewHolder holder, int position) {
        holder.bind(ips.get(position));
    }

    @Override
    public int getItemCount() {
        return ips.size();
    }

    class IpViewHolder extends RecyclerView.ViewHolder {
        TextView txtIp;

        IpViewHolder(View itemView) {
            super(itemView);
            txtIp = itemView.findViewById(R.id.txtIp);
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onIpClick(ips.get(getAdapterPosition()));
                }
            });
        }

        void bind(String ip) {
            txtIp.setText(ip);
        }
    }
}