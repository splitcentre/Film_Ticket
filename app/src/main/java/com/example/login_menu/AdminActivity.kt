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
import com.example.login_menu.database.film
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var filmRepository: FilmRepository
    private lateinit var roomDatabase: FilmDatabase
    private lateinit var firebaseDatabase: DatabaseReference

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
        filmRepository = FilmRepository(roomDatabase.filmDao())

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance().reference.child("films")

        // Load film data from both Firebase and Room
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
        // Load film data from Firebase
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseFilmList = mutableListOf<FilmEntity>()

                // Parse Firebase data and add to the list
                for (filmSnapshot in snapshot.children) {
                    val filmData = filmSnapshot.getValue(FilmEntity::class.java)
                    filmData?.let { firebaseFilmList.add(it) }
                }

                // Update the adapter with Firebase entities
                filmAdapter.submitList(firebaseFilmList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle Firebase error
            }
        })

        // Load film data from Room database
        lifecycleScope.launch(Dispatchers.Main) {
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
