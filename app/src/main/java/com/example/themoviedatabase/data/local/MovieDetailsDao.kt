package com.example.themoviedatabase.data.local

import androidx.room.*
import com.example.themoviedatabase.data.remote.results.MovieDetailsResult


//Data access object for Room Database.
@Dao
interface MovieDetailsDao {

    //Insert movieDetails
    //The OnConflictStrategy.REPLACE strategy replaces a new item if it's primary key is already in the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movieDetails: MovieDetailsResult)

    //Get movieDetails by movieId
    @Query("SELECT * from movie_details WHERE id = :movieId")
    suspend fun getMovieDetails(movieId: Int): MovieDetailsResult

}