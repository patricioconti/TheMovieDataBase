package com.example.themoviedatabase.data.repository

import com.example.themoviedatabase.data.network.results.PopularMoviesResult
import com.example.themoviedatabase.data.network.results.moviedetails.MovieDetailsResult

class MovieRepository(private val remoteDataSource: MovieRemoteDataSource) {

    suspend fun getPopularMovies(): RepositoryResult<PopularMoviesResult> {
        return this.remoteDataSource.getPopularMovies()
    }

    suspend fun getMovieDetails(movieId:Int) : RepositoryResult<MovieDetailsResult> {
        return this.remoteDataSource.getMovieDetails(movieId)
    }
}