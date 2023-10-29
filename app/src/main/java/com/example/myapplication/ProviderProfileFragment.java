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

                    // Update the UI elements with Realtime Database data
                    companyNameTextView.setText(companyName);
                    phoneNumberTextView.setText(phoneNumber);
                    emailTextView.setText(email);
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



