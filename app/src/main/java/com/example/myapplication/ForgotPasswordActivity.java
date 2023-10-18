package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private RadioGroup resetMethodRadioGroup;
    private RadioButton resetByEmailRadioButton;
    private RadioButton resetByPhoneNumberRadioButton;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetMethodRadioGroup = findViewById(R.id.resetMethodRadioGroup);
        resetByEmailRadioButton = findViewById(R.id.resetByEmailRadioButton);
        resetByPhoneNumberRadioButton = findViewById(R.id.resetByPhoneNumberRadioButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        resetMethodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.resetByEmailRadioButton) {
                    emailEditText.setVisibility(View.VISIBLE);
                    phoneNumberEditText.setVisibility(View.GONE);
                } else if (checkedId == R.id.resetByPhoneNumberRadioButton) {
                    emailEditText.setVisibility(View.GONE);
                    phoneNumberEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected method (email or phone)
                String selectedMethod = resetByEmailRadioButton.isChecked() ? "Email" : "Phone";

                // Get the entered username, email, and phone number
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();

                // Perform password reset based on the selected method
                if (selectedMethod.equals("Email")) {
                    // Handle password reset by email for the given username
                    // ...
                } else if (selectedMethod.equals("Phone")) {
                    // Handle password reset by phone number for the given username
                    // ...
                }
            }
        });
    }
}

