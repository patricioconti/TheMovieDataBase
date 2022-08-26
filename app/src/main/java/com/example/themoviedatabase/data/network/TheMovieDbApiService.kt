package com.example.themoviedatabase.data.network

import com.example.themoviedatabase.BuildConfig
import com.example.themoviedatabase.data.network.requests.MovieRatingDto
import com.example.themoviedatabase.data.network.results.GuestSessionIdResult
import com.example.themoviedatabase.data.network.results.PopularMoviesResult
import com.example.themoviedatabase.data.network.results.MovieRatingResponse
import com.example.themoviedatabase.data.network.results.moviedetails.MovieDetailsResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

//Create Moshi object
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

//Logging Interceptor for debugging
val httpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    .build()

//Compiler for creating and compiling Retrofit object
//With this, Retrofit can get a JSON response and show it as a string
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    //Base URL for web service
    .baseUrl(BuildConfig.API_URL)
    .client(httpClient)
    .build()


interface TheMovieDbApiService {

    //Indicate retrofit that is a GET request and that the extreme is movie/popular
    @GET("movie/popular")
    //To list popular movies, I also pass query argument name and its value so to obtain
    //https://api.themoviedb.org/3/movie/popular?api_key=4ff5054f4fcb39f0f4fc6b9a578265d6
  suspend fun getPopularMovies(@Query("api_key") apiKey:String): Response<PopularMoviesResult>


    //Indicate retrofit that is a GET request and that the extreme is movie/{movie_id}
    @GET("movie/{movie_id}")
    //To get movie details, I need to pass path for the movie and query api_key.
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
    ): Response<MovieDetailsResult>



    //To rate a movie, first I need to get a guest_session id
    //Indicate retrofit that is a GET request and that the extreme is authentication/guest_session/new
    @GET("authentication/guest_session/new")
    //To get guest session id , I also pass query argument name and its value so to obtain
    //https://api.themoviedb.org/3/authentication/guest_session/new?api_key=4ff5054f4fcb39f0f4fc6b9a578265d6
    suspend fun getGuestSessionId(@Query("api_key") apiKey:String): GuestSessionIdResult


    //To post a movie rating, I need to pass a header, path for the movie, query api_key,
    //query guest_session_id and body with rating value
    @Headers("Content-Type: application/json;charset=utf-8")
    //Indicate retrofit that is a POST request and that the extreme is movie/{movie_id}/rating
    @POST("movie/{movie_id}/rating")
    suspend fun postMovieRating(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("guest_session_id") guestSessionId: String,
        @Body value: MovieRatingDto,
    ) : MovieRatingResponse

}

//Expose the service to the rest of the app using object declaration.
object MovieDbClient {
    val retrofitService: TheMovieDbApiService by lazy {
        retrofit.create(TheMovieDbApiService::class.java)
    }
}