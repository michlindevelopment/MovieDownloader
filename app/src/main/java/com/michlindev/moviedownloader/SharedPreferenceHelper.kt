package com.michlindev.moviedownloader

import android.content.Context
import android.content.SharedPreferences


object SharedPreferenceHelper {
    private const val PAGES_NUMBER = "PAGES_NUMBER"
    private const val APP_SHARED_PREFS = "PAGES_NUMBER"

    private var preferences:SharedPreferences = MovieDownloader.applicationContext().getSharedPreferences(APP_SHARED_PREFS,Context.MODE_PRIVATE)

    var pagesNumber: String?
        get() = preferences.getString(PAGES_NUMBER, "10")
        set(value) = preferences.edit().putString(PAGES_NUMBER, value).apply()

}