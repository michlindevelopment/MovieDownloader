package com.michlindev.moviedownloader.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TorrentEntity(
    var ytsId: Long,
    var movieName: String,
    var torrentUrl: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

}




