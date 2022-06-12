package com.michlindev.moviedownloader.data

import com.michlindev.moviedownloader.BuildConfig

object Constants {
    const val MIN_YEAR = 2020
    const val MIN_RATING = 5
    const val PAGES = 25
    const val SEARCH_PAGES = 3
    const val PAGE_LIMIT = 50
    const val YTX_DOMAIN = "https://yts.mx/api"
    const val API = "/v2/"
    const val JSON_FILE = "list_movies.json"
    const val IMDB_URL = "https://www.imdb.com"
    const val IMDB_CODE = "imdbCode"

    private const val WORKER_DEBUG_CONS = false
    val WORKER_DEBUG: Boolean
        get() {
            return if (BuildConfig.DEBUG)
                WORKER_DEBUG_CONS
            else
                false
        }

}