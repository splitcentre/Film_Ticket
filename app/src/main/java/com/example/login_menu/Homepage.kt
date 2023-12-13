package com.example.login_menu

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_menu.database.film
import com.google.firebase.database.*

class Homepage : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Retrieve username from the intent
        username = intent.getStringExtra("USERNAME") ?: ""

        // Set the username in the TextView
        val usernameTextView = findViewById<TextView>(R.id.username)
        usernameTextView.text = username

        // Initialize RecyclerView and FilmAdapter
        val recyclerView: RecyclerView = findViewById(R.id.list_film)
        filmAdapter = FilmAdapter { film -> onFilmItemClick(film) }

        // Set the layout manager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the adapter for the RecyclerView
        recyclerView.adapter = filmAdapter

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("films")

        // Load film data from Firebase
        loadFilmData()
    }

    private fun loadFilmData() {
        val filmList = mutableListOf<film>()

        // Read data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing list
                filmList.clear()

                // Parse Firebase data and add to the list
                for (filmSnapshot in snapshot.children) {
                    val filmData = filmSnapshot.getValue(film::class.java)
                    filmData?.let { filmList.add(it) }
                }

                // Update the adapter
                filmAdapter.submitList(filmList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(this@Homepage, "Failed to load film data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onFilmItemClick(film: film) {
        // TODO: Implement logic for handling item click (e.g., open a new activity with film details)
        // Example:
        val intent = Intent(this, FilmDetailsActivity::class.java)
        intent.putExtra("FILM_NAME", film.filmName)
        intent.putExtra("FILM_RELEASE_DATE", film.filmReleaseDate)
        intent.putExtra("FILM_SYNOPSIS", film.filmSynopsis)
        startActivity(intent)
    }
}
