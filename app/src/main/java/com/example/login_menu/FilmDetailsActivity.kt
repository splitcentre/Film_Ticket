package com.example.login_menu

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class FilmDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_details)

        // Find the backarrow ImageView
        val backArrowImageView = findViewById<ImageView>(R.id.backarrow)

        // Set an OnClickListener to handle the click event
        backArrowImageView.setOnClickListener {
            onBackPressed()
        }

        // Retrieve data passed from Homepage activity
        val filmName = intent.getStringExtra("FILM_NAME") ?: ""

        // Fetch additional data from Firestore based on the film name
        fetchFilmDetailsFromFirestore(filmName)
    }

    private fun fetchFilmDetailsFromFirestore(filmName: String) {
        val firestore = FirebaseFirestore.getInstance()

        // Reference to the "films" collection in Firestore
        val filmsCollection = firestore.collection("films")

        // Query to find the document with the matching filmName
        val query = filmsCollection.whereEqualTo("filmName", filmName)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Retrieve the first document (assuming filmName is unique)
                    val document = querySnapshot.documents[0]

                    // Extract data from the document
                    val filmReleaseDateLong = document.getLong("filmReleaseDate") ?: 0
                    val filmReleaseDate = filmReleaseDateLong.toString()
                    val filmSynopsis = document.getString("filmSynopsis") ?: ""
                    // Add other fields if needed

                    // Display film details in TextViews or other UI elements
                    val filmNameTextView = findViewById<TextView>(R.id.txt_film_name)
                    val releaseDateTextView = findViewById<TextView>(R.id.date)
                    val synopsisTextView = findViewById<TextView>(R.id.synopsis)
                    // Add other TextViews or UI elements for additional fields

                    filmNameTextView.text = filmName
                    releaseDateTextView.text = filmReleaseDate
                    synopsisTextView.text = filmSynopsis
                    // Set values for other UI elements based on the data received

                    // Load the film image using Glide (or your preferred image loading library)
                    val filmImageView = findViewById<ImageView>(R.id.film_image)
                    Glide.with(this)
                        .load(document.getString("filmImage"))  // Use the actual field name
                        .placeholder(R.drawable.picture)
                        .error(R.drawable.baseline_error_outline_24)
                        .into(filmImageView)
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to retrieve data from Firestore
                // You can show an error message or log the exception
            }
    }

}
