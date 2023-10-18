package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button directionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Initialize the directions button
        directionsButton = findViewById(R.id.directionsButton);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Google Maps with directions to a specific location
                openDirectionsInGoogleMaps();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for the target location
        LatLng targetLocation = new LatLng(37.7749, -122.4194); // Example: San Francisco, CA
        mMap.addMarker(new MarkerOptions().position(targetLocation).title("Target Location"));

        // Move the camera to the target location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 14)); // Adjust the zoom level as needed
    }

    private void openDirectionsInGoogleMaps() {
        // Replace the latitude and longitude with your target location's coordinates
        double latitude = 37.7749; // Example: San Francisco, CA
        double longitude = -122.4194;

        // Create a Uri to launch Google Maps with directions
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);

        // Create an Intent to open Google Maps
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Specify the Google Maps app package

        // Check if Google Maps is installed on the device
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Google Maps app is not installed, handle accordingly (e.g., show a message)
            // You can also open a web-based map if desired.
        }
    }
}

