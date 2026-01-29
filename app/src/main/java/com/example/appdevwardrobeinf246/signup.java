package com.example.appdevwardrobeinf246;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText username = findViewById(R.id.signupUsername);
        EditText password = findViewById(R.id.signupPassword);
        Button signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                prefs.edit()
                        .putString("username", user)
                        .putString("password", pass)
                        .apply();

                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}