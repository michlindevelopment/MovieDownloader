package com.michlindev.moviedownloader.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.database.DataBaseHelper
import com.michlindev.moviedownloader.main.MovieListRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


object DialogsBuilder {

    fun clearDBDialog(context: Context,runnable: ()->Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                runnable.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    fun showRssUrl(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Rss file url")
        builder.setMessage(generateRssUrl())

        builder.setPositiveButton("Copy") { _, _ ->
            val clipboard: ClipboardManager? = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("Rss", generateRssUrl())
            clipboard?.setPrimaryClip(clip)
        }
        builder.setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()

    }

    fun createQualityDialog(it: Movie, context: Context,scope: CoroutineScope, runnable: ()->Unit) {
        val qualitiesList = it.let { movie -> MovieListRepo.generateQualities(movie) }
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Quality")
            .setItems(qualitiesList.toTypedArray()) { _: DialogInterface?, which: Int ->

                it.torrents[which].let { selectedTorrent ->
                    SharedPreferenceHelper.uploadRequired = true
                    scope.launch {
                        DataBaseHelper.addTorrents(it.id, it.title, selectedTorrent) }
                    runnable.invoke()
                }
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun generateRssUrl(): String {
        return "https://firebasestorage.googleapis.com/v0/b/moviedownloader-9661e.appspot.com/o/${SharedPreferenceHelper.uid}.rss?alt=media"

    }
}