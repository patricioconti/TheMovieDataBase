package com.example.themoviedatabase.data.repository.movie

import com.example.themoviedatabase.data.local.MovieDetailsDao
import com.example.themoviedatabase.data.remote.results.MovieDetailsResult
import com.example.themoviedatabase.data.repository.RepositoryResult
import com.example.themoviedatabase.data.repository.Source


class MovieLocalDataSource(private val movieDetailsDao: MovieDetailsDao) {

    //getMovieDetails from localDataSource passing movieId
    suspend fun getMovieDetails(movieId: Int): RepositoryResult<MovieDetailsResult> {
        try {
            //Call movieDetailsDao, call getMovieDetails passing movieId
            val movieDetails: MovieDetailsResult =
                movieDetailsDao.getMovieDetails(movieId)

            //Repository result has data
            return RepositoryResult(
                data = movieDetails,
                error = null,
                source = Source.LOCAL )
            //Handle exception and repository result has error
        } catch (e: Exception) {
            return RepositoryResult(
                data = null,
                error = e.message ?: "An Error has occurred",
                source = Source.LOCAL
            )
        }
    }
    //insertMovieDetails into localDataSource
    suspend fun insertMovieDetails(movieDetails: MovieDetailsResult) {
    movieDetailsDao.insert(movieDetails)
    }

}