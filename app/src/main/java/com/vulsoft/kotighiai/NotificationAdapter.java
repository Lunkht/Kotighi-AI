package com.vulsoft.kotighiai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private final List<JSONObject> notifications = new ArrayList<>();

    public void updateList(List<JSONObject> newNotifs) {
        notifications.clear();
        notifications.addAll(newNotifs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMessage, txtTime;
        View indicator;

        NotifViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            indicator = itemView.findViewById(R.id.indicator);
        }

        void bind(JSONObject data) {
            String type = data.optString("type_analyse", "Info");
            String res = data.optString("resultat", "");
            String details = data.optString("details", "");
            String dateStr = data.optString("created_at", "");

            // Titre et couleur
            if ("Attaque".equals(res) || details.contains("Urgent: true")) {
                txtTitle.setText("⚠️ ALERTE CRITIQUE");
                indicator.setBackgroundColor(0xFFFF4757); // Red
            } else {
                txtTitle.setText("ℹ️ Information");
                indicator.setBackgroundColor(0xFF00E5FF); // Cyan
            }

            // Message
            txtMessage.setText(type + ": " + res + "\n" + details);

            // Date
            try {
                // Format API: 2023-10-27T10:00:00
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse(dateStr);
                SimpleDateFormat out = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
                txtTime.setText(out.format(date));
            } catch (Exception e) {
                txtTime.setText(dateStr);
            }
        }
    }
}