package com.example.themoviedatabase

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source

open class BaseTest {

    //This object will intercept our network requests,
    val mockWebServer = MockWebServer()

// The enqueue() function in this class parses the data from the get_movie_popular.json (local file).
    fun enqueue(fileName: String) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        val buffer = inputStream.source().buffer()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(buffer.readString(Charsets.UTF_8))
        )
    }
}