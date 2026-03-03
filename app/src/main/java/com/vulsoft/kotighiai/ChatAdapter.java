package com.vulsoft.kotighiai;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        LinearLayout container;

        ChatViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            container = (LinearLayout) itemView;
        }

        void bind(ChatMessage message) {
            textMessage.setText(message.content);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textMessage.getLayoutParams();
            
            if (message.isUser) {
                container.setGravity(Gravity.END);
                textMessage.setBackgroundResource(R.drawable.bg_card); // Ou un drawable spécifique user
                textMessage.setTextColor(0xFF00F5C4); // Couleur primaire
            } else {
                container.setGravity(Gravity.START);
                textMessage.setBackgroundResource(R.drawable.bg_input); // Ou un drawable spécifique bot
                textMessage.setTextColor(0xFFFFFFFF);
            }
        }
    }

    public static class ChatMessage {
        String content;
        boolean isUser;

        public ChatMessage(String content, boolean isUser) {
            this.content = content;
            this.isUser = isUser;
        }
    }
}