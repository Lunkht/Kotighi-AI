package com.vulsoft.kotighiai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {
    private final List<JSONObject> items = new ArrayList<>();

    public void setData(JSONArray arr) {
        items.clear();
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.optJSONObject(i);
                if (o != null) items.add(o);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtType, txtResultat, txtDetails, txtDate;
        VH(@NonNull View itemView) {
            super(itemView);
            txtType = itemView.findViewById(R.id.txtType);
            txtResultat = itemView.findViewById(R.id.txtResultat);
            txtDetails = itemView.findViewById(R.id.txtDetails);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
        void bind(JSONObject obj) {
            String type = obj.optString("type_analyse", "?");
            String res  = obj.optString("resultat", "?");
            String det  = obj.optString("details", "");
            String date = obj.optString("created_at", "");
            txtType.setText(type);
            txtResultat.setText(res);
            txtDetails.setText(det);
            txtDate.setText(date);
        }
    }
}
