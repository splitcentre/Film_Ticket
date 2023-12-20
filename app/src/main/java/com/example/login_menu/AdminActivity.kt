package com.example.login_menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_menu.database.FilmDatabase
import com.example.login_menu.database.FilmEntity
import com.example.login_menu.database.FilmRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var filmRepository: FilmRepository
    private lateinit var roomDatabase: FilmDatabase
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Retrieve username from the intent
        username = intent.getStringExtra("USERNAME") ?: ""

        // Set the username in the TextView
        val usernameTextView = findViewById<TextView>(R.id.username)
        usernameTextView.text = "$username"

        // Initialize RecyclerView and FilmAdapter
        val recyclerView: RecyclerView = findViewById(R.id.list_film)
        filmAdapter = FilmAdapter { film -> onFilmItemClick(film) }

        // Set the layout manager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the adapter for the RecyclerView
        recyclerView.adapter = filmAdapter

        // Initialize Room database and repository
        roomDatabase = FilmDatabase.getInstance(applicationContext)
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore here
        filmRepository = FilmRepository(roomDatabase.filmDao(), firestore)

        // Load film data from Firestore
        loadFilmData()

        // Handle "Add Movie" button click
        val addMovieButton: Button = findViewById(R.id.addmovie)
        addMovieButton.setOnClickListener {
            // Redirect to AddFilm activity
            val intent = Intent(this, AddFilm::class.java)
            startActivity(intent)
        }
    }

    private fun loadFilmData() {
        lifecycleScope.launch(Dispatchers.Main) {
            // Fetch data from Firestore and save to Room database
            filmRepository.fetchDataFromFirestoreAndSaveToLocal()

            // Load film data from Room database
            val roomFilmList = filmRepository.getAllFilms()

            // Update the adapter with Room entities
            filmAdapter.submitList(roomFilmList)
        }
    }

    private fun onFilmItemClick(film: FilmEntity) {
        // Handle film item click (e.g., open details activity)
        val intent = Intent(this, FilmDetailsActivity::class.java)
        intent.putExtra("FILM_ID", film.id)
        startActivity(intent)
    }
}
