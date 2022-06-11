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
import com.michlindev.moviedownloader.imdb.Imdb
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object MovieListRepo {

    private suspend fun getPage(page: Int): List<Movie> {
        return getPage(page, SharedPreferenceHelper.minRating, "")
    }


    private suspend fun getPage(page: Int, minRating: Int, query: String): List<Movie> = suspendCoroutine { cont ->

        val mDisposable = CompositeDisposable()
        val apiService = ApiClient.getInstance().create(ApiService::class.java)

        mDisposable.add(
            apiService.getWithParameters(minRating, Constants.PAGE_LIMIT, page, query).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<MoviesResponse?>() {
                    override fun onSuccess(movies: MoviesResponse) {
                        val res = movies.data.movies
                        val retList = mutableListOf<Movie>()
                        res?.forEach {
                            it?.let {
                                retList.add(it)
                            }
                        }
                        DLog.d("Resuming page $page")
                        cont.resume(retList)
                    }

                    override fun onError(e: Throwable) {
                        DLog.d("$e")
                        cont.resumeWithException(e)
                    }
                })
        )
    }

    suspend fun getMoviesAsync3(progress: MutableLiveData<Int>) = coroutineScope {
        val moviesListPage = mutableListOf<Movie>()
        var prg = 1
        (1..25).map {
            async(Dispatchers.IO) {
                DLog.d("Start: $it")
                moviesListPage.addAll(getPage(it))
                progress.postValue(prg++)
                DLog.d("End: $it")
            }
        }.awaitAll()

        moviesListPage
    }


    suspend fun getMoviesAsync2() = coroutineScope {
        val moviesListPage = mutableListOf<Movie>()
        awaitAll(
            async { moviesListPage.addAll(getPage(0)) },
            async { moviesListPage.addAll(getPage(1)) },
            async { moviesListPage.addAll(getPage(2)) },
            async { moviesListPage.addAll(getPage(3)) },
            async { moviesListPage.addAll(getPage(4)) },
            async { moviesListPage.addAll(getPage(5)) })

        moviesListPage
    }

    /*fun main() {
        runBlocking {
            println(getMoviesAsync1()) // [false, true]
        }
    }*/


    //TODO something wrong
    suspend fun getMoviesAsync(movieName: MutableLiveData<String>?, progress: MutableLiveData<Int>?, scope: CoroutineScope): MutableList<Movie> =
        suspendCancellableCoroutine { cont ->

            val mutex = Mutex()
            val movies = mutableListOf<Movie>()
            val searchMode = movieName != null
            val numberOfPages = if (movieName == null) SharedPreferenceHelper.pagesNumber else SEARCH_PAGES

            var cnt = 0
            var resumed = false
            for (i in 1..numberOfPages) {
                scope.launch(Dispatchers.IO) {

                    val moviesListPage = mutableListOf<Movie>()
                    try {
                        if (searchMode)
                            moviesListPage.addAll(getPage(i, 0, movieName?.value!!))
                        else
                            moviesListPage.addAll(getPage(i))
                    } catch (e: Exception) {
                        DLog.e("Caught $e")
                    }

                    withContext(Dispatchers.Default) {
                        mutex.withLock {
                            movies.addAll(moviesListPage)
                        }
                    }

                    cnt++
                    DLog.d("Cnt: $cnt")

                    withContext(Dispatchers.Main) {
                        progress?.postValue(cnt)
                    }

                    if (cnt == numberOfPages) {
                        DLog.d("Resuming $resumed")
                        if (cont.isActive && !resumed) {
                            resumed = true
                            cont.resume(movies)
                            delay(500)
                        }
                        cont.cancel()
                    }
                }

            }
        }

    suspend fun getMoviesAsync(progress: MutableLiveData<Int>?, scope: CoroutineScope): MutableList<Movie> {
        return getMoviesAsync(null, progress, scope)
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
}