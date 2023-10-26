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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProviderProfileFragment extends Fragment {

    private TextView companyNameTextView;
    private TextView phoneNumberTextView;
    private TextView emailTextView;
    private Button editProfileButton;

    // Initialize Firebase Firestore and Firebase Authentication
    private FirebaseFirestore db;
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

        // Initialize Firebase Firestore and Firebase Authentication
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get the current user's UID
        String userId = mAuth.getCurrentUser().getUid();

        // Reference to the user's document in Firestore
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Data found in Firestore
                            String companyName = document.getString("companyName");
                            String phoneNumber = document.getString("phone");
                            String email = mAuth.getCurrentUser().getEmail();

                            // Update the UI elements with Firestore data
                            companyNameTextView.setText(companyName);
                            phoneNumberTextView.setText(phoneNumber);
                            emailTextView.setText(email);
                        } else {
                            // Handle the case where the document doesn't exist
                        }
                    } else {
                        // Handle errors while fetching data from Firestore
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


