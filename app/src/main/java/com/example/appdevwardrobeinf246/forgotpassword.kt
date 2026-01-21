package com.example.appdevwardrobeinf246

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val newPasswordInput = findViewById<EditText>(R.id.newPasswordInput)
        val resetBtn = findViewById<Button>(R.id.resetBtn)

        resetBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val newPassword = newPasswordInput.text.toString()

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val savedUser = prefs.getString("username", null)

            if (username.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else if (username != savedUser) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit()
                    .putString("password", newPassword)
                    .apply()

                Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
