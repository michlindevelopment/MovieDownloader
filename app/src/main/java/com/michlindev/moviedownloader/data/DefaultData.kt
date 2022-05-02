package com.michlindev.moviedownloader.data

object DefaultData {
    const val MIN_YEAR = 2019
    const val MIN_RATING = 6
    const val PAGES = 25
    const val PAGE_LIMIT = 50
    const val DOMAIN = "https://yts.mx/api"
    const val API = "/v2/"
    const val JSON_FILE = "list_movies.json"

    enum class MovieGenre(private val friendlyName: String) {
        ALL("All"),
        ACTION("Action"),
        ADVENTURE("Adventure"),
        ANIMATION("Animation"),
        BIOGRAPHY("Biography"),
        COMEDY("Comedy"),
        CRIME("Crime"),
        DOCUMENTARY("Documentary"),
        DRAMA("Drama"),
        FAMILY("Family"),
        FANTASY("Fantasy"),
        HISTORY("History"),
        HORROR("Horror"),
        MUSIC("Music"),
        MUSICAL("Musical"),
        MYSTERY("Mystery"),
        ROMANCE("Romance"),
        SCI_FI("Sci-Fi"),
        SHORT_FILM("Short Film"),
        SPORT("Sport"),
        SUPERHERO("Superhero"),
        THRILLER("Thriller"),
        WAR("War"),
        WESTERN("Western");

        override fun toString(): String {
            return friendlyName
        }
    }

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

    enum class SearchType {
        SPECIFIC, ALL
    }
}