package com.example.themoviedatabase.data.repository


//Used to wrap remoteDataSource or localDataSource result
class RepositoryResult<T>(
    val data: T?,
    val error: String?,
    val source:Source)

