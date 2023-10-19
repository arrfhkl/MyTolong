package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class RegistrationActivity extends AppCompatActivity {

    private EditText riderNameEditText;
    private EditText riderEmailEditText;
    private EditText riderPhoneEditText;
    private EditText riderPasswordEditText;
    private EditText riderConfirmPasswordEditText;
    private Button riderRegisterButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        riderNameEditText = findViewById(R.id.riderNameEditText);
        riderEmailEditText = findViewById(R.id.riderEmailEditText);
        riderPhoneEditText = findViewById(R.id.riderPhoneEditText);
        riderPasswordEditText = findViewById(R.id.riderPasswordEditText);
        riderConfirmPasswordEditText = findViewById(R.id.riderConfirmPasswordEditText);
        riderRegisterButton = findViewById(R.id.riderRegisterButton);

        riderRegisterButton.setOnClickListener(v -> {
            String riderName = riderNameEditText.getText().toString();
            String riderEmail = riderEmailEditText.getText().toString();
            String riderPhone = riderPhoneEditText.getText().toString();
            String riderPassword = riderPasswordEditText.getText().toString();
            String riderConfirmPassword = riderConfirmPasswordEditText.getText().toString();

            if (validateInput(riderName, riderEmail, riderPhone, riderPassword, riderConfirmPassword)) {
                mAuth.createUserWithEmailAndPassword(riderEmail, riderPassword)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null) {
                                    String userId = currentUser.getUid();

                                    // Create a Rider object with the details to be saved in Firestore
                                    Rider newRider = new Rider(riderName, riderPhone);

                                    // Save additional rider details in Firestore
                                    db.collection("riders")
                                            .document(userId)
                                            .set(newRider, SetOptions.merge())
                                            .addOnSuccessListener(aVoid -> {
                                                mAuth.signOut();
                                                Toast.makeText(RegistrationActivity.this, "Registration successful! You are now signed out.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(RegistrationActivity.this, "Error saving rider data in Firestore. Please try again.", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private boolean validateInput(String riderName, String riderEmail, String riderPhone, String riderPassword, String riderConfirmPassword) {
        if (riderName.isEmpty() || riderEmail.isEmpty() || riderPhone.isEmpty() || riderPassword.isEmpty() || riderConfirmPassword.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(riderEmail).matches()) {
            Toast.makeText(RegistrationActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (riderPhone.length() != 10) {
            Toast.makeText(RegistrationActivity.this, "Phone number should be 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (riderPassword.length() < 6) {
            Toast.makeText(RegistrationActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!riderPassword.equals(riderConfirmPassword)) {
            Toast.makeText(RegistrationActivity.this, "Password and confirm password do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public class Rider {
        private String name;
        private String phone;

        public Rider() {
            // Default constructor required for Firestore
        }

        public Rider(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }
}









