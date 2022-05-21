package com.michlindev.moviedownloader.imdb

import com.google.gson.annotations.SerializedName

data class Imdb (
    @SerializedName("aggregateRating") var aggregateRating: Rating,


    )