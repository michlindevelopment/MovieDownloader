package com.michlindev.moviedownloader.imdb

import com.google.gson.annotations.SerializedName

data class Imdb(
    @SerializedName("name") var movieName: String,
    @SerializedName("url") var url: String,
    @SerializedName("aggregateRating") var aggregateRating: Rating,
    @SerializedName("image") var image: String,
    @SerializedName("description") var description: String,
    @SerializedName("trailer") var trailer: Trailer,
    @SerializedName("actor") private var actors: List<Person>?,
    @SerializedName("director") private var director: List<Person>?
) {
    val directorString: String
        get() {
            val pop = mutableListOf<String>()
            director?.forEach {
                pop.add(it.name)
            }
            return pop.joinToString(separator = ", ")
        }

    val actorsString: String
        get() {
            val pop = mutableListOf<String>()
            actors?.forEach {
                pop.add(it.name)
            }
            return pop.joinToString(separator = ", ")
        }
}


data class Person(
    @SerializedName("url") var url: String,
    @SerializedName("name") var name: String
)

data class Rating(
    @SerializedName("ratingCount") var ratingCount: Int,
    @SerializedName("ratingValue") var ratingValue: Float
)


data class Trailer(
    @SerializedName("embedUrl") var url: String,
    @SerializedName("thumbnailUrl") var thumbnailUrl: String,
    @SerializedName("description") var description: String
)