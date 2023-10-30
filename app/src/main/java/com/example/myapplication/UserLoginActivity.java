package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UserLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_username_login);

        // Load the UsernameLoginFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new UsernameLoginFragment())
                .commit();
    }
}