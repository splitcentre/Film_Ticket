package com.example.login_menu.database

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FilmRepository(private val filmDao: FilmDao, private val firestore: FirebaseFirestore) {

    suspend fun insertFilm(film: List<FilmEntity>) {
        withContext(Dispatchers.IO) {
            filmDao.insert(film)
        }
    }

    suspend fun getAllFilms(): List<FilmEntity> {
        return withContext(Dispatchers.IO) {
            filmDao.getAllFilms()
        }
    }

    // Function to fetch data from Firestore and save to local Room database
    suspend fun fetchDataFromFirestoreAndSaveToLocal() {
        withContext(Dispatchers.IO) {
            val firestoreCollection = firestore.collection("films")

            val documents = firestoreCollection.get().await()

            val filmList = mutableListOf<FilmEntity>()
            for (document in documents) {
                val film = document.toObject(FilmEntity::class.java)
                filmList.add(film)
            }
            saveFilmsToLocalDatabase(filmList)
        }
    }

    private suspend fun saveFilmsToLocalDatabase(filmList: List<FilmEntity>) {
        withContext(Dispatchers.IO) {
            filmDao.insert(filmList)
        }
    }
}
