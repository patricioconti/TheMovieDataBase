package com.example.themoviedatabase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.themoviedatabase.data.remote.results.MovieDetailsResult
import com.example.themoviedatabase.data.repository.*
import com.example.themoviedatabase.data.repository.movie.MovieLocalDataSource
import com.example.themoviedatabase.data.repository.movie.MovieRemoteDataSource
import com.example.themoviedatabase.data.repository.movie.MovieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryTest {

    //A JUnit Test Rule that swaps the background executor used by the Architecture Components
    // with a different one which executes each task synchronously.
    //To specify that LiveData objects should not call the main thread
    // we need to provide a specific test rule any time we are testing a LiveData object
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    //Execute coroutines at the main thread through runBlocking
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    //Mock properties
    @Mock
    private lateinit var remoteDataSource: MovieRemoteDataSource

    @Mock
    private lateinit var localDataSource: MovieLocalDataSource

    private lateinit var repository: MovieRepository

    //Run this fun before every test.
    //MovieRepository is the SUT
    @Before
    fun setup() {
        repository = MovieRepository(remoteDataSource, localDataSource)
    }

    // Case getMovieDetails from LocalDataSource data OK
    @Test
    fun getMovieDetails_fromLocalDataSource() = runBlocking {
        //GIVEN

        //mockMovieDetails
        val mockMovieDetails =
            MovieDetailsResult(
                false, "path", 1, "homepage", 1, "language",
                "title", "overview", 6.5, "path", "release",
                1, 1, "status", "tagline", "title", false,
                8.5, 1
            )


        //mockRepositoryResultMovieDetails with data
        val mockRepositoryResultMovieDetails =
            RepositoryResult(data = mockMovieDetails, error = null, source = Source.LOCAL)

        //mockMovieId
        val mockMovieId = 1234

        //Set localDataSource.getMovieDetails(mockMovieId) to always return mockRepositoryResultMovieDetails
        whenever(localDataSource.getMovieDetails(mockMovieId)).thenReturn(
            mockRepositoryResultMovieDetails
        )

        //WHEN
        //Call repository getMovieDetails(movieId)
        val repositoryResultMovieDetails = repository.getMovieDetails(mockMovieId)

        //THEN
        //Check repositoryResultMovieDetails equals mockRepositoryResultMovieDetails
        assertTrue(
            "repositoryResultMovieDetails not equal to mockRepositoryResultMovieDetails",
            repositoryResultMovieDetails == mockRepositoryResultMovieDetails
        )

        //Verify Zero Interactions with remoteDataSource, because the data was sucessfully got from
        //localDataSource and there is no need to call remoteDataSource
        verifyZeroInteractions(remoteDataSource)
    }


    // Case getMovieDetails from LocalDataSource data NULL - RemoteDataSource data OK
    @Test
    fun getMovieDetails_fromRemoteDataSource() = runBlocking {
        //GIVEN

        //mockMovieDetails
        val mockMovieDetails =
            MovieDetailsResult(
                false, "path", 1, "homepage", 1, "language",
                "title", "overview", 6.5, "path", "release",
                1, 1, "status", "tagline", "title", false,
                8.5, 1
            )

        //mockRepositoryResultMovieDetails with data
        val mockRepositoryResultMovieDetails =
            RepositoryResult(data = mockMovieDetails, error = null, source = Source.REMOTE)

        //Create mockMovieDetailsNull
        val mockMovieDetailsNull: MovieDetailsResult? = null

        //mockRepositoryResultMovieDetails with data NULL
        val mockRepositoryResultMovieDetailsNull =
            RepositoryResult(
                data = mockMovieDetailsNull,
                error = "An Error has occurred",
                source = Source.LOCAL
            )

        //mockMovieId
        val mockMovieId = 1234

        //Set localDataSource.getMovieDetails to always return mockRepositoryResultMovieDetailsNull
        whenever(localDataSource.getMovieDetails(mockMovieId)).thenReturn(
            mockRepositoryResultMovieDetailsNull
        )

        //Set remoteDataSource.getMovieDetails to always return mockRepositoryResultMovieDetails with data.
        whenever(remoteDataSource.getMovieDetails(mockMovieId)).thenReturn(
            mockRepositoryResultMovieDetails
        )

        //WHEN
        //Call repository getMovieDetails(movieId)
        val repositoryResultMovieDetails = repository.getMovieDetails(mockMovieId)

        //THEN
        //Check repositoryResultMovieDetails equals mockRepositoryResultMovieDetails
        assertTrue(
            "repositoryResultMovieDetails not equal to mockRepositoryResultMovieDetails",
            repositoryResultMovieDetails == mockRepositoryResultMovieDetails
        )

        //Verify interaction with localDataSource.insertMovieDetails after getting data from
        //remoteDataSource
        verify(localDataSource).insertMovieDetails(repositoryResultMovieDetails.data!!)

    }

    // Case  getMovieDetails from LocalDataSource data NULL - RemoteDataSource data NULL
    @Test
    fun getMovieDetails_returnsNull() = runBlocking {
        //GIVEN

        //Create mockMovieDetailsNull
        val mockMovieDetailsNull: MovieDetailsResult? = null

        //mockRepositoryResultMovieDetails with data NULL Source.LOCAL
        val mockRepositoryLocalResultMovieDetailsNull =
            RepositoryResult(
                data = mockMovieDetailsNull,
                error = "An Error has occurred",
                source = Source.LOCAL
            )

        //mockRepositoryResultMovieDetails with data NULL Source.REMOTE
        val mockRepositoryRemoteResultMovieDetailsNull =
            RepositoryResult(
                data = mockMovieDetailsNull,
                error = "An Error has occurred",
                source = Source.REMOTE
            )

        //mockMovieId
        val mockMovieId = 1234

        //Set localDataSource.getMovieDetails to always return mockRepositoryLocalResultMovieDetailsNull
        whenever(localDataSource.getMovieDetails(mockMovieId)).thenReturn(
            mockRepositoryLocalResultMovieDetailsNull
        )

        //Set remoteDataSource.getMovieDetails to always return mockRepositoryRemoteResultMovieDetailsNull
        whenever(remoteDataSource.getMovieDetails(mockMovieId)).thenReturn(
            mockRepositoryRemoteResultMovieDetailsNull
        )


        //WHEN
        //Call repository getMovieDetails(movieId)
        val repositoryResultMovieDetails = repository.getMovieDetails(mockMovieId)

        //THEN
        //Check repositoryResultMovieDetails equals mockRepositoryRemoteResultMovieDetailsNull
        assertTrue(
            "repositoryResultMovieDetails not equal to mockRepositoryRemoteResultMovieDetailsNull",
            repositoryResultMovieDetails == mockRepositoryRemoteResultMovieDetailsNull
        )

        //Verify localDataSource.insertMovieDetails() is never called
        verify(localDataSource, never()).insertMovieDetails(any())
    }

}