package com.example.themoviedatabase.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.themoviedatabase.data.repository.movierating.MovieRepository

//Factory to instantiate viewModel passing repository as a parameter
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
