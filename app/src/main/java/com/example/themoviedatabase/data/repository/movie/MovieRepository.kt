package com.example.themoviedatabase.data.repository.movie

import com.example.themoviedatabase.data.remote.results.MovieDetailsResult
import com.example.themoviedatabase.data.remote.results.PopularMoviesResult
import com.example.themoviedatabase.data.repository.RepositoryResult

//Repository has two sources: remoteDataSource API and localDataSource Room.
class MovieRepository(
    private val remoteDataSource: MovieRemoteDataSource,
    private val localDataSource: MovieLocalDataSource
) {

    //getPopularMovies from remoteDataSource
    suspend fun getPopularMovies(pageNumber:Int): RepositoryResult<PopularMoviesResult> {
        return this.remoteDataSource.getPopularMovies(pageNumber)
    }


    //Get movie details from localDataSource or remoteDataSource
    suspend fun getMovieDetails(movieId: Int): RepositoryResult<MovieDetailsResult> {

   //First, try to get movie Details passing movieId from localDataSource
    var result = this.localDataSource.getMovieDetails(movieId)

        //If result is not null return result
        return if (result.data != null)
            result

        //Else, result is null so we get movie Details from remoteDataSource
        else {
            result = this.remoteDataSource.getMovieDetails(movieId)

            //if result is not null insert movie Details into localDataSource to have it available locally
            if(result.data !=null)
                this.localDataSource.insertMovieDetails(result.data!!)
            //Return result
            result
        }
    }
}