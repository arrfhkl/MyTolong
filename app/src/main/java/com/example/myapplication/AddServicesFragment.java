package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AddServicesFragment extends Fragment {

    private ListView serviceListView;
    private List<String> addedServices;
    private ArrayAdapter<String> serviceListAdapter;
    private EditText itemNameEditText;
    private EditText itemPriceEditText;

    public static AddServicesFragment newInstance() {
        return new AddServicesFragment();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_services, container, false);

        serviceListView = view.findViewById(R.id.serviceListView);
        addedServices = new ArrayList<>();
        serviceListAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, addedServices);
        serviceListView.setAdapter(serviceListAdapter);

        itemNameEditText = view.findViewById(R.id.itemNameEditText);
        itemPriceEditText = view.findViewById(R.id.itemPriceEditText);

        Button btnAddService = view.findViewById(R.id.btnAddService);
        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the custom service dialog or perform any other action
                showCustomServiceDialog();
            }
        });

        return view;
    }

    private void showCustomServiceDialog() {
        CustomServiceDialog dialog = new CustomServiceDialog(new CustomServiceDialog.ServiceDialogListener() {
            @Override
            public void onServiceAdded(String serviceType, String itemName, String itemPrice) {
                // Handle the service addition here
                // You can add the service details to your addedServices list
                // and update the list adapter
                String serviceDetails = "Service Type: " + serviceType + ", Item Name: " + itemName + ", Item Price: " + itemPrice;
                addedServices.add(serviceDetails);
                serviceListAdapter.notifyDataSetChanged();
            }
        });
        dialog.show(getFragmentManager(), "customServiceDialog");
    }

    private void clearFields() {
        itemNameEditText.setText("");
        itemPriceEditText.setText("");
    }
}
