package com.example.themoviedatabase.data.repository.movierating

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.themoviedatabase.data.remote.results.MovieDb
import com.example.themoviedatabase.data.remote.results.PopularMoviesResult
import com.example.themoviedatabase.data.repository.RepositoryResult

//First page at API is 1
private const val API_STARTING_PAGE_INDEX = 1

//Last page at API is 1000
private const val API_LAST_PAGE_INDEX = 1000


class MoviePagingSource(private val repository: MovieRepository) : PagingSource<Int, MovieDb>() {

    //This function will be called by the Paging library to asynchronously fetch more data
    // to be displayed as the user scrolls around.
    // The LoadParams object keeps information related to the load operation.
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDb> {
        //We are going to use the params.key for get the current page index.
        //If this is the first time that load is called, params.key will be null.
        // In that case, we will have to define the initial page key with the API_STARTING_PAGE_INDEX constant.
        // Finally, params.loadSize is the requested number of items to load.
        val pageIndex = params.key ?: API_STARTING_PAGE_INDEX

        try {

            //Call getPopularMovies from repository
            val result: RepositoryResult<PopularMoviesResult> =
                repository.getPopularMovies(pageIndex)

            //data from repository result on popularMovieResult
            val popularMoviesResult = result.data

            //List of movieDb on movies
            val movies = popularMoviesResult!!.results

            //If movies is empty then next key is null because there is no more data to load
            val nextKey =
                if (movies.isEmpty()) {
                    null
                } else {
                    //next key is pageIndex + 1
                    pageIndex + 1
                }
            //A LoadResult.Page has three required arguments:
            //data: A List of the items fetched.
            //prevKey: The key used by the load() method if it needs to fetch items behind the current page.
            //nextKey: The key used by the load() method if it needs to fetch items after the current page.
            return LoadResult.Page(
                data = movies,
                //if pageIndex is the starting page there is no data to load behind the current page
                //so prevKey is null else is pageIndex
                prevKey = if (pageIndex == API_STARTING_PAGE_INDEX) null else pageIndex,
                //if pageIndex is the last page there is no data to load after the current page
                //so nextKey is null else is pageIndex
                nextKey = if (pageIndex  == API_LAST_PAGE_INDEX) null else nextKey
            )

        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    /**
     * The refresh key is used for subsequent calls to PagingSource.Load after the initial load.
     */
    override fun getRefreshKey(state: PagingState<Int, MovieDb>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}