package com.example.login_menu.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FilmRepository(private val filmDao: FilmDao) {
    suspend fun insertFilm(film: FilmEntity) {
        withContext(Dispatchers.IO) {
            filmDao.insertFilm(film)
        }
    }

    suspend fun getAllFilms(): List<FilmEntity> {
        return withContext(Dispatchers.IO) {
            filmDao.getAllFilms()
        }
    }
}
