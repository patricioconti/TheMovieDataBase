package com.example.themoviedatabase.data.repository.movierating

import android.content.Context
import com.example.themoviedatabase.data.remote.requests.MovieRatingBodyRequest
import com.example.themoviedatabase.data.repository.RepositoryResult
import com.example.themoviedatabase.data.repository.Source

//SharedPreferences file name
private const val SHARED_PREF_FILE_NAME = "movieRatingValues"

class MovieRatingLocalDataSource(context: Context) {

    // getSharedPreferences from context passing sharedPrefFile and using private Mode
    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREF_FILE_NAME,
        Context.MODE_PRIVATE
    )

    //get movie rating value from SharedPreferences and return RepositoryResult with data or error
    fun getMovieRating(movieId: Int): RepositoryResult<MovieRatingBodyRequest> {

        //Use try and catch to handle exceptions
        try {
            //Return Float from sharedPreferences or default value
            //Preference key must be String type and movieId is Int type so use movieId.toString()
            val movieRatingValue = sharedPreferences.getFloat(movieId.toString(), 0F)

            //pass movieRatingValue.toDouble() into movieRatingBodyRequest
            val movieRatingBody = MovieRatingBodyRequest(
                value = movieRatingValue.toDouble()
            )
            return RepositoryResult(
                data = movieRatingBody,
                error = null,
                source = Source.LOCAL
            )
        }
        //Catch exception and return error
        catch (e: Exception) {
            return RepositoryResult(
                data = null,
                error = e.message ?: "An Error has occurred",
                source = Source.LOCAL
            )
        }
    }

    //Save movie rating value into SharedPreferences using movieId as key
    fun saveMovieRating(movieId: Int, movieRatingValue: Double) {
        //Call sharedPreferences editor
        val editor = sharedPreferences.edit()
        //Save movieRatingValue passing key (movieId.toString()) and value (movieRatingValue.toFloat())
        editor.putFloat(movieId.toString(), movieRatingValue.toFloat())
        //Apply changes
        editor.apply()
    }
}