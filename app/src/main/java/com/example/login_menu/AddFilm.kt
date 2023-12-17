package com.example.login_menu

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.login_menu.database.FilmEntity
import com.google.firebase.database.*


class AddFilm : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_film)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("films")

        val btnUpdateMov = findViewById<ImageButton>(R.id.btn_Updatemov)
        val btnDelete = findViewById<ImageButton>(R.id.btn_delete)

        btnUpdateMov.setOnClickListener {
            addFilmToDatabase()
        }

        btnDelete.setOnClickListener {
            // Add logic for delete operation if needed
        }
    }

    private fun addFilmToDatabase() {
        // Retrieve data from UI components
        val titleEditText: EditText = findViewById(R.id.Title_input)
        val releaseDateEditText: EditText = findViewById(R.id.input_genre)
        val synopsisEditText: EditText = findViewById(R.id.synopsis)

        val title = titleEditText.text.toString()
        val releaseDate = releaseDateEditText.text.toString()
        val synopsis = synopsisEditText.text.toString()

        // Validate input
        if (title.isEmpty() || releaseDate.isEmpty() || synopsis.isEmpty()) {
            // Show an error message or toast if any field is empty
            return
        }

        // Check if the film with the same name already exists
        val query = database.orderByChild("filmName").equalTo(title)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Film with the same name already exists, update the data
                    for (filmSnapshot in snapshot.children) {
                        val filmId = filmSnapshot.key
                        filmId?.let {
                            // Assuming you have the actual URLs and values, replace the placeholders below
                            val film = FilmEntity(
                                filmImage = "url_to_image",
                                filmName = title,
                                filmReleaseDate = 2023, // Replace with the actual release date
                                filmSynopsis = synopsis
                            )
                            database.child(it).setValue(film)
                        }
                    }
                } else {
                    // Film with the same name doesn't exist, add new data
                    val filmId = database.push().key
                    filmId?.let {
                        // Assuming you have the actual URLs and values, replace the placeholders below
                        val film = FilmEntity(
                            filmImage = "film_ex",
                            filmName = title,
                            filmReleaseDate = 2023, // Replace with the actual release date
                            filmSynopsis = synopsis
                        )
                        database.child(it).setValue(film)
                    }
                }

                // Clear input fields
                titleEditText.text.clear()
                releaseDateEditText.text.clear()
                synopsisEditText.text.clear()

                // Show notification here if needed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}


