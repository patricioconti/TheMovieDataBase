package com.example.themoviedatabase.data.repository.guestsessionid

import com.example.themoviedatabase.BuildConfig
import com.example.themoviedatabase.data.remote.MovieDbClient
import com.example.themoviedatabase.data.remote.results.GuestSessionIdResult
import com.example.themoviedatabase.data.repository.RepositoryResult
import com.example.themoviedatabase.data.repository.Source
import retrofit2.Response

class GuestSessionIdRemoteDataSource {

  suspend fun  getGuestSessionId(): RepositoryResult<GuestSessionIdResult> {

      //Use try and catch to handle exceptions
      try {
          //Call MovieDBClient with retrofit service, call getGuestSessionId passing apiKey

          val response: Response<GuestSessionIdResult> =
              MovieDbClient.retrofitService.getGuestSessionId(BuildConfig.API_KEY)

          //Response body on guestSessionIdResult
          val guestSessionIdResult = response.body()

          //Check response is successful AND response.body() not null AND guestSessionId not null
          return if (response.isSuccessful && (guestSessionIdResult != null) && (guestSessionIdResult.guestSessionId != null)) {
              //Repository result has data
              RepositoryResult(
                  data = guestSessionIdResult,
                  error = null,
                  source = Source.REMOTE
              )

              //Else Repository result has error
          } else RepositoryResult(
              data = null,
              error = "Couldn't get guest session Id",
              source = Source.REMOTE
          )

      //Handle exception and repository result has error
      } catch (e: Exception) {
          return RepositoryResult(
              data = null,
              error = e.message ?: "An Error has occurred",
              source = Source.REMOTE
          )
      }
  }
}