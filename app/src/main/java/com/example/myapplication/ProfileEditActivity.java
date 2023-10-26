package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button saveButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);

        // You can retrieve user data from Firestore or any other source here
        // Populate the EditText fields with the user's current data

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle saving the edited data to Firestore or any other source
                String newName = nameEditText.getText().toString();
                String newEmail = emailEditText.getText().toString();
                String newPhone = phoneEditText.getText().toString();

                // You can update the user's data here and show a success message
                // For simplicity, we'll just show a toast message
                Toast.makeText(ProfileEditActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
