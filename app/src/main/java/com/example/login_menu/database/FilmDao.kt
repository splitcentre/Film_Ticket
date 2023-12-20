package com.example.login_menu.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FilmDao {
    @Insert
    suspend fun insert(film: List<FilmEntity>)

    @Query("SELECT * FROM films")
    suspend fun getAllFilms(): List<FilmEntity>

    @Query("SELECT * FROM films WHERE id = :filmId")
    suspend fun getFilmById(filmId: String): FilmEntity?

    @Update
    suspend fun updateFilm(film: FilmEntity)

    @Delete
    suspend fun deleteFilm(film: FilmEntity)
}
