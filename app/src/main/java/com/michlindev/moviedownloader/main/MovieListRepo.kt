package com.michlindev.moviedownloader.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.api.ApiClient
import com.michlindev.moviedownloader.api.ApiService
import com.michlindev.moviedownloader.data.DefaultData
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
            apiService.getWithParameters(minRating, DefaultData.PAGE_LIMIT, page, query).subscribeOn(Schedulers.io())
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
    /*suspend fun getMoviesSync(progress: MutableLiveData<Int>): List<Movie> = suspendCancellableCoroutine { cont ->

        val movies = mutableListOf<Movie>()
        val numberOfPages = SharedPreferenceHelper.pagesNumber
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..numberOfPages) {
                movies.addAll(getPage(i))
                withContext(Dispatchers.Main) {
                    progress.postValue(i)
                }
            }
            cont.resume(movies)
        }
    }*/

    suspend fun getMoviesAsync(movieName: MutableLiveData<String>?, progress: MutableLiveData<Int>?): MutableList<Movie> =
        suspendCancellableCoroutine { cont ->

            val mutex = Mutex()
            val movies = mutableListOf<Movie>()

            //TODO set 3 to const
            val numberOfPages = if (movieName == null) SharedPreferenceHelper.pagesNumber else 3

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
                            //moviesListPage?.let { movies.addAll(it) }

                            moviesListPage?.let { movies.addAll(it) }

                           /* //var list= moviesListPage?.distinctBy { it!=null }
                            if (moviesListPage != null) {
                                movies.addAll(moviesListPage)
                            }*/
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

                    //TODO handle ifs and already resumed
                    //var newMovies = mutableListOf<Movie>()
                    /*DLog.d("cnt $cnt cont: ${cont.isActive}")
                    if (cnt == numberOfPages && cont.isActive) {

                        cont.resume(movies)
                    }
                        //if (cont.isActive) {

                        //}
                        cont.cancel()*/

                }

            }

            //////////////////////////////////////////////////

            /*val mutex = Mutex()
            val movies = mutableListOf<Movie>()

            val numberOfPages = 3

            var cnt = 0
            for (i in 1..numberOfPages) {
                //TODO search this and replace
                CoroutineScope(Dispatchers.IO).launch {
                    val sdf = movieName.value?.let { getPage(i,0, it) }
                    withContext(Dispatchers.Default) {
                        mutex.withLock {
                            if (sdf != null) {
                                movies.addAll(sdf)
                            }
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

            }*/

        }

    suspend fun getMoviesAsync(progress: MutableLiveData<Int>?): MutableList<Movie> {
        return getMoviesAsync(null,  progress)

        /*val mutex = Mutex()
        var movies = mutableListOf<Movie>()

        //val numberOfPages = pages ?: SharedPreferenceHelper.pagesNumber

        val numberOfPages = SharedPreferenceHelper.pagesNumber

        var cnt = 0

        for (i in 1..numberOfPages) {
            //TODO search this and replace
            CoroutineScope(Dispatchers.IO).launch {
                val sdf = getPage(i)

                withContext(Dispatchers.Default) {
                    mutex.withLock {
                        movies.addAll(sdf)
                    }
                }

                cnt++
                withContext(Dispatchers.Main) {
                    progress?.postValue(cnt)
                }
                if (cnt == numberOfPages) {
                    if (cont.isActive) {
                        movies = applyFilters(movies)
                        cont.resume(movies)
                    }
                    cont.cancel()
                }
            }

        }*/
    }

    fun applyFilters(movies: MutableList<Movie>): MutableList<Movie> {

        val newMovies = mutableListOf<Movie>()
        val englishOnly = SharedPreferenceHelper.englishOnly
        val genres = SharedPreferenceHelper.genres

        DLog.d("Removing ${SharedPreferenceHelper.minYear}")

        movies.forEach {
            if (it.year >= SharedPreferenceHelper.minYear && (englishOnly && it.language == "en") && !checkContainment(it, genres))
                newMovies.add(it)
        }

       /* movies.removeIf {
            it == null ||
                    it.year < SharedPreferenceHelper.minYear
                    || checkContainment(it, genres)
                    || (englishOnly && it.language != "en")
        }*/

        newMovies.sortByDescending { it.date_uploaded_unix }
        DLog.d("After filter: ${movies.size}")

        return newMovies

    }

    suspend fun getImdbPage(imdbCode: String): Imdb = suspendCoroutine { cont ->

        //TODO combine calls
        val url = "https://www.imdb.com/title/$imdbCode/"
        val document = Jsoup.connect(url).get()
        val e: Element? = document.select("script").first()
        val testModel = Gson().fromJson(e?.html(), Imdb::class.java)

        cont.resume(testModel)
    }


    suspend fun getRealRating(imdbCode: String): String = suspendCoroutine { cont ->

        //TODO handle error
        var rating = "N"

        try {
            val url = "https://www.imdb.com/title/$imdbCode/"
            val document = Jsoup.connect(url).get()
            val e: Element? = document.select("script").first()
            val testModel = Gson().fromJson(e?.html(), Imdb::class.java)
            rating = testModel?.aggregateRating?.ratingValue.toString()
        } catch (e: Exception) {
        }
        cont.resume(rating)
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