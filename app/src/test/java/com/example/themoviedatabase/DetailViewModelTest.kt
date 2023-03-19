package com.example.themoviedatabase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.themoviedatabase.data.remote.results.MovieDetailsResult
import com.example.themoviedatabase.data.repository.movierating.MovieRatingRepository
import com.example.themoviedatabase.data.repository.movie.MovieRepository
import com.example.themoviedatabase.data.repository.RepositoryResult
import com.example.themoviedatabase.data.repository.Source
import com.example.themoviedatabase.data.utils.Loaded
import com.example.themoviedatabase.ui.DetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.*
import org.mockito.kotlin.whenever


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {
    //A JUnit Test Rule that swaps the background executor used by the Architecture Components
    // with a different one which executes each task synchronously.
    //To specify that LiveData objects should not call the main thread
    // we need to provide a specific test rule any time we are testing a LiveData object
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    //Execute coroutines at the main thread through runBolocking
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    //Mock properties
    @Mock
    private lateinit var movieRepository: MovieRepository
    @Mock
    private lateinit var movieRatingRepository: MovieRatingRepository

    private lateinit var viewModel:DetailViewModel

    //Run this fun before every test.
    //DetailViewModel is the SUT
    @Before
    fun setup() {
          viewModel = DetailViewModel(movieRepository,movieRatingRepository)
    }

    //get movie Details fun from viewModel returns Current Details
    @Test
    fun getMovieDetails_returnsCurrentDetails() = runBlocking {
        //GIVEN

        //mockMovieDetails
        val mockMovieDetails =
            MovieDetailsResult(
                false,"path",1,"homepage",1,"language",
                "title","overview",6.5,"path","release",
                1,1,"status","tagline","title",false,
                8.5,1
            )

        //Repository getMovieDetails(movieId) returns RepositoryResult class with the data.
        //mockRepositoryResultMovieDetails
        val mockRepositoryResultMovieDetails =
            RepositoryResult(data = mockMovieDetails,error = null, source = Source.REMOTE )

        //mockMovieId
        val mockMovieId = 1234

        //Set movieRepository to always return mockRepositoryResultMovieDetails
        whenever(movieRepository.getMovieDetails(mockMovieId)).thenReturn(mockRepositoryResultMovieDetails)

        //WHEN
        //Call viewModel getMovieDetails(movieId)
        viewModel.getMovieDetails(mockMovieId)

        //THEN
        //Now get LiveData movieDetails.value using LiveDataTestUtil
        val movieDetails = LiveDataTestUtil.getValue(viewModel.movieDetails)

        //Check movieDetails LiveData equals Loaded(mockRepositoryResultMovieDetails.data)
        //I had to override equals at Loaded class to compare it with movieDetails
        assertTrue("movieDetails not equal to Loaded(mockRepositoryResultMovieDetails.data)",
            movieDetails == Loaded(mockRepositoryResultMovieDetails.data))
    }
}