package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    private ListView customerListView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        customerListView = view.findViewById(R.id.customerListView);

        // Sample list of customer names
        final String[] customerNames = {"Customer 1", "Customer 2", "Customer 3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, customerNames);
        customerListView.setAdapter(adapter);

        // Set click listener for the customer list items
        customerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected customer name
                String selectedCustomer = customerNames[position];

                // Start the ChatPageActivity and pass the selected customer name
                Intent intent = new Intent(requireActivity(), ChatActivity.class);
                intent.putExtra("customerName", selectedCustomer);
                startActivity(intent);
            }
        });

        return view;
    }
}
