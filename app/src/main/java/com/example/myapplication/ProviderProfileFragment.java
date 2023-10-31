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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ProviderProfileFragment extends Fragment {

    private TextView companyNameTextView;
    private TextView phoneNumberTextView;
    private TextView emailTextView;
    private TextView fullNameTextView;
    private TextView typeTextView;
    private TextView addressLine1TextView;
    private TextView addressLine2TextView;
    private TextView districtTextView;
    private TextView postcodeTextView;
    private TextView stateTextView;
    private Button editProfileButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ListenerRegistration userSnapshotListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provider_profile, container, false);

        companyNameTextView = view.findViewById(R.id.companyNameTextView);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        typeTextView = view.findViewById(R.id.typeTextView);
        addressLine1TextView = view.findViewById(R.id.addressLine1TextView);
        addressLine2TextView = view.findViewById(R.id.addressLine2TextView);
        districtTextView = view.findViewById(R.id.districtTextView);
        postcodeTextView = view.findViewById(R.id.postcodeTextView);
        stateTextView = view.findViewById(R.id.stateTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String userId = mAuth.getCurrentUser().getUid();

        // Reference to the user's data in Firestore
        userSnapshotListener = firestore.collection("user")
                .document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        // Handle errors while fetching data from Firestore
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Data found in Firestore
                        String companyName = documentSnapshot.getString("companyName");
                        String phoneNumber = documentSnapshot.getString("phone");
                        String email = mAuth.getCurrentUser().getEmail();
                        String fullName = documentSnapshot.getString("fullName");
                        String userType = documentSnapshot.getString("userType");
                        String addressLine1 = documentSnapshot.getString("addressLine1");
                        String addressLine2 = documentSnapshot.getString("addressLine2");
                        String district = documentSnapshot.getString("district");
                        String postcode = documentSnapshot.getString("postcode");
                        String state = documentSnapshot.getString("state");

                        // Update the UI elements with Firestore data
                        companyNameTextView.setText(companyName);
                        phoneNumberTextView.setText(phoneNumber);
                        emailTextView.setText(email);
                        fullNameTextView.setText(fullName);
                        typeTextView.setText(userType);
                        addressLine1TextView.setText(addressLine1);
                        addressLine2TextView.setText(addressLine2);
                        districtTextView.setText(district);
                        postcodeTextView.setText(postcode);
                        stateTextView.setText(state);
                    } else {
                        // Handle the case where the data doesn't exist
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove Firestore listener when the fragment is destroyed
        if (userSnapshotListener != null) {
            userSnapshotListener.remove();
        }
    }
}






