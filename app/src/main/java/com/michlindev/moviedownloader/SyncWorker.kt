package com.michlindev.moviedownloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.main.MovieListRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        var TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result {

        //42479

        //val movies = MovieListRepo.searchMovie("Die Hard")
        val movies = MovieListRepo.getMoviesAsync(null)

        val lastMovie = SharedPreferenceHelper.lastMovie
        movies.removeIf { it.id <= lastMovie }

        withContext(Dispatchers.Main) {
            //val newMovies = mutableListOf<Movie>()
            //newMovies.add(movies[1])
            //newMovies.add(movies[2])

            if (movies.size == 1) {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(movies[0].large_cover_image)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            Notification.showNotification(movies, resource, applicationContext)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            } else {
                Notification.showNotification(movies, null, applicationContext)
            }
        }


        return Result.success()
        //return Result.failure()
    }
}