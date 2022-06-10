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
                        cont.resume(retList)
                    }

                    override fun onError(e: Throwable) {
                        DLog.d("$e")
                    }
                })
        )
    }

    //Todo maybe change to this type
    /*suspend fun getMovies2(): Boolean = withContext(Dispatchers.IO) {
        return@withContext true
    }*/

    suspend fun getMoviesAsync(movieName: MutableLiveData<String>?, progress: MutableLiveData<Int>?): MutableList<Movie> =
        suspendCancellableCoroutine { cont ->

            val mutex = Mutex()
            val movies = mutableListOf<Movie>()

            val numberOfPages = if (movieName == null) SharedPreferenceHelper.pagesNumber else SEARCH_PAGES

            var cnt = 0
            for (i in 1..numberOfPages) {
                //TODO search this and replace
                CoroutineScope(Dispatchers.IO).launch {
                    val moviesListPage = if (movieName == null)
                        getPage(i)
                    else
                        movieName.value?.let { getPage(i, 0, it) }


                    withContext(Dispatchers.Default) {
                        mutex.withLock {
                            moviesListPage?.let { movies.addAll(it) }
                        }
                    }

                    cnt++
                    withContext(Dispatchers.Main) {
                        progress?.postValue(cnt)
                    }

                    if (cnt == numberOfPages) {
                        if (cont.isActive) {
                            cont.resume(movies)
                        }
                        cont.cancel()
                    }
                }

            }
        }

    suspend fun getMoviesAsync(progress: MutableLiveData<Int>?): MutableList<Movie> {
        return getMoviesAsync(null, progress)
    }

    fun applyFilters(movies: MutableList<Movie>): MutableList<Movie> {

        val newMovies = mutableListOf<Movie>()
        val englishOnly = SharedPreferenceHelper.englishOnly
        val genres = SharedPreferenceHelper.genres

        movies.forEach {

            var inLanguage = true
            val inYear: Boolean = it.year >= SharedPreferenceHelper.minYear
            val inContainment: Boolean = !checkContainment(it, genres)
            if (englishOnly) inLanguage = it.language == "en"

            if (inYear && inLanguage && inContainment)
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

    //TODO set to constants and strings
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