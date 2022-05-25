package com.michlindev.moviedownloader

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.michlindev.moviedownloader.database.TorrentEntity
import java.io.*

object FileManager {
    private const val RSS_HEADER =
        "<rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" xmlns:atom=\"http://www.w3.org/2005/Atom\" version=\"2.0\">"

    private const val ENCLOSURE_PREFIX = "<enclosure url=\""
    private const val ENCLOSURE_SUFFIX = "\" type=\"application/x-bittorrent\" length=\"10000\"/>"

    private const val RSS_TITLE = "<title>Movies RSS</title>"
    private const val CHANNEL_OPEN = "<channel>"
    private const val CHANNEL_CLOSE = "</channel>"
    private const val RSS_HEADER_CLOSE = "</rss>"
    private const val FOLDER_NAME = "MoviesSync"
    private const val FILE_NAME = "local.rss"
    private const val ITEM = "<item>"
    private const val ITEM_CLOSE = "</item>"
    private const val TITLE = "<title>"
    private const val TITLE_CLOSE = "</title>"
    private const val B = "\n"


    private val path = MovieDownloader.appContext.filesDir.path

    fun writeToRssFile(torrents: List<TorrentEntity>) {
        //TODO remove prefix
        checkFile()

        val file = File("$path/$FOLDER_NAME", FILE_NAME)
        file.bufferedWriter().use { out ->

            out.write("$RSS_HEADER$B")
            out.write("$CHANNEL_OPEN$B")
            out.write("$RSS_TITLE$B")

            torrents.forEach {
                out.write("$ITEM$B")
                out.write("$TITLE$B")
                out.write("${it.movieName}$B")
                out.write("$TITLE_CLOSE$B")
                out.write("$ENCLOSURE_PREFIX${it.torrentUrl}$ENCLOSURE_SUFFIX$B")
                out.write("$ITEM_CLOSE$B")
            }
            out.write("$CHANNEL_CLOSE$B")
            out.write("$RSS_HEADER_CLOSE$B")
        }
    }

    private fun checkFile() {
        val directory = File("$path/$FOLDER_NAME")
        val file = File(directory, FILE_NAME)

        if (file.exists())
            return
        else {
            if (!directory.exists()) directory.mkdir()
            if (!file.exists()) file.createNewFile()

        }
    }

    fun uploadFile() {

        val storageReference = FirebaseStorage.getInstance().reference
        val file = Uri.fromFile(File("$path/$FOLDER_NAME", FILE_NAME))

        val mountainsRef = storageReference.child("${SharedPreferenceHelper.uid}.rss")
        val uploadTask = mountainsRef.putFile(file)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            DLog.d("Fail $it")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            DLog.d("Success")

        }.addOnProgressListener {
            DLog.d("Prog")
        }

    }
}