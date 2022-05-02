package com.michlindev.moviedownloader.data

import com.google.gson.annotations.SerializedName
import com.michlindev.moviedownloader.data.Torrents
import java.io.Serializable
import java.util.ArrayList

data class Movie(
    @SerializedName("url") var url:String,
    @SerializedName("torrents") var torrents: MutableList<Torrents>
) {

fun getPop(): String {
    return url
}



//var isWatched = false

   /* @SerializedName("id")
    var id = 0

    @SerializedName("url")
    var url: String? = null

    @SerializedName("imdb_code")
    var imdb_code: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("title_english")
    var title_english: String? = null

    @SerializedName("year")
    var year = 0

    @SerializedName("rating")
    var rating = 0.0

    @SerializedName("genres")
    lateinit var genres: Array<String>

    @SerializedName("summary")
    var summary: String? = null

    @SerializedName("background_image")
    var background_image: String? = null

    @SerializedName("large_cover_image")
    var large_cover_image: String? = null

    @SerializedName("torrents")
    var torrents: MutableList<Torrents>? = null

    @SerializedName("date_uploaded_unix")
    var date_uploaded_unix: Long = 0*/


   /* @JvmName("getTorrents1")
    fun getTorrents(): List<Torrents>? {
        return torrents
    }

    fun setTorrents(torrents: MutableList<Torrents>?) {
        this.torrents = torrents
    }*/

    /*fun addGenres(genresArr: Array<String>) {
        genres = genresArr
    }

    fun addTorrent(torrent: Torrents) {
        if (torrents == null) torrents = ArrayList()
        torrents!!.add(torrent)
    }*/

    /*override fun equals(o: Any?): Boolean {
        if (o is Movie) {
            return id == o.id
        }
        return false
    }*/
}