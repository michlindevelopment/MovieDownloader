package com.michlindev.moviedownloader.api

import com.michlindev.moviedownloader.data.Constants
import com.michlindev.moviedownloader.data.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(Constants.JSON_FILE)
    suspend fun getWithParameters(
        @Query("minimum_rating") rating: Int,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("query_term") query: String?
    ): Response<MoviesResponse>
}