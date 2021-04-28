package com.example.footballsms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {
    TextView textViewUsername, textViewChangeUsername, textViewChangePassword;
    Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textViewUsername = findViewById(R.id.textViewUsername);
        textViewChangeUsername = findViewById(R.id.textViewChangeUsername);
        textViewChangePassword = findViewById(R.id.textViewChangePassword);
        buttonLogout = findViewById(R.id.buttonLogout);

        PreferencesManager preferencesManager = new PreferencesManager(getApplicationContext());
        String username = preferencesManager.getUsername();

        textViewUsername.setText("Bonjour, " + username);

        textViewChangeUsername.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Not implemented yet!", Toast.LENGTH_LONG)
                    .show();
        });

        textViewChangePassword.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Not implemented yet!", Toast.LENGTH_LONG)
                    .show();
        });

        buttonLogout.setOnClickListener(view -> {
            preferencesManager.logout();
            finish();
        });
    }
}