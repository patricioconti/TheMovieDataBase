package com.example.themoviedatabase.data.remote.results

import com.squareup.moshi.Json

//Result from GET movie/popular. It includes a list of movies.
data class PopularMoviesResult(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val results: List<MovieDb>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)