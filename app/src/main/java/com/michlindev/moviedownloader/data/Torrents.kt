package com.michlindev.moviedownloader.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Torrents(
    @SerializedName("url") var url: String,
    @SerializedName("quality") var quality: String,
    @SerializedName("type") var type: String)