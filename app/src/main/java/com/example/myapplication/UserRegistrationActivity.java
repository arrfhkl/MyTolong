package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (validateInput(fullName, email, phone, password, confirmPassword)) {
                    registerUser(email, password, fullName, phone);
                }
            }
        });
    }

    // Inside the registerUser method
    private void registerUser(String email, String password, String fullName, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();

                                // Create a Map to store user data
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("fullName", fullName);
                                userData.put("email", email);
                                userData.put("phone", phone);

                                // Get a reference to the Firestore database
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                // Add the user data to Firestore
                                db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // User data added to Firestore successfully

                                                    // You can add a success message or navigate to the next screen here
                                                    Toast.makeText(UserRegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Error adding user data to Firestore
                                                    Toast.makeText(UserRegistrationActivity.this, "Error saving user data in Firestore.", Toast.LENGTH_SHORT).show();
                                                }

                                                // Now, navigate to the next screen, e.g., ProviderLoginActivity, regardless of Firestore success
                                                Intent intent = new Intent(UserRegistrationActivity.this, UsernameLoginFragment.class);
                                                startActivity(intent);
                                                finish(); // Close the registration activity
                                            }
                                        });
                            }
                        } else {
                            // Registration failed, handle errors
                            Toast.makeText(UserRegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();

                            // Navigate to the next screen, e.g., ProviderLoginActivity, in case of registration failure
                            Intent intent = new Intent(UserRegistrationActivity.this, UsernameLoginFragment.class);
                            startActivity(intent);
                            finish(); // Close the registration activity
                        }
                    }
                });
    }

    private boolean validateInput(String fullName, String email, String phone, String password, String confirmPassword) {
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            // Check if any field is empty
            Toast.makeText(UserRegistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Check if the email is in a valid format
            Toast.makeText(UserRegistrationActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.length() < 10) {
            // Check if the phone number is less than 10 digits
            Toast.makeText(UserRegistrationActivity.this, "Phone number should be at least 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            // Check if the password is at least 6 characters long (you can adjust this as needed)
            Toast.makeText(UserRegistrationActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            // Check if the password and confirm password match
            Toast.makeText(UserRegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // If all checks pass, return true to indicate valid input
        return true;
    }
}
