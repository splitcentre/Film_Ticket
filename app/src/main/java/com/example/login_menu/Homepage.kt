package com.example.login_menu

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
import com.google.firebase.database.ValueEventListener

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
    fun convertFirebaseFilmsToRoomEntities(firebaseFilms: List<film>): List<FilmEntity> {
        return firebaseFilms.map { firebaseFilm ->
            FilmEntity(
                filmImage = firebaseFilm.filmImage,
                filmName = firebaseFilm.filmName,
                filmReleaseDate = firebaseFilm.filmReleaseDate,
                filmSynopsis = firebaseFilm.filmSynopsis
            )
        }
    }

    private fun loadFilmData() {
        val firebaseFilmList = mutableListOf<film>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                firebaseFilmList.clear()

                for (filmSnapshot in snapshot.children) {
                    val filmData = filmSnapshot.getValue(film::class.java)
                    filmData?.let { firebaseFilmList.add(it) }
                }

                // Convert List<film> to List<FilmEntity>
                val roomFilmList = convertFirebaseFilmsToRoomEntities(firebaseFilmList)

                // Update the adapter with converted data
                filmAdapter.submitList(roomFilmList)
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

