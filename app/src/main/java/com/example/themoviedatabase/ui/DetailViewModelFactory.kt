package com.example.themoviedatabase.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.themoviedatabase.data.repository.movierating.MovieRatingRepository
import com.example.themoviedatabase.data.repository.movierating.MovieRepository

//Factory to instantiate viewModel passing movieRepository and movieRatingRepository
@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val movieRepository: MovieRepository,
    private val movieRatingRepository: MovieRatingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {

            return DetailViewModel(movieRepository, movieRatingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}