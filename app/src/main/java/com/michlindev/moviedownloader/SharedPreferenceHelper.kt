package com.michlindev.moviedownloader

import android.content.Context
import android.content.SharedPreferences


object SharedPreferenceHelper {
    private const val PAGES_NUMBER = "PAGES_NUMBER"
    private const val MIN_RATING = "MIN_RATING"
    private const val MIN_YEAR= "MIN_YEAR"
    private const val ENGLISH_ONLY= "ENGLISH_ONLY"
    private const val APP_SHARED_PREFS = "APP_SHARED_PREFS"

    private var preferences:SharedPreferences = MovieDownloader.applicationContext().getSharedPreferences(APP_SHARED_PREFS,Context.MODE_PRIVATE)

    var pagesNumber: Int
        get() = preferences.getInt(PAGES_NUMBER, 10)
        set(value) = preferences.edit().putInt(PAGES_NUMBER, value).apply()

    var minRating: Int
        get() = preferences.getInt(MIN_RATING, 0)
        set(value) = preferences.edit().putInt(MIN_RATING, value).apply()

    var minYear: Int
        get() = preferences.getInt(MIN_YEAR, 2000)
        set(value) = preferences.edit().putInt(MIN_YEAR, value).apply()

    var englishOnly: Boolean
        get() = preferences.getBoolean(ENGLISH_ONLY, false)
        set(value) = preferences.edit().putBoolean(ENGLISH_ONLY, value).apply()

}