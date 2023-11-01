package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private RadioGroup radioGroup;
    private RadioButton radioCompany;
    private RadioButton radioIndividual;
    private EditText companyNameEditText;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText addressEditText;  // Combined address field
    private Button registerButton;
    private Button getLocationButton;  // Button to get location and fill address

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        radioGroup = findViewById(R.id.radioGroup);
        radioCompany = findViewById(R.id.radioCompany);
        radioIndividual = findViewById(R.id.radioIndividual);
        companyNameEditText = findViewById(R.id.companyNameEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        addressEditText = findViewById(R.id.editTextLocation);
        registerButton = findViewById(R.id.registerButton);
        getLocationButton = findViewById(R.id.btnGetLocation);

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    // Request location updates
                    startLocationUpdates();
                }
            }
        });

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
                String address = addressEditText.getText().toString();

                if (validateInput(fullName, email, phone, password, confirmPassword)) {
                    registerUser(email, password, selectedType, companyName, fullName, phone, address);
                }
            }
        });

        createLocationCallback();
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates when the activity is not in the foreground
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        updateAddressFromLocation(location);
                    }
                }
            }
        };
    }

    private void updateAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);  // Get the first line of the address
                addressEditText.setText(addressText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void registerUser(String email, String password, String selectedType, String companyName, String fullName, String phone, String address) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();

                                // Create a User object to store user data
                                User newUser = new User(selectedType, companyName, fullName, email, phone, address);

                                // Save the user data in Firestore under the generated user ID
                                DocumentReference userRef = firestore.collection("user").document(userId);
                                userRef.set(newUser)
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
        private String address;

        public User() {
            // Default constructor required for Firestore
        }

        public User(String userType, String companyName, String fullName, String email, String phone, String address) {
            this.userType = userType;
            this.companyName = companyName;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.address = address;
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

        public String getAddress() {
            return address;
        }
    }
}





