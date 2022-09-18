package com.example.themoviedatabase.data.repository.movierating

import com.example.themoviedatabase.BuildConfig
import com.example.themoviedatabase.data.remote.MovieDbClient
import com.example.themoviedatabase.data.remote.results.PopularMoviesResult
import com.example.themoviedatabase.data.remote.results.MovieDetailsResult
import com.example.themoviedatabase.data.repository.RepositoryResult
import com.example.themoviedatabase.data.repository.Source
import retrofit2.Response

class MovieRemoteDataSource {

    suspend fun getPopularMovies(pageNumber:Int): RepositoryResult<PopularMoviesResult> {

        //Use try and catch to handle exceptions
        try {
            //Call MovieDBClient with retrofit service, call getPopularMovies passing apiKey and page
            //for pagination
            val response: Response<PopularMoviesResult> =
                MovieDbClient.retrofitService.getPopularMovies(BuildConfig.API_KEY, pageNumber)

            //Response body on popularMovieResult
            val popularMoviesResult = response.body()

            //Check response is successful AND response.body() not null
            return if (response.isSuccessful && popularMoviesResult != null) {
                //Repository result has data
                RepositoryResult(
                    data = popularMoviesResult,
                    error = null,
                    source = Source.REMOTE
                )

                //Else Repository result has error
            } else RepositoryResult(
                data = null,
                error = "Request to server was rejected",
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
                //Repository result has data
                RepositoryResult(
                    data = movieDetails,
                    error = null,
                    source = Source.REMOTE
                )
            } else {
                //Else Repository result has error
                RepositoryResult(
                    data = null,
                    error = "Request to server was rejected",
                    source = Source.REMOTE
                )
            }
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





