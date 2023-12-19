package com.example.login_menu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.login_menu.database.FilmEntity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.InputStream

class AddFilm : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_film)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("films")

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().reference.child("film_images")

        val btnSubmitFilm = findViewById<Button>(R.id.submitfilm)

        btnSubmitFilm.setOnClickListener {
            // Obtain an InputStream from the user's selected image
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)

            // Now you can use imageStream in your addFilmToDatabase function
            // Make sure to handle null or errors appropriately
            if (imageStream != null) {
                addFilmToDatabase(imageStream)
            } else {
                // Handle the case where the InputStream is null
            }
        }
    }

    private fun addFilmToDatabase(userImageInputStream: InputStream) {
        // Retrieve data from UI components
        val titleEditText: EditText = findViewById(R.id.Title_input)
        val releaseDateEditText: EditText = findViewById(R.id.input_genre)
        val synopsisEditText: EditText = findViewById(R.id.synopsis)

        val title = titleEditText.text.toString()
        val releaseDate = releaseDateEditText.text.toString().toIntOrNull() ?: 0
        val synopsis = synopsisEditText.text.toString()

        // Validate input
        if (title.isEmpty() || releaseDate == 0 || synopsis.isEmpty()) {
            // Show an error message or toast if any field is empty or if releaseDate is not a valid integer
            return
        }

        // Generate a unique filename for the image
        val imageName = "film_image_${System.currentTimeMillis()}.jpg"

        // Create a reference to the image in Firebase Storage
        val imageRef: StorageReference = storageReference.child(imageName)

        // Upload the image to Firebase Storage
        val uploadTask: UploadTask = imageRef.putStream(userImageInputStream)

        // Handle the success or failure of the image upload
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image upload successful, get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Replace the placeholder values with actual data, including the image URL
                    val film = FilmEntity(
                        filmImage = uri.toString(),
                        filmName = title,
                        filmReleaseDate = releaseDate,
                        filmSynopsis = synopsis
                    )

                    // Check if the film with the same name already exists
                    val query: Query = database.orderByChild("filmName").equalTo(title)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                // Film with the same name already exists, update the data
                                for (filmSnapshot in snapshot.children) {
                                    val filmId: String? = filmSnapshot.key
                                    filmId?.let {
                                        database.child(it).setValue(film)
                                    }
                                }
                            } else {
                                // Film with the same name doesn't exist, add new data
                                val filmId: String? = database.push().key
                                filmId?.let {
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
            } else {
                // Image upload failed, handle the error
                // You can show a toast or log an error message
            }
        }
    }
}
