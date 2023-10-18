package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProviderProfileFragment extends Fragment {

    private TextView companyNameTextView;
    private TextView phoneNumberTextView;
    private TextView emailTextView;
    private Button editProfileButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provider_profile, container, false);

        // Initialize UI elements
        companyNameTextView = view.findViewById(R.id.companyNameTextView);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);

        // Set provider information (company name, phone number, and email)
        String companyName = "Your Company Name"; // Replace with actual data
        String phoneNumber = "Your Phone Number";   // Replace with actual data
        String email = "Your Email Address";         // Replace with actual data

        companyNameTextView.setText(companyName);
        phoneNumberTextView.setText(phoneNumber);
        emailTextView.setText(email);

        // Set up a click listener for the Edit Profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event to navigate to the profile editing screen
                // You can start a new fragment or activity for editing the profile here
                // For simplicity, I'm not including the editing logic in this example
            }
        });

        return view;
    }
}

