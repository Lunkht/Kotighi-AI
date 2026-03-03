package com.vulsoft.kotighiai;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity {

    private EditText inputMessage;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inputMessage = findViewById(R.id.inputMessage);
        recyclerChat = findViewById(R.id.recyclerChat);
        ImageButton btnSend = findViewById(R.id.btnSend);
        ImageButton btnBack = findViewById(R.id.btnBack);

        chatAdapter = new ChatAdapter();
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        // Message de bienvenue
        chatAdapter.addMessage(new ChatAdapter.ChatMessage("Bonjour ! Je suis l'assistant Kotighi. Posez-moi vos questions sur la cybersécurité ou la santé.", false));

        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendMessage() {
        String msg = inputMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        // Afficher message utilisateur
        chatAdapter.addMessage(new ChatAdapter.ChatMessage(msg, true));
        inputMessage.setText("");
        recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        // Appel API
        ApiClient.getInstance().sendChat(msg, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                String reply = response.optString("response", "Désolé, je n'ai pas compris.");
                chatAdapter.addMessage(new ChatAdapter.ChatMessage(reply, false));
                recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onError(String errorMessage) {
                chatAdapter.addMessage(new ChatAdapter.ChatMessage("Erreur : " + errorMessage, false));
            }
        });
    }
}