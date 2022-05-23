package com.michlindev.moviedownloader.database

import com.michlindev.moviedownloader.MovieDownloader
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.data.Torrents
import kotlinx.coroutines.*


object DataBaseHelper {

    var db: AppDatabase = AppDatabase(MovieDownloader.appContext)


    suspend fun getAllTorrents() = withContext(Dispatchers.IO) {
        db.torrentDao().getTorrents()
    }

    suspend fun addTorrents(id: Long, title: String, torrent: Torrents) = withContext(Dispatchers.IO) {
        val torrentEntity = TorrentEntity(id,title,torrent.url)
        db.torrentDao().insert(torrentEntity)
    }

}



