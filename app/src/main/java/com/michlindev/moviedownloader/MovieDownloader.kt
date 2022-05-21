package com.michlindev.moviedownloader

import android.app.Application
import android.content.Context


class MovieDownloader : Application() {

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: MovieDownloader

        fun applicationContext() : Context {
            return instance.applicationContext
        }
    }

}