package com.michlindev.moviedownloader.main

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.api.ApiClient
import com.michlindev.moviedownloader.api.ApiService
import com.michlindev.moviedownloader.data.Constants
import com.michlindev.moviedownloader.data.Constants.IMDB_URL
import com.michlindev.moviedownloader.data.Constants.SEARCH_PAGES
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.data.MoviesResponse
import com.michlindev.moviedownloader.database.DataBaseHelper
import com.michlindev.moviedownloader.imdb.Imdb
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object MovieListRepo {

    /* private suspend fun getPage(page: Int): List<Movie>? {
         return getPage(page, SharedPreferenceHelper.minRating, "")
     }*/


    private suspend fun getPage(page: Int, query: String?, minRating: Int): List<Movie>? = suspendCoroutine { cont ->

        val mDisposable = CompositeDisposable()
        val apiService = ApiClient.getInstance().create(ApiService::class.java)
        val movieName = query ?: ""

        mDisposable.add(
            apiService.getWithParameters(minRating, Constants.PAGE_LIMIT, page, movieName).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<MoviesResponse?>() {
                    override fun onSuccess(movies: MoviesResponse) {
                        val res = movies.data.movies
                        val retList = mutableListOf<Movie>()
                        res?.forEach {
                            it?.let {
                                retList.add(it)
                            }
                        }
                        cont.resume(retList)
                    }

                    override fun onError(e: Throwable) {
                        DLog.d("$e")
                        //cont.resumeWithException(e)
                        cont.resume(null)
                    }
                })
        )
    }

    suspend fun getMoviesAsync(movieName: String?, progress: MutableLiveData<Int>?, searchMode: Boolean) = coroutineScope {
        val numberOfPages = if (searchMode) SEARCH_PAGES else SharedPreferenceHelper.pagesNumber
        val minRating: Int = if (searchMode) 0 else SharedPreferenceHelper.minRating
        val moviesListPage = mutableListOf<Movie>()


        var prg = 1
        (1..numberOfPages).map {
            async(Dispatchers.IO) {

                val pageResult = getPage(it, movieName, minRating)
                synchronized(moviesListPage) {
                    pageResult?.let { it1 -> moviesListPage.addAll(it1) }
                }

                progress?.postValue(prg++)
            }
        }.awaitAll()

        moviesListPage
    }


    fun applyFilters(movies: MutableList<Movie>): MutableList<Movie> {

        val newMovies = mutableListOf<Movie>()
        val englishOnly = SharedPreferenceHelper.englishOnly
        val genres = SharedPreferenceHelper.genres

        movies.forEach {

            var inLanguage = true
            val minYear: Boolean = it.year >= SharedPreferenceHelper.minYear //TODO year null
            val inContainment: Boolean = !checkContainment(it, genres)
            if (englishOnly) inLanguage = it.language == "en"

            if (minYear && inLanguage && inContainment)
                newMovies.add(it)
        }

        newMovies.sortByDescending { it.date_uploaded_unix }
        return newMovies

    }

    private fun getImdbData(imdbCode: String): Imdb {
        val url = "$IMDB_URL/title/$imdbCode/"
        val document = Jsoup.connect(url).get()
        val e: Element? = document.select("script").first()
        return Gson().fromJson(e?.html(), Imdb::class.java)
    }

    fun getImdbPage(imdbCode: String): Imdb {
        return getImdbData(imdbCode)
    }


    fun getRealRating(imdbCode: String): String {
        val imdb = getImdbData(imdbCode)
        return imdb.aggregateRating.ratingValue.toString()
    }

    fun generateQualities(it: Movie): MutableList<String> {
        val qualitiesList = mutableListOf<String>()
        it.torrents.forEach { torrent ->
            when (torrent.type) {
                "bluray" -> qualitiesList.add("BluRay ${torrent.quality}")
                "web" -> qualitiesList.add("Web ${torrent.quality}")
            }
        }
        return qualitiesList
    }

    private fun checkContainment(movie: Movie, genres: MutableSet<String>?): Boolean {

        if (movie.genres.isEmpty()) return true
        else {
            movie.genres.forEach {
                if (genres?.contains(it) == false) return true
            }
            return false
        }
    }

    suspend fun markDownloaded(movies: MutableList<Movie>): MutableList<Movie> = coroutineScope {
        val downloaded = DataBaseHelper.getAllTorrents()
        movies.forEach { mov ->
            if (downloaded.any { it.id == mov.id })
                mov.dowloaded = true
        }
        (movies)
    }
}