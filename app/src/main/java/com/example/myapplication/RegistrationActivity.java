package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioCompany;
    private RadioButton radioIndividual;
    private EditText companyNameEditText;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseApp.initializeApp(this);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        radioGroup = findViewById(R.id.radioGroup);
        radioCompany = findViewById(R.id.radioCompany);
        radioIndividual = findViewById(R.id.radioIndividual);
        companyNameEditText = findViewById(R.id.companyNameEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioCompany) {
                    companyNameEditText.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radioIndividual) {
                    companyNameEditText.setVisibility(View.GONE);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = radioCompany.isChecked() ? "Company" : "Individual";
                String companyName = companyNameEditText.getText().toString();
                String fullName = fullNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (validateInput(fullName, email, phone, password, confirmPassword)) {
                    registerUser(email, password, selectedType, companyName, fullName, phone);
                }
            }
        });
    }

    private boolean validateInput(String fullName, String email, String phone, String password, String confirmPassword) {
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            // Check if any field is empty
            Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Check if the email is in a valid format
            Toast.makeText(RegistrationActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.length() < 10) {
            // Check if the phone number is less than 10 digits
            Toast.makeText(RegistrationActivity.this, "Phone number should be at least 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            // Check if the password is at least 6 characters long (you can adjust this as needed)
            Toast.makeText(RegistrationActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            // Check if the password and confirm password match
            Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // If all checks pass, return true to indicate valid input
        return true;
    }


    private void registerUser(String email, String password, String selectedType, String companyName, String fullName, String phone) {
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
                                userData.put("userType", selectedType);
                                userData.put("companyName", companyName);
                                userData.put("fullName", fullName);
                                userData.put("email", email);
                                userData.put("phone", phone);

                                // Get a reference to the Firestore database
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                // Add the user data to Firestore
                                db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // User data added to Firestore successfully

                                                // You can add a success message or navigate to the next screen here
                                                // For example:
                                                Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                                                // Navigate to the next screen, e.g., ProviderLoginActivity
                                                Intent intent = new Intent(RegistrationActivity.this, ProviderLoginFragment.class);
                                                startActivity(intent);
                                                finish(); // Close the registration activity
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Error adding user data to Firestore
                                                Toast.makeText(RegistrationActivity.this, "Error saving user data in Firestore.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Registration failed, handle errors
                            Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    public class User {
        private String userType;
        private String companyName;
        private String fullName;
        private String email;
        private String phone;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userType, String companyName, String fullName, String email, String phone) {
            this.userType = userType;
            this.companyName = companyName;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
        }

        public String getUserType() {
            return userType;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }
    }

}

