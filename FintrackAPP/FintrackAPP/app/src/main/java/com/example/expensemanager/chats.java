package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chats extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView welcomeTextView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private FloatingActionButton fbbtnhelp;

    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Optimized OkHttpClient with custom DNS to resolve potential network issues in some environments
    private final OkHttpClient client = new OkHttpClient.Builder()
            .dns(hostname -> {
                try {
                    return Arrays.asList(InetAddress.getAllByName(hostname));
                } catch (Exception e) {
                    return new ArrayList<>();
                }
            })
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    // OpenRouter API Key
    private static final String OPENROUTER_API_KEY = "Your api key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        fbbtnhelp = findViewById(R.id.fb_helpline);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener(v -> {
            String question = messageEditText.getText().toString().trim();

            if (!question.isEmpty()) {
                addToChat(question, Message.SENT_BY_ME);
                messageEditText.setText("");
                welcomeTextView.setVisibility(View.GONE);

                callOpenRouterAPI(question);
            }
        });

        fbbtnhelp.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), HelpLine.class))
        );
    }

    private void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    private void addResponse(String response) {
        runOnUiThread(() -> {
            if (!messageList.isEmpty() &&
                    messageList.get(messageList.size() - 1).getMessage().equals("Thinking...")) {
                messageList.remove(messageList.size() - 1);
            }
            addToChat(response, Message.SENT_BY_BOT);
        });
    }

    private void callOpenRouterAPI(String question) {
        addToChat("Thinking...", Message.SENT_BY_BOT);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "openrouter/auto");
            JSONArray messages = new JSONArray();
            JSONObject msg = new JSONObject();
            msg.put("role", "user");
            msg.put("content", "You are a professional financial advisor. Answer clearly and concisely:\n" + question);
            messages.put(msg);
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            addResponse("Error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + OPENROUTER_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Request failed. Please check your internet connection.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String result = jsonObject.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (Exception e) {
                        Log.e("AI_CHATS", "Error parsing response", e);
                        addResponse("Sorry, I encountered an error while processing the response.");
                    }
                } else {
                    addResponse("AI service is currently unavailable.");
                }
            }
        });
    }
}
