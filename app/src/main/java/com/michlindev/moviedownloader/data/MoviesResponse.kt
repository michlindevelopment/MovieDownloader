package com.michlindev.moviedownloader.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MoviesResponse(@SerializedName("data") var data: MoviesData)

