package com.example.login_menu.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "films")
data class FilmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filmImage: String,
    val filmName: String,
    val filmReleaseDate: Int,
    val filmSynopsis: String
)

