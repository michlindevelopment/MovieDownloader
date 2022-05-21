package com.michlindev.moviedownloader.main

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object MovieListRepo {

    //suspend fun signIn(result: ActivityResult): Boolean? = suspendCoroutine { cont ->
    private suspend fun getPage(page: Int): List<Movie> = suspendCoroutine { cont ->

        val mDisposable = CompositeDisposable()
        val apiService = ApiClient.getInstance().create(ApiService::class.java)

        mDisposable.add(
            //TODO error with 9
            apiService.getWithParameters(SharedPreferenceHelper.minRating, DefaultData.PAGE_LIMIT, page, "").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<MoviesResponse?>() {
                    override fun onSuccess(movies: MoviesResponse) {
                        var res = movies.data.movies



                        /*globalCounter++
                        if (globalCounter == pages) {
                            allDone()
                        }
                        progressBar.setProgress(globalCounter, true)*/

                        if (res==null) res = listOf()
                        cont.resume(res)
                    }

                    override fun onError(e: Throwable) {
                        DLog.d("$e")
                        /* Log.e(MainActivity.TAG, String.format("onError: %s", e.message), e)
                         setEnabledBar(true)*/
                    }
                })
        )
    }

    //Todo maybe change to this type
    suspend fun getMovies2(): Boolean = withContext(Dispatchers.IO) {
        return@withContext true
    }

    suspend fun getMovies(): List<Movie> = suspendCoroutine { cont ->
        val movies = mutableListOf<Movie>()

        val numberOfPages = SharedPreferenceHelper.pagesNumber

        DLog.d("Num of pages: $numberOfPages")
        DLog.d("Min Rating: ${SharedPreferenceHelper.minRating}")


        var cnt = 0

        for (i in 1..numberOfPages) {
            DLog.d("Firing $i")
            CoroutineScope(Dispatchers.IO).launch {
                DLog.d("Start $i")
                DLog.d("End1 $i")
                movies.addAll(getPage(i))
                DLog.d("End2 $i")
                cnt++
                if (cnt == numberOfPages) {
                    DLog.d("Resuming")
                    cont.resume(movies)
                }
            }

        }
    }


    suspend fun getRealRating(imdbCode: String): String = suspendCoroutine { cont ->

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
}