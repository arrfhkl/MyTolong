package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProviderProfileFragment extends Fragment {

    private TextView companyNameTextView;
    private TextView phoneNumberTextView;
    private TextView emailTextView;
    private TextView fullNameTextView; // New TextView for Full Name
    private TextView typeTextView; // New TextView for Type (Company or Individual)
    private TextView addressLine1TextView; // New TextView for Address Line 1
    private TextView addressLine2TextView; // New TextView for Address Line 2
    private TextView districtTextView; // New TextView for District
    private TextView postcodeTextView; // New TextView for Postcode
    private TextView stateTextView; // New TextView for State
    private Button editProfileButton;

    // Initialize Firebase Realtime Database and Firebase Authentication
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provider_profile, container, false);

        // Initialize UI elements
        companyNameTextView = view.findViewById(R.id.companyNameTextView);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        fullNameTextView = view.findViewById(R.id.fullNameTextView); // Initialize Full Name TextView
        typeTextView = view.findViewById(R.id.typeTextView); // Initialize Type TextView
        addressLine1TextView = view.findViewById(R.id.addressLine1TextView); // Initialize Address Line 1 TextView
        addressLine2TextView = view.findViewById(R.id.addressLine2TextView); // Initialize Address Line 2 TextView
        districtTextView = view.findViewById(R.id.districtTextView); // Initialize District TextView
        postcodeTextView = view.findViewById(R.id.postcodeTextView); // Initialize Postcode TextView
        stateTextView = view.findViewById(R.id.stateTextView); // Initialize State TextView
        editProfileButton = view.findViewById(R.id.editProfileButton);

        // Initialize Firebase Realtime Database and Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        // Get the current user's UID
        String userId = mAuth.getCurrentUser().getUid();

        // Reference to the user's data in the Realtime Database
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Data found in Realtime Database
                    String companyName = dataSnapshot.child("companyName").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phone").getValue(String.class);
                    String email = mAuth.getCurrentUser().getEmail();
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String userType = dataSnapshot.child("userType").getValue(String.class);
                    String addressLine1 = dataSnapshot.child("addressLine1").getValue(String.class);
                    String addressLine2 = dataSnapshot.child("addressLine2").getValue(String.class);
                    String district = dataSnapshot.child("district").getValue(String.class);
                    String postcode = dataSnapshot.child("postcode").getValue(String.class);
                    String state = dataSnapshot.child("state").getValue(String.class);

                    // Update the UI elements with Realtime Database data
                    companyNameTextView.setText(companyName);
                    phoneNumberTextView.setText(phoneNumber);
                    emailTextView.setText(email);
                    fullNameTextView.setText(fullName); // Display Full Name
                    typeTextView.setText(userType); // Display Type (Company or Individual)
                    addressLine1TextView.setText(addressLine1); // Display Address Line 1
                    addressLine2TextView.setText(addressLine2); // Display Address Line 2
                    districtTextView.setText(district); // Display District
                    postcodeTextView.setText(postcode); // Display Postcode
                    stateTextView.setText(state); // Display State
                } else {
                    // Handle the case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors while fetching data from Realtime Database
            }
        });

        // Set up a click listener for the Edit Profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start the ProfileEditActivity
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}





