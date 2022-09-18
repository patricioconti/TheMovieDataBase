package com.example.themoviedatabase.data.repository.movierating

import com.example.themoviedatabase.BuildConfig
import com.example.themoviedatabase.data.remote.MovieDbClient
import com.example.themoviedatabase.data.remote.requests.MovieRatingBodyRequest
import com.example.themoviedatabase.data.remote.results.MovieRatingResponse
import com.example.themoviedatabase.data.repository.RepositoryResult
import com.example.themoviedatabase.data.repository.Source
import retrofit2.Response

class MovieRatingRemoteDataSource {

    suspend fun postMovieRating(
        movieId: Int,
        guestSessionId: String,
        movieRatingValue: MovieRatingBodyRequest
    ): RepositoryResult<MovieRatingResponse> {

        //Use try and catch to handle exceptions
        try {
            //Call MovieDBClient with retrofit service, call postMovieRating passing
            // movieId, apiKey, guestSessionId, movieRatingValue
            val response: Response<MovieRatingResponse> =
                MovieDbClient.retrofitService.postMovieRating(
                    movieId,
                    BuildConfig.API_KEY,
                    guestSessionId,
                    movieRatingValue
                )

            //Response body on movieRatingResult
            val movieRatingResult = response.body()

            //Check response is successful AND response.body() not null
            return if (response.isSuccessful && movieRatingResult != null) {
                //Repository result has data
                RepositoryResult(
                    data = movieRatingResult,
                    error = null,
                    source = Source.REMOTE
                )

                //Else Repository result has error, put statusMessage on error
            } else RepositoryResult(
                data = null,
                error = movieRatingResult?.statusMessage?: "Request to server was rejected",
                source = Source.REMOTE
            )

            //Handle exception and repository result has error
        } catch (e: Exception) {
            return RepositoryResult(
                data = null,
                error = e.message ?: "An Error has occurred",
                source = Source.REMOTE
            )
        }
    }
}