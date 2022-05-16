package com.michlindev.moviedownloader.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MoviesData(@SerializedName("movies") var movies: List<Movie>)