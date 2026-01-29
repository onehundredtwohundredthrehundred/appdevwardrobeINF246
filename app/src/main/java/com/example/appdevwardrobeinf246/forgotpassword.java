package com.example.appdevwardrobeinf246;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class forgotpassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText newPasswordInput = findViewById(R.id.newPasswordInput);
        Button resetBtn = findViewById(R.id.resetBtn);

        resetBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String newPassword = newPasswordInput.getText().toString();

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String savedUser = prefs.getString("username", null);

            if (username.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (!username.equals(savedUser)) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit()
                        .putString("password", newPassword)
                        .apply();

                Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}