package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomServiceDialog extends DialogFragment {

    private Spinner serviceTypeSpinner;
    private EditText itemNameEditText;
    private EditText itemPriceEditText;

    public interface ServiceDialogListener {
        void onServiceAdded(String serviceType, String itemName, String itemPrice);
    }

    private ServiceDialogListener listener;

    public CustomServiceDialog(ServiceDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_custom_service_dialog, null);

        serviceTypeSpinner = view.findViewById(R.id.serviceTypeSpinner);
        itemNameEditText = view.findViewById(R.id.itemNameEditText);
        itemPriceEditText = view.findViewById(R.id.itemPriceEditText);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.service_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceTypeSpinner.setAdapter(adapter);

        builder.setView(view)
                .setTitle("Add Service")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String serviceType = serviceTypeSpinner.getSelectedItem().toString();
                        String itemName = itemNameEditText.getText().toString();
                        String itemPrice = itemPriceEditText.getText().toString();

                        if (listener != null) {
                            listener.onServiceAdded(serviceType, itemName, itemPrice);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel the dialog
                        dismiss();
                    }
                });

        return builder.create();
    }
}
