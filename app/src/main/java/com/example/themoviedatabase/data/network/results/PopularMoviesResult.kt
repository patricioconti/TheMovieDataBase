package com.example.themoviedatabase.data.network.results

import com.squareup.moshi.Json

//Result from GET movie/popular. It includes a list of movies.
data class PopularMoviesResult(
    val page: Int,
    val results: List<MovieDb>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)