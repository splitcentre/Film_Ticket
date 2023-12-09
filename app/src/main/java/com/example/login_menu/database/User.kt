package com.example.login_menu.database

data class User(
    val userId: String, // Unique identifier for the user
    val email: String,
    val username:String,
    val role: String, // Role can be "Admin" or "User"
    val hashedPassword: String // Hashed password
)

