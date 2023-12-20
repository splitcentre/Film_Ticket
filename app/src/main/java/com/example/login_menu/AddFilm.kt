package com.example.login_menu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_menu.database.FilmEntity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class AddFilm : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageView: ImageView
    private lateinit var imageStream: InputStream
    private val PICK_IMAGE_REQUEST = 1

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_film)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().reference.child("film_images")

        imageView = findViewById(R.id.image_view)

        val btnSubmitFilm = findViewById<Button>(R.id.submitfilm)
        val btnSelectImage = findViewById<Button>(R.id.selectimage)

        btnSubmitFilm.setOnClickListener {
            // Check if an image is selected before submitting
            if (selectedImageUri != null) {
                // Upload the image to Firebase Storage and save film details to Firestore Database
                addFilmToDatabase(selectedImageUri!!)
            } else {
                showToast("Please select an image.")
            }
        }

        btnSelectImage.setOnClickListener {
            // Open the image picker
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Store the selected image URI
            selectedImageUri = data.data

            // Display the selected image
            imageView.setImageURI(selectedImageUri)
        }
    }

    private fun addFilmToDatabase(imageUri: Uri) {
        // Retrieve film details from the form
        val filmTitle = findViewById<EditText>(R.id.Title_input).text.toString()
        val releaseDate = findViewById<EditText>(R.id.input_genre).text.toString().toInt()
        val synopsis = findViewById<EditText>(R.id.synopsis).text.toString()

        // Create a unique key for the film
        val filmId = firestore.collection("films").document().id

        // Upload the image to Firebase Storage
        val imageRef = storageReference.child("$filmId.jpg")

        try {
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri)
            if (imageStream != null) {
                val byteArray = getByteArrayFromInputStream(imageStream)
                imageRef.putBytes(byteArray)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image uploaded successfully
                        // Get the download URL for the image
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Save film details and image URL to Firestore Database
                            val filmEntity = FilmEntity(filmId, uri.toString(), filmTitle, releaseDate, synopsis)
                            firestore.collection("films").document(filmId)
                                .set(filmEntity)
                                .addOnSuccessListener {
                                    // Display a success message
                                    showToast("Film Submitted!")
                                    // Finish the activity or perform other actions
                                    finish()
                                }
                                .addOnFailureListener { exception ->
                                    // Handle the error
                                    showToast("Error adding film to Firestore: ${exception.message}")
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle the error
                        showToast("Error uploading image: ${exception.message}")
                    }
            } else {
                showToast("Error getting image stream.")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getByteArrayFromInputStream(inputStream: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()
        var n: Int
        val data = ByteArray(16384)
        while (inputStream.read(data, 0, data.size).also { n = it } != -1) {
            buffer.write(data, 0, n)
        }
        buffer.flush()
        return buffer.toByteArray()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

