package com.example.login_menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Retrieve username from the intent
        val username = intent.getStringExtra("USERNAME")

        // Set the username in the TextView
        val usernameTextView = findViewById<TextView>(R.id.username)
        usernameTextView.text = username
    }
}