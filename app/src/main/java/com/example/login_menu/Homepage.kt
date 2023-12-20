package com.example.login_menu

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_menu.database.FilmEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class Homepage : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        username = intent.getStringExtra("USERNAME") ?: ""

        val usernameTextView = findViewById<TextView>(R.id.username)
        usernameTextView.text = username

        val recyclerView: RecyclerView = findViewById(R.id.list_film)
        filmAdapter = FilmAdapter { film -> onFilmItemClick(film) }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = filmAdapter

        firestore = FirebaseFirestore.getInstance()

        loadFilmData()
    }

    private fun loadFilmData() {
        val firestoreFilmList = mutableListOf<FilmEntity>()

        firestore.collection("films")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                for (document: QueryDocumentSnapshot in querySnapshot) {
                    val film = FilmEntity(
                        filmImage = document.getString("filmImage") ?: "",
                        filmName = document.getString("filmName") ?: "",
                        filmReleaseDate = document.getString("filmReleaseDate")?.toInt() ?: 0,
                        // Add other fields if needed
                    )
                    firestoreFilmList.add(film)
                }

                filmAdapter.submitList(firestoreFilmList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@Homepage,
                    "Failed to load film data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun onFilmItemClick(film: FilmEntity) {
        val intent = Intent(this, FilmDetailsActivity::class.java)
        intent.putExtra("FILM_NAME", film.filmName)
        intent.putExtra("FILM_RELEASE_DATE", film.filmReleaseDate)
        // Add other fields if needed
        startActivity(intent)
    }
}
