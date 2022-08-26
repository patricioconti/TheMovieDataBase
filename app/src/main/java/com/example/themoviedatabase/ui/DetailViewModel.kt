package com.example.themoviedatabase.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.themoviedatabase.data.network.results.moviedetails.MovieDetailsResult
import com.example.themoviedatabase.data.repository.MovieRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: MovieRepository): ViewModel(){

    // MutableLiveData which stores movieDetails from a movie
    private val _movieDetails = MutableLiveData<MovieDetailsResult>()
    // The external immutable LiveData for movieDetails from a movie
    val movieDetails: LiveData<MovieDetailsResult> = _movieDetails

    // MutableLiveData which stores error
    private val _error = MutableLiveData<String>()
    // The external immutable LiveData for error
    val error: LiveData<String> = _error


   fun getMovieDetails(movieId:Int) {

        viewModelScope.launch{
            //Call getMovieDetails from repository
            val result = repository.getMovieDetails(movieId)

            //If data result is not null
            if (result.data != null) {
                //Store movieDetails Data from repository on _movieDetails
                _movieDetails.value = result.data
                //Success
               } else {
                val errorMessage = result.error ?: "An error has occurred"
                _error.value = errorMessage
                //When an error, Set status to ERROR
            }
           }
    }
}