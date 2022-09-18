package com.example.themoviedatabase

import com.example.themoviedatabase.data.remote.TheMovieDbApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TheMovieDbApiServiceTests : BaseTest() {

    private lateinit var service: TheMovieDbApiService

    @Before
    fun setup() {

        //MockWebServer has a function called url() that specifies which URL we want to intercept
        //The url() function takes a string that represents that fake URL and
        // it returns an HttpUrl object.
        val url = mockWebServer.url("/")

        //Create an instance of the TheMovieDbApiService in the same way that was done in the TheMovieDbApiService
        service = Retrofit.Builder()

            //This specifies to our API service that we want to route requests to our MockWebServer
            .baseUrl(url)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()
            .create(TheMovieDbApiService::class.java)

        //the point of MockWebServer is to avoid making a real network request to a real API.
        // The basic idea is that if a real network request was made, then the test would fail
        // if the API failed. Using a real API tests the API itself, and we are only concerned with
        // testing the code in our Android project.
    }

    // MockWebServer as a fake API that returns data that we created, and as such,
    // we need to explicitly tell MockWebServer what to return before a request is made
    @Test
    fun api_service_getPopularMovies() {

        // The function takes a file from test resources and turns it into a fake API response.
        enqueue("get_movie_popular.json")

        //getPopularMovies() is a suspend function, and it must be called from a coroutine scope
        runBlocking {
                   //Call service.getPopularMovies()
            val apiResponse = service.getPopularMovies(BuildConfig.API_KEY,1)
            //Let's make sure that getPopularMovies() response is not null
             TestCase.assertNotNull(apiResponse.body())
            //Make sure that the list is not empty (remember that there is a list of results.
            TestCase.assertTrue("The list was empty", apiResponse.body()!!.results.isNotEmpty())
            //Assert that the value of that ID equals the value of the ID from the corresponding list item
            TestCase.assertEquals(
                "The title for first movie in the list did not match",
                "Fall",
                apiResponse.body()!!.results.first().title
            )
            TestCase.assertEquals(
                "The voteAverage for the last movie in the list did not match",
                6.9,
                apiResponse.body()!!.results.last().voteAverage
            )
        }
    }
}