package com.example.playlistmaker.api

import com.example.playlistmaker.data.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search?entity=song")
    fun search(@Query("term") term: String): Call<ITunesResponseSongs>
}

class ITunesResponseSongs(
    val resultCount: Int,
    val results: List<Track>
)

object ITunesApiConfig {
    const val url = "https://itunes.apple.com"
}