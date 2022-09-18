package com.example.themoviedatabase.data.repository.guestsessionid

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.themoviedatabase.data.remote.results.GuestSessionIdResult
import com.example.themoviedatabase.data.repository.RepositoryResult
import com.example.themoviedatabase.data.repository.Source

//GUEST_SESSION_ID_KEY for SharedPreferences
private const val GUEST_SESSION_ID_KEY = "guestSessionId"
//SharedPreferences file name
private const val SHARED_PREF_FILE_NAME = "myPreferences"

class GuestSessionIdLocalDataSource (context:Context) {

    // getSharedPreferences from context passing sharedPrefFile and using private Mode
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE)

    //get guest session Id from SharedPreferences and return RepositoryResult with data or error
    fun getGuestSessionId() : RepositoryResult<GuestSessionIdResult> {

        //Use try and catch to handle exceptions
        try {
            //Return String from sharedPreferences or default value
            val guestSessionId = sharedPreferences.getString(GUEST_SESSION_ID_KEY,"")

            //if guestSessionId is null or empty return data null
            if (guestSessionId ==null || guestSessionId =="") {

            return  RepositoryResult(
                data = null,
                error = "Couldn't get guest session Id",
                source = Source.LOCAL
                )
            }
            //Else guestSessionId has data, return Repository result with data
            else {
                //pass guestSessionId into GuestSessionIdResult
                val guestSessionIdResult = GuestSessionIdResult(
                    expiresAt = "Never",
                    guestSessionId = guestSessionId,
                    success = true
                )
                return RepositoryResult(
                    data = guestSessionIdResult,
                    error = null,
                    source = Source.LOCAL
                )
            }
     }
        //Catch exception and return error
        catch (e: Exception) {
            return RepositoryResult(
                data = null,
                error = e.message ?: "An Error has occurred",
                source = Source.LOCAL
            )
        }
    }

    //Save guest session Id into SharedPreferences
    fun saveGuestSessionId(guestSessionId:String?) {
        //Call sharedPreferences editor
        val editor = sharedPreferences.edit()
        //Save guestSession Id passing key and value
        editor.putString(GUEST_SESSION_ID_KEY,guestSessionId)
        //Apply changes
        editor.apply()
    }
}