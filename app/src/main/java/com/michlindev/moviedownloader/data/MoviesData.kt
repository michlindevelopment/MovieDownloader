package com.michlindev.moviedownloader.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*
class MoviesData : Serializable {
    @SerializedName("movies")
    var movies: List<Movie>? = null
}*/

data class MoviesData(@SerializedName("movies") var movies: List<Movie>)