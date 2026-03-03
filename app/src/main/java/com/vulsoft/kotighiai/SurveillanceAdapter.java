package com.vulsoft.kotighiai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SurveillanceAdapter extends RecyclerView.Adapter<SurveillanceAdapter.SurvViewHolder> {

    private final List<JSONObject> targets = new ArrayList<>();
    private final OnStopListener listener;

    public interface OnStopListener {
        void onStop(String ip);
    }

    public SurveillanceAdapter(OnStopListener listener) {
        this.listener = listener;
    }

    public void updateList(List<JSONObject> newTargets) {
        targets.clear();
        targets.addAll(newTargets);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SurvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surveillance_target, parent, false);
        return new SurvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurvViewHolder holder, int position) {
        holder.bind(targets.get(position));
    }

    @Override
    public int getItemCount() {
        return targets.size();
    }

    class SurvViewHolder extends RecyclerView.ViewHolder {
        TextView txtIp, txtReq, txtStatus;
        Button btnStop;

        SurvViewHolder(View itemView) {
            super(itemView);
            txtIp = itemView.findViewById(R.id.txtSurvIp);
            txtReq = itemView.findViewById(R.id.txtSurvReq);
            txtStatus = itemView.findViewById(R.id.txtSurvStatus);
            btnStop = itemView.findViewById(R.id.btnSurvStop);
        }

        void bind(JSONObject data) {
            String ip = data.optString("ip");
            txtIp.setText(ip);
            txtReq.setText(data.optInt("req_min") + " req/m");
            txtStatus.setText(data.optString("status"));
            
            btnStop.setOnClickListener(v -> {
                if (listener != null) listener.onStop(ip);
            });
        }
    }
}