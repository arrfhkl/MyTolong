package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class ServiceOrderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_order, container, false);

        Button acceptButton = view.findViewById(R.id.acceptButton);
        Button rejectButton = view.findViewById(R.id.rejectButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle accept button click
                // You can implement the logic to accept the order here
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle reject button click
                // You can implement the logic to reject the order here
            }
        });

        return view;
    }
}
