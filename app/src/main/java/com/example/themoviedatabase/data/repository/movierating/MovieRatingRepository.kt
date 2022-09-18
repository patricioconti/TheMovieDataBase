package com.example.themoviedatabase.data.repository.movierating

import com.example.themoviedatabase.data.remote.requests.MovieRatingBodyRequest
import com.example.themoviedatabase.data.remote.results.MovieRatingResponse
import com.example.themoviedatabase.data.repository.RepositoryResult
import com.example.themoviedatabase.data.repository.guestsessionid.GuestSessionIdRepository


class MovieRatingRepository(
    private val guestSessionIdRepository: GuestSessionIdRepository,
    private val remoteDataSource: MovieRatingRemoteDataSource,
    private val localDataSource: MovieRatingLocalDataSource
) {
    suspend fun postMovieRating(
        movieId: Int,
        movieRatingValue: MovieRatingBodyRequest
    ): RepositoryResult<MovieRatingResponse> {

        //First I need a guest session Id to post a movie rating
        //Call guestSessionIdRepository.getGuestSessionId()
        val guestSessionIdResult = guestSessionIdRepository.getGuestSessionId()
        //Store guestSessionId on a val
        val guestSessionId = guestSessionIdResult.data?.guestSessionId

        //If null return RepositoryResult with error ("Couldn't get guest session Id")
        if (guestSessionId == null)
            return RepositoryResult(
                data = null,
                error = guestSessionIdResult.error ?: "Couldn't get guest session Id",
                source = guestSessionIdResult.source
            )
        else {
            // result is remoteDataSource.postMovieRating passing movieId, guestSessionId and
            //movieRatingValue
            val result =
                this.remoteDataSource.postMovieRating(movieId, guestSessionId, movieRatingValue)

            // If result.data not null and result.data.success
            // result.data.success means postMovieRating at remoteDataSource was successful
            if ((result.data != null) && result.data.success)

            //Save movie rating at localDataSource to have it available locally
            //Pass movie id and movieRatingValue.value
                this.localDataSource.saveMovieRating(movieId, movieRatingValue.value)
            //Then return result
            return result
        }
    }

    fun getMovieRating(movieId: Int): RepositoryResult<MovieRatingBodyRequest> {
        //Get movie Details passing movieId from localDataSource
        return this.localDataSource.getMovieRating(movieId)
    }
}