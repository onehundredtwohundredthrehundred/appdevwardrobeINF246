package com.example.appdevwardrobeinf246

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val username = findViewById<EditText>(R.id.signupUsername)
        val password = findViewById<EditText>(R.id.signupPassword)
        val signupBtn = findViewById<Button>(R.id.signupBtn)

        signupBtn.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                prefs.edit()
                    .putString("username", user)
                    .putString("password", pass)
                    .apply()

                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}