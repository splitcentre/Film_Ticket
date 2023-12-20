package com.example.login_menu.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Database(entities = [FilmEntity::class], version = 5, exportSchema = false)
abstract class FilmDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao

    companion object {
        @Volatile
        private var INSTANCE: FilmDatabase? = null

        fun getInstance(context: Context): FilmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FilmDatabase::class.java,
                    "film_database"
                )
                    .fallbackToDestructiveMigration() // Use this if you don't care about preserving data
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun insertDefaultFilmEntities(context: Context, defaultFilms: List<FilmEntity>) {
            // Save data to Room
            getInstance(context).filmDao().insertFilm(defaultFilms)

            // Save data to Firestore
            FilmRepository.getInstance(getInstance(context).filmDao(), FirebaseFirestore.getInstance())
                .addFilm(defaultFilms)
        }
    }
}
