package com.michlindev.moviedownloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.michlindev.moviedownloader.data.Constants.WORKER_DEBUG
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.main.MovieListRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        var TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result {

        var movies: MutableList<Movie>

        //withContext(Dispatchers.IO) {
        if (WORKER_DEBUG) {
            movies = MovieListRepo.getMoviesAsync("Die Hard", null,true) //TODO constucor
            val movie = movies[1]
            movies.clear()
            movies.add(movie)
        } else {
            movies = MovieListRepo.getMoviesAsync("", null,false)//TODO constucor
            movies = MovieListRepo.applyFilters(movies)
            movies.removeIf { it.id <= SharedPreferenceHelper.lastMovie }

            SharedPreferenceHelper.lastMovie = movies.maxOf { it.id }
        }

        if (movies.isNotEmpty()) {
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


        //}
        return Result.success()
        //return Result.failure()
    }
}