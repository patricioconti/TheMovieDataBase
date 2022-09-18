package com.example.themoviedatabase.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.themoviedatabase.data.remote.results.MovieDetailsResult

//Database annotation, MovieDetailsResult is the only class for entity list, version, do not backup old schemas
@Database(entities = [MovieDetailsResult::class], version = 1, exportSchema = false)
abstract class MovieDetailsRoomDatabase : RoomDatabase() {

    abstract fun movieDetailsDao(): MovieDetailsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MovieDetailsRoomDatabase? = null
        fun getDatabase(context: Context): MovieDetailsRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDetailsRoomDatabase::class.java,
                    "movie_details_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                return instance
            }
        }
    }
}
