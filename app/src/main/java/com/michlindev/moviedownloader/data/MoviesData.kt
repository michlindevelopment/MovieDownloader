package com.michlindev.moviedownloader.data

import com.google.gson.annotations.SerializedName

data class MoviesData(@SerializedName("movies") var movies: List<Movie>?)