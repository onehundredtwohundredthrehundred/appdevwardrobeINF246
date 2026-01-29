package com.example.appdevwardrobeinf246

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signupLink = findViewById<TextView>(R.id.signupLink)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, forgotpassword::class.java))
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, signup::class.java))
        }

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                authenticate(username, password)
            }
        }
    }

    private fun authenticate(username: String, password: String) {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedUser = prefs.getString("username", "")
        val savedPass = prefs.getString("password", "")

        if (username == savedUser && password == savedPass) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, mainapp::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
            finish()

        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }
}