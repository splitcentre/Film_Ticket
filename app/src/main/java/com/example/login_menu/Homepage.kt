package com.example.login_menu

import FilmAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_menu.database.FilmEntity
import com.example.login_menu.database.film
import com.google.firebase.database.*

class Homepage : AppCompatActivity() {
    private lateinit var username: String
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var database: DatabaseReference

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

        database = FirebaseDatabase.getInstance().reference.child("films")

        loadFilmData()
    }

    private fun loadFilmData() {
        val filmList = mutableListOf<film>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                filmList.clear()

                for (filmSnapshot in snapshot.children) {
                    val filmData = filmSnapshot.getValue(film::class.java)
                    filmData?.let { filmList.add(it) }
                }

                filmAdapter.submitList(filmList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Homepage, "Failed to load film data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onFilmItemClick(film: FilmEntity) {
        val intent = Intent(this, FilmDetailsActivity::class.java)
        intent.putExtra("FILM_NAME", film.filmName)
        intent.putExtra("FILM_RELEASE_DATE", film.filmReleaseDate)
        intent.putExtra("FILM_SYNOPSIS", film.filmSynopsis)
        startActivity(intent)
    }
}

