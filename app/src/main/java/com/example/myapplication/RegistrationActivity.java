package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
    private EditText addressEditText;
    private Button registerButton;
    private Button getLocationButton;
    private Button uploadIdCardButton;
    private Button uploadLicenseButton;
    private ImageView identityCardImageView;
    private ImageView vehicleLicenseImageView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // This variable will store the selected image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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
        uploadIdCardButton = findViewById(R.id.uploadIdCardButton);
        uploadLicenseButton = findViewById(R.id.uploadLicenseButton);
        identityCardImageView = findViewById(R.id.identityCardImageView);
        vehicleLicenseImageView = findViewById(R.id.vehicleLicenseImageView);

        // Check if identity card and vehicle license URLs are already saved in Firestore
        loadImages();

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
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

        uploadIdCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        uploadLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        createLocationCallback();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData(); // This is the URI of the selected image
            // Now you can use the imageUri as needed, such as displaying the image or uploading it.
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                String addressText = address.getAddressLine(0);
                addressEditText.setText(addressText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateInput(String fullName, String email, String phone, String password, String confirmPassword) {
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegistrationActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.length() < 10) {
            Toast.makeText(RegistrationActivity.this, "Phone number should be at least 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(RegistrationActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registerUser(String email, String password, String selectedType, String companyName, String fullName, String phone, String address) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        User newUser = new User(selectedType, companyName, fullName, email, phone, address);
                        DocumentReference userRef = firestore.collection("user").document(userId);
                        userRef.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrationActivity.this, ProviderLoginFragment.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegistrationActivity.this, "Error saving user data in Firestore.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage(String imageType) {
        if (checkLocationPermission()) {
            // Implement the image upload logic using Firebase Storage
            // For example, you can create a unique file name using UUID
            String uniqueFilename = UUID.randomUUID().toString();
            StorageReference imageRef = storageRef.child("images/" + imageType + "/" + uniqueFilename);

            // Get the download URL after a successful upload
            imageRef.putFile(Uri.parse("Path:/storage/emulated/0/Pictures/IMG_20231101_032426.jpg"))
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();

                                    // Save the download URL in Firestore
                                    String userId = mAuth.getCurrentUser().getUid();
                                    DocumentReference userRef = firestore.collection("user").document(userId);
                                    userRef.update(imageType, downloadUrl)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(RegistrationActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                                                loadImages(); // Reload images to display the newly uploaded image
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(RegistrationActivity.this, "Error saving image URL in Firestore.", Toast.LENGTH_SHORT).show();
                                            });
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RegistrationActivity.this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void loadImages() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = firestore.collection("user").document(userId);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String identityCardUrl = documentSnapshot.getString("identity_card");
                            String vehicleLicenseUrl = documentSnapshot.getString("vehicle_license");

                            if (identityCardUrl != null) {
                                Glide.with(RegistrationActivity.this)
                                        .load(identityCardUrl)
                                        .into(identityCardImageView);
                            }

                            if (vehicleLicenseUrl != null) {
                                Glide.with(RegistrationActivity.this)
                                        .load(vehicleLicenseUrl)
                                        .into(vehicleLicenseImageView);
                            }
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






