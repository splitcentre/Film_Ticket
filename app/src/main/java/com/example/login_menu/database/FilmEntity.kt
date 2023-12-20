package com.example.login_menu.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "films")
data class FilmEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val filmImage: String,
    val filmName: String,
    val filmReleaseDate: Int,
    val filmSynopsis: String
)


