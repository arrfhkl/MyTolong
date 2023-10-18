package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ListView messageListView;
    private EditText messageEditText;
    private Button sendButton;
    private ArrayAdapter<String> messageListAdapter;
    private List<String> messages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageListView = findViewById(R.id.messageListView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        Intent intent = getIntent();
        if (intent != null) {
            String customerName = intent.getStringExtra("customerName");

            // Display the customer name in the activity
            TextView customerNameTextView = findViewById(R.id.customerNameTextView);
            customerNameTextView.setText(customerName);
        }

        // Initialize the message list and adapter
        messages = new ArrayList<>();
        messageListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messageListView.setAdapter(messageListAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the message text from the EditText
                String message = messageEditText.getText().toString().trim();

                // Get the current date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateAndTime = sdf.format(new Date());

                // Add the message with timestamp to the list and notify the adapter
                if (!message.isEmpty()) {
                    messages.add(currentDateAndTime + "\n" + message);
                    messageListAdapter.notifyDataSetChanged();

                    // Clear the input field
                    messageEditText.setText("");

                    // Scroll to the last message
                    messageListView.setSelection(messageListAdapter.getCount() - 1);
                }
            }
        });
    }
}


