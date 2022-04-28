package com.michlindev.moviedownloader

import android.app.Application
import android.content.Context


class MovieDownloader : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MovieDownloader? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = applicationContext()
    }
}