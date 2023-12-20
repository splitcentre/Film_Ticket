package com.example.login_menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_menu.database.FilmDatabase
import com.example.login_menu.database.FilmEntity
import com.example.login_menu.database.FilmRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

        // Set the layout manager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the adapter for the RecyclerView
        recyclerView.adapter = filmAdapter

        // Initialize Room database and repository
        roomDatabase = FilmDatabase.getInstance(applicationContext)
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore here
        filmRepository = FilmRepository(roomDatabase.filmDao(), firestore)

        // Load film data from Firestore
        loadFilmDataFromFirestore()

        // Handle "Add Movie" button click
        val addMovieButton: Button = findViewById(R.id.addmovie)
        addMovieButton.setOnClickListener {
            // Redirect to AddFilm activity
            val intent = Intent(this, AddFilm::class.java)
            startActivity(intent)
        }
    }

    private fun loadFilmDataFromFirestore() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val collectionRef = db.collection("films")

            val documents = collectionRef.get().await()
            val filmList = mutableListOf<FilmEntity>()
            for (document in documents) {
                val film = document.toObject(FilmEntity::class.java)
                film?.let { filmList.add(it) }
            }

            withContext(Dispatchers.Main) {
                filmAdapter.submitList(filmList)
            }
        }
    }

}
