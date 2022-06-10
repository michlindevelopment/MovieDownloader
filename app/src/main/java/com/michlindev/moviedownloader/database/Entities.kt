package com.michlindev.moviedownloader.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TorrentEntity(
    @PrimaryKey var id: Long,
    var movieName: String,
    var torrentUrl: String)




