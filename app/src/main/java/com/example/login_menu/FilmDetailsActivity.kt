package com.example.login_menu

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

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
    }
}
