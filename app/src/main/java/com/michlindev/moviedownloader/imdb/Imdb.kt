package com.michlindev.moviedownloader.imdb

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.michlindev.moviedownloader.data.Torrents

data class Imdb (
    @SerializedName("aggregateRating") var aggregateRating: Rating,


    )