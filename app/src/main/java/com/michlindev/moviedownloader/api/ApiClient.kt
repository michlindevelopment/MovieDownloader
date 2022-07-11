package com.michlindev.moviedownloader.api

import com.michlindev.moviedownloader.data.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "${Constants.YTX_DOMAIN}${Constants.API}"
    val getClient: ApiService
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
}