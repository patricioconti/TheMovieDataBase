package com.example.themoviedatabase

import android.app.Application
import com.example.themoviedatabase.data.local.MovieDetailsRoomDatabase

class MovieApplication : Application() {

 /* Instantiate the database instance by calling getDatabase() on MovieDetailsRoomDatabase passing in the context.
 * Use lazy delegate so the instance database is lazily created
 * when you first need/access the reference (rather than when the app starts)
 */
    val database: MovieDetailsRoomDatabase by lazy {
        MovieDetailsRoomDatabase.getDatabase(this)
    }

}