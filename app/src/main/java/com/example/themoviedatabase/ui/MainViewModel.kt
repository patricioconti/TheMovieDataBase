package com.example.themoviedatabase.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.themoviedatabase.data.remote.results.MovieDb
import com.example.themoviedatabase.data.repository.movierating.MoviePagingSource
import com.example.themoviedatabase.data.repository.movierating.MovieRepository
import kotlinx.coroutines.flow.Flow

private const val ITEMS_PER_PAGE = 20

class MainViewModel(repository: MovieRepository) : ViewModel() {

    //Initialize pagingSource passing repository as parameter.
    private val pagingSource = MoviePagingSource(repository)

    // This value is used to notify splashscreen (app launching) when data is loaded
    var isDataLoading = true

    //Initiate viewModel calling getPagedPopularMovies() to have the list of movies when viewModel
    //is created.
    init {
        getPagedPopularMovies()
        // This value is used to notify splashscreen (app launching) when data is loaded
        isDataLoading = false
    }

    //Use Flow PagingData to getPagedPopularMovies
    fun getPagedPopularMovies(): Flow<PagingData<MovieDb>> {

        //Pager for Paging library
        return Pager(
            //The PagingConfig have two params, the items per page size and the enablePlaceHolders.
            config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
            //Define pagingSourceFactory, which is going to be pagingSource
            pagingSourceFactory = { pagingSource }
            //as flow
        ).flow
            .cachedIn(viewModelScope)
    }
}






