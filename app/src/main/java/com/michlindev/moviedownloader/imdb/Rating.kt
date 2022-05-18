package com.michlindev.moviedownloader.imdb

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.michlindev.moviedownloader.data.Torrents

data class Rating (
    @SerializedName("ratingCount") var ratingCount: Int,
    //@SerializedName("bestRating") var bestRating: Int,
    //@SerializedName("worstRating") var worstRating: Int,
    @SerializedName("ratingValue") var ratingValue: Float


    )