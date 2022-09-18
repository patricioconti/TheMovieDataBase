package com.example.themoviedatabase.data.remote.results

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


//Result from GET movie/{movie_id}. It includes movie details
//MovieDetailsResult class also represents an entity for movie_details local database
@Entity(tableName = "movie_details")
data class MovieDetailsResult(
    @ColumnInfo(name = "adult") @Json(name = "adult") val adult: Boolean,
    @ColumnInfo(name = "backdrop_path") @Json(name = "backdrop_path") val backdropPath: String,
    @ColumnInfo(name = "budget") @Json(name = "budget") val budget: Int,
    @ColumnInfo(name = "homepage") @Json(name = "homepage") val homepage: String,
    //movieId as PrimaryKey
    @PrimaryKey @ColumnInfo(name = "id") @Json(name = "id") val id: Int,
    @ColumnInfo(name = "original_language") @Json(name = "original_language") val originalLanguage: String,
    @ColumnInfo(name = "original_title") @Json(name = "original_title") val originalTitle: String,
    @ColumnInfo(name = "overview") @Json(name = "overview") val overview: String,
    @ColumnInfo(name = "popularity") @Json(name = "popularity") val popularity: Double,
    @ColumnInfo(name = "poster_path") @Json(name = "poster_path") val posterPath: String,
    @ColumnInfo(name = "release_date") @Json(name = "release_date") val releaseDate: String,
    @ColumnInfo(name = "revenue") @Json(name = "revenue") val revenue: Int,
    @ColumnInfo(name = "runtime") @Json(name = "runtime") val runtime: Int,
    @ColumnInfo(name = "status") @Json(name = "status") val status: String,
    @ColumnInfo(name = "tagline") @Json(name = "tagline") val tagline: String,
    @ColumnInfo(name = "title") @Json(name = "title") val title: String,
    @ColumnInfo(name = "video") @Json(name = "video") val video: Boolean,
    @ColumnInfo(name = "vote_average") @Json(name = "vote_average") val voteAverage: Double,
    @ColumnInfo(name = "vote_count") @Json(name = "vote_count") val voteCount: Int
)