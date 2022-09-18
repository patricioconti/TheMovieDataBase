package com.example.themoviedatabase.data.repository.guestsessionid

import com.example.themoviedatabase.data.remote.results.GuestSessionIdResult
import com.example.themoviedatabase.data.repository.RepositoryResult

class GuestSessionIdRepository(
    private val remoteDataSource: GuestSessionIdRemoteDataSource,
    private val localDataSource: GuestSessionIdLocalDataSource
) {
    //Get guestSessionId from localDataSource or remoteDataSource
    suspend fun getGuestSessionId(): RepositoryResult<GuestSessionIdResult> {

        //First try to getGuestSessionId from localDataSource
        var result = this.localDataSource.getGuestSessionId()

        //If result data is not null return result (we get the guest session Id from localDataSource)
        return if (result.data != null)
            result
        //Else, result data is null so we get guest session Id from remoteDataSource
        else {
            result = this.remoteDataSource.getGuestSessionId()

            //If result data from remoteDataSource is not null,
            // save GuestSessionId into localDataSource to have it available locally
            if (result.data != null)
                this.localDataSource.saveGuestSessionId(result.data!!.guestSessionId)
            //Return result
            result
        }
    }
}