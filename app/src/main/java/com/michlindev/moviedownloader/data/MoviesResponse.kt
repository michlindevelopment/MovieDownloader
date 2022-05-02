package com.michlindev.moviedownloader.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*class MoviesResponse : Serializable {
    @SerializedName("data")
    var data: MoviesData? = null
}*/

data class MoviesResponse(@SerializedName("data") var data: MoviesData)

