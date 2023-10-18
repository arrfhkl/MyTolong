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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                    // Register user with Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if (currentUser != null) {
                                            String userId = currentUser.getUid();
                                            @SuppressLint("RestrictedApi") MyAppUser newUser = new MyAppUser(selectedType, companyName, fullName, email, phone);
                                            databaseReference.child(userId).setValue(newUser);

                                            // Login the user after successful registration
                                            mAuth.signInWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // Inside your login method, after login is successful
                                                                Toast.makeText(RegistrationActivity.this, "Registration and login successful!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(RegistrationActivity.this, ProviderLoginFragment.class);
                                                                startActivity(intent);
                                                                finish(); // Close the registration activity
                                                            } else {
                                                                // Login failed, handle errors
                                                                Toast.makeText(RegistrationActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                                                            }
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
            }
        });
    }

    // Validation method as in your previous code
    private boolean validateInput(String fullName, String email, String phone, String password, String confirmPassword) {
        // Validation code here
        return true;
    }

    // MyAppUser class with getters and setters
    public class MyAppUser {
        private String userType;
        private String companyName;
        private String fullName;
        private String email;
        private String phone;

        public MyAppUser() {
            // Default constructor required for Firebase
        }

        public MyAppUser(String userType, String companyName, String fullName, String email, String phone) {
            this.userType = userType;
            this.companyName = companyName;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}




