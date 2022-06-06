package com.michlindev.moviedownloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.michlindev.moviedownloader.main.MovieListRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        var TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result {

        //42479

        //val movies = MovieListRepo.searchMovie("Die Hard")
        withContext(Dispatchers.IO){
            var movies = MovieListRepo.getMoviesAsync(null)
            movies = MovieListRepo.applyFilters(movies)

            val lastMovie = SharedPreferenceHelper.lastMovie
            movies.removeIf { it.id <= lastMovie }

            withContext(Dispatchers.Main) {
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



        }
        return Result.success()

        //return Result.failure()
    }
}