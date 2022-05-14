package com.michlindev.moviedownloader

import android.content.Context
import android.content.SharedPreferences


object SharedPreferenceHelper {
    private const val PAGES_NUMBER = "PAGES_NUMBER"

    private var preferences:SharedPreferences = MovieDownloader.applicationContext().getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)

    /*var pagesNumber: Int
        get() = preferences.getInt(PAGES_NUMBER, 0)
        set(value) = preferences.edit().putInt(PAGES_NUMBER, value).apply()*/

    var pagesNumber: String?
        get() = preferences.getString(PAGES_NUMBER, "10")
        set(value) = preferences.edit().putString(PAGES_NUMBER, value).apply()

}