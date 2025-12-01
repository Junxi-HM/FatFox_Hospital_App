package com.example.fatfoxhospital.pr06

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fatfoxhospital.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Login button logic - now using NurseList
        binding.loginButton.setOnClickListener {
            val username = binding.user.text.toString().trim()
            val password = binding.password.text.toString().trim()

            // Authenticate against NurseList.mockNurses
            val isValidUser = NurseList.mockNurses.any {
                it.user == username && it.password == password
            }

            if (isValidUser) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                // Navigate to main screen
                val intent = Intent(this, NurseMainScreen::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
                finish() // Optional: prevent returning to login with back button
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                // Stay on login page
            }
        }

        // Back to Main button (always visible)
        binding.backButton.setOnClickListener {
            val intent = Intent(this, NurseMainScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }
    }
}