package com.michlindev.moviedownloader

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate


class MovieDownloader : Application() {


    init {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        instance = this

    }

    companion object {
        private lateinit var instance: MovieDownloader

        val appContext: Context
        get() = instance.applicationContext

        /*fun applicationContext() : Context {
            return instance.applicationContext
        }*/
    }

}