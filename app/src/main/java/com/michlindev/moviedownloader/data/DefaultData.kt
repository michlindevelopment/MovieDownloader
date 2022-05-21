package com.michlindev.moviedownloader.data

object DefaultData {
    const val MIN_YEAR = 2019
    const val MIN_RATING = 6
    const val PAGES = 25
    const val PAGE_LIMIT = 50
    const val DOMAIN = "https://yts.mx/api"
    const val API = "/v2/"
    const val JSON_FILE = "list_movies.json"


    enum class Sort(private val value: String) {
        TITLE("title"),
        YEAR("year"),
        RATING("rating"),
        DOWNLOADS("download_count"),
        LIKES("like_count"),
        DATE_ADDED("date_added");

        override fun toString(): String {
            return value
        }
    }

}