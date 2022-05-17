package com.michlindev.moviedownloader.data

import com.google.gson.annotations.SerializedName

data class MoviesResponse(@SerializedName("data") var data: MoviesData)

