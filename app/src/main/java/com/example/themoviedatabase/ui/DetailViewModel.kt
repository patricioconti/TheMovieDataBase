package com.example.themoviedatabase.ui

import android.provider.Settings.Global.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.themoviedatabase.R
import com.example.themoviedatabase.data.remote.requests.MovieRatingBodyRequest
import com.example.themoviedatabase.data.remote.results.MovieDetailsResult
import com.example.themoviedatabase.data.remote.results.MovieRatingResponse
import com.example.themoviedatabase.data.repository.movierating.MovieRatingRepository
import com.example.themoviedatabase.data.repository.movierating.MovieRepository
import com.example.themoviedatabase.data.utils.*
import kotlinx.coroutines.launch


class DetailViewModel(
    private val movieRepository: MovieRepository,
    private val movieRatingRepository: MovieRatingRepository
) : ViewModel() {

    // MutableLiveData which stores movieDetails from a movie.
    // Use Operation State sealed class as wrapper.
    // With this we can define operation states and observe them at the fragment.
    private val _movieDetails = MutableLiveData<OperationState<MovieDetailsResult>>(None())

    // The external immutable LiveData for movieDetails from a movie
    val movieDetails: LiveData<OperationState<MovieDetailsResult>> = _movieDetails

    // MutableLiveData which stores movieRatingResponse from a putMovieRating.
    // Use Operation State sealed class as wrapper.
    // With this we can define operation states and observe them at the fragment.
    private val _movieRatingResponse = MutableLiveData<OperationState<MovieRatingResponse>>(None())

    // The external immutable LiveData for movieRatingResponse from a putMovieRating.
    val movieRatingResponse: LiveData<OperationState<MovieRatingResponse>> = _movieRatingResponse

    // MutableLiveData which stores movieRatingValue
    // Use Operation State sealed class as wrapper.
    // With this we can define operation states and observe them at the fragment.
    private val _movieRatingValue = MutableLiveData<OperationState<MovieRatingBodyRequest>>(None())
    val movieRatingValue: LiveData<OperationState<MovieRatingBodyRequest>> = _movieRatingValue


    fun getMovieDetails(movieId: Int) {

        //Set status to LOADING
        _movieDetails.value = Loading()

        viewModelScope.launch {
            //Call getMovieDetails from movieRepository
            val result = movieRepository.getMovieDetails(movieId)

            //If data result is not null
            if (result.data != null) {
                //Store movieDetails Data from movieRepository on _movieDetails using Loaded operationState
                _movieDetails.value = Loaded(result.data)
            } else {
                val errorMessage = result.error ?: "An error has occurred"
                //When an error, Set operationState to Error() and pass errorMessage
                _movieDetails.value = Error(error = errorMessage)
            }
        }
    }

    fun postMovieRating(movieId: Int, movieRatingValue: MovieRatingBodyRequest) {

        //Set status to LOADING
        _movieRatingResponse.value = Loading()

        viewModelScope.launch {
            //Call postMovieRating from movieRatingRepository passing movieId and movieRatingValue
            val result = movieRatingRepository.postMovieRating(movieId, movieRatingValue)

            //If data result is not null
            if (result.data != null) {
                //Store movieRatingResponse Data from movieRatingRepository on _movieRatingResponse
                // using Loaded operationState
                _movieRatingResponse.value = Loaded(result.data)
            } else {
                val errorMessage = result.error ?: "An error has occurred"
                //When an error, Set operationState to Error() and pass errorMessage
                _movieRatingResponse.value = Error(error = errorMessage)
            }
        }
    }

    fun getMovieRating(movieId: Int) {

        //Set status to LOADING
        _movieRatingValue.value = Loading()

        //Call getMovieRating from movieRatingRepository passing movieId
        val result = movieRatingRepository.getMovieRating(movieId)

        //If data result is not null
        if (result.data != null) {
            //Store getMovieRating Data from movieRatingRepository on _movieRatingValue
            // using Loaded operationState
            _movieRatingValue.value = Loaded(result.data)
        } else {
            val errorMessage = result.error ?:"An error has occurred"
            //When an error, Set operationState to Error() and pass errorMessage
            _movieRatingValue.value = Error(error = errorMessage)
        }


    }

}

