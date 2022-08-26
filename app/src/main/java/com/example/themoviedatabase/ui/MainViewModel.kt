package com.example.themoviedatabase.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.themoviedatabase.data.network.results.MovieDb
import com.example.themoviedatabase.data.repository.MovieRepository
import kotlinx.coroutines.launch

//TheMovieDbApiStatus possible states
enum class TheMovieDbApiStatus { LOADING, ERROR, DONE }


class MainViewModel(private val repository: MovieRepository) : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<TheMovieDbApiStatus>()
    // The external immutable LiveData for the request status
    val status: LiveData<TheMovieDbApiStatus> = _status


    // MutableLiveData which stores a list of movies MovieDb objects
    private val _movies = MutableLiveData<List<MovieDb>>()
    // The external immutable LiveData for movies
    val movies: LiveData<List<MovieDb>> = _movies

    // This value is used to notify splashscreen (app launching) when data is loaded
    var isDataLoading = true

    //Remember to call init after liveData variable initialization
    init {
        //getPopularMovies at viewModel init
        getPopularMovies()
    }

    // This fun getPopularMovies calls the fun from repository
    //  The return value is used to notify splashscreen (app launching) when data is loaded
    private fun getPopularMovies() {

        //Call repository fun using Coroutines
        viewModelScope.launch {
            //Set status to LOADING
            _status.value = TheMovieDbApiStatus.LOADING

            //Call getPopularMovies from repository
            val result = repository.getPopularMovies()

            //If data result is not null
            if (result.data != null) {
                //Store movieList Data from repository on _movies
                _movies.value = result.data.results
                //Success
                //Set status to DONE
                _status.value = TheMovieDbApiStatus.DONE
            } else {
                val errorMessage = result.error ?: "An error has occurred"
                //When an error, Set status to ERROR
                _status.value = TheMovieDbApiStatus.ERROR
                //If there is an error _movies is an empty list
                _movies.value = listOf()
            }
            // This value is used to notify splashscreen (app launching) when data is loaded
            isDataLoading = false
        }
    }
    }






