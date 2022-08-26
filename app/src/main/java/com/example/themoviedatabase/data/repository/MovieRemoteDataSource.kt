package com.example.themoviedatabase.data.repository


import com.example.themoviedatabase.BuildConfig
import com.example.themoviedatabase.data.network.MovieDbClient
import com.example.themoviedatabase.data.network.results.PopularMoviesResult
import com.example.themoviedatabase.data.network.results.moviedetails.MovieDetailsResult
import retrofit2.Response

class MovieRemoteDataSource {

    suspend fun getPopularMovies(): RepositoryResult<PopularMoviesResult> {

        //Use try and catch to handle exceptions
        try {
            //Call MovieDBClient with retrofit service, call getPopularMovies passing apiKey
            val response: Response<PopularMoviesResult> =
                MovieDbClient.retrofitService.getPopularMovies(BuildConfig.API_KEY)

            //Response body on popularMovieResult
            val popularMoviesResult = response.body()

            //Check response is successful AND response.body() not null
            return if (response.isSuccessful && popularMoviesResult != null) {
                RepositoryResult(
                    data = popularMoviesResult,
                    error = null,
                    source = Source.REMOTE
                )
            } else RepositoryResult(
                data = null,
                error = "Request to server was rejected",
                source = Source.REMOTE
            )

        } catch (e: Exception) {
            return RepositoryResult(
                data = null,
                error = e.message ?: "An Error has occurred",
                source = Source.REMOTE
            )
        }
    }

    suspend fun getMovieDetails(movieId: Int): RepositoryResult<MovieDetailsResult> {

        //Use try and catch to handle exceptions
        try {
            //Call MovieDBClient with retrofit service, call getMovieDetails passing movieId and apiKey
            val response: Response<MovieDetailsResult> =
                MovieDbClient.retrofitService.getMovieDetails(movieId, BuildConfig.API_KEY)

            //Response body on movieDetails
            val movieDetails = response.body()

            //Check response is successful AND response.body() not null
            return if (response.isSuccessful && movieDetails != null) {
                RepositoryResult(
                    data = movieDetails,
                    error = null,
                    source = Source.REMOTE
                )
            } else {
                RepositoryResult(
                    data = null,
                    error = "Request to server was rejected",
                    source = Source.REMOTE
                )
            }
        } catch (e: Exception) {
            return RepositoryResult(
                data = null,
                error = e.message ?: "An Error has occurred",
                source = Source.REMOTE
            )
        }
    }
}





