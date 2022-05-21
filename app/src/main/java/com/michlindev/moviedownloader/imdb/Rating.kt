package com.michlindev.moviedownloader.imdb

import com.google.gson.annotations.SerializedName

data class Rating (
    @SerializedName("ratingCount") var ratingCount: Int,
    //@SerializedName("bestRating") var bestRating: Int,
    //@SerializedName("worstRating") var worstRating: Int,
    @SerializedName("ratingValue") var ratingValue: Float


    )