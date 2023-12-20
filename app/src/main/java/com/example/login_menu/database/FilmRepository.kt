package com.example.login_menu.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FilmRepository(
    private val filmDao: FilmDao,
    private val firestore: FirebaseFirestore
) {

    companion object {
        // Singleton instantiation
        @Volatile
        private var instance: FilmRepository? = null

        fun getInstance(filmDao: FilmDao, firestore: FirebaseFirestore): FilmRepository {
            return instance ?: synchronized(this) {
                instance ?: FilmRepository(filmDao, firestore).also { instance = it }
            }
        }
    }

    suspend fun addFilm(film: FilmEntity) {
        // Save data to Room
        filmDao.insertFilm(film)

        // Save data to Firestore
        firestore.collection("films")
            .document(film.id)
            .set(film)
            .addOnSuccessListener { Log.d("FilmRepository", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("FilmRepository", "Error writing document", e) }
    }


    suspend fun insertFilm(film: List<FilmEntity>) {
        withContext(Dispatchers.IO) {
            filmDao.insertFilm(film)
        }
    }

    suspend fun getAllFilms(): List<FilmEntity> {
        return withContext(Dispatchers.IO) {
            filmDao.getAllFilms()
        }
    }

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
            filmDao.insertFilm(filmList)
        }
    }

    suspend fun getFilmById(filmId: String): FilmEntity? {
        return withContext(Dispatchers.IO) {
            filmDao.getFilmById(filmId)
        }
    }

    suspend fun updateFilm(film: FilmEntity) {
        withContext(Dispatchers.IO) {
            filmDao.updateFilm(film)
            // Update data in Firestore as well
            firestore.collection("films")
                .document(film.id)
                .set(film)
                .addOnSuccessListener { Log.d("FilmRepository", "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w("FilmRepository", "Error updating document", e) }
        }
    }

    suspend fun deleteFilm(film: FilmEntity) {
        withContext(Dispatchers.IO) {
            filmDao.deleteFilm(film)
            // Delete data from Firestore as well
            firestore.collection("films")
                .document(film.id)
                .delete()
                .addOnSuccessListener { Log.d("FilmRepository", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("FilmRepository", "Error deleting document", e) }
        }
    }
}
