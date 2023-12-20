package com.example.login_menu

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.login_menu.database.FilmEntity
import com.example.login_menu.database.FilmDao
import com.example.login_menu.database.FilmDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var releaseDateEditText: EditText
    private lateinit var synopsisEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var filmDao: FilmDao
    private var updateId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        titleEditText = findViewById(R.id.Title_input)
        releaseDateEditText = findViewById(R.id.input_genre)
        synopsisEditText = findViewById(R.id.synopsis)
        submitButton = findViewById(R.id.submitfilm)

        // Retrieve film ID from the intent
        updateId = intent.getStringExtra("FILM_ID") ?: ""

        // Initialize your Room database
        val db = FilmDatabase.getInstance(application)
        filmDao = db.filmDao()

        lifecycleScope.launch(Dispatchers.Main) {
            // Use a coroutine to retrieve and set the film details
            retrieveFilmDetails(updateId)
            populateUI()
        }

        submitButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                updateFilmDetails()
                finish()
            }
        }
    }

    private suspend fun retrieveFilmDetails(id: String) {
        // Replace with your actual implementation to retrieve film details
        // from the database based on the film ID
        filmDao.getFilmById(id)?.let {
            titleEditText.setText(it.filmName)
            releaseDateEditText.setText(it.filmReleaseDate.toString())
            synopsisEditText.setText(it.filmSynopsis)
        }
    }

    private suspend fun updateFilmDetails() {
        val updatedFilm = FilmEntity(
            id = updateId,
            filmName = titleEditText.text.toString(),
            filmReleaseDate = releaseDateEditText.text.toString().toInt(),
            filmSynopsis = synopsisEditText.text.toString()
        )

        // Update data in the Room database
        filmDao.updateFilm(updatedFilm)

        // TODO: Implement code to update Firestore document here
        // You can use FirebaseFirestore.getInstance().collection("your_collection").document("your_document_id")
        // and update the fields accordingly
    }

    private fun populateUI() {
        // Populate UI fields based on the retrieved film details
        // You've already done this in retrieveFilmDetails function
    }
}
