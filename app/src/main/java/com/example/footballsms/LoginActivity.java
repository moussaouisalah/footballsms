package com.example.footballsms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());

            String toastText = "";
            if(sqLiteHelper.loginUser(username, password))
                toastText = "Login Correct";
            else
                toastText = "Login Incorrect";

            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();


        });
    }
}