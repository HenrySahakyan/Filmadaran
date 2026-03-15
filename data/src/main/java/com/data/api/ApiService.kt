package com.data.api

import com.domain.model.Show
import com.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("shows")
    suspend fun getShows(@Query("page") page: Int): List<Show>

    @GET("shows/{id}")
    suspend fun getShowDetails(@Path("id") id: Int): Show

    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): List<SearchResponse>
}
