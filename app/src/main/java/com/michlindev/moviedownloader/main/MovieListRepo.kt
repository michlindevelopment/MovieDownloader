package com.michlindev.moviedownloader.main

import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.api.ApiClient
import com.michlindev.moviedownloader.api.ApiService
import com.michlindev.moviedownloader.data.DefaultData
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.data.MoviesResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
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

        DLog.d("imdbCode: $imdbCode")
        val url = "https://www.imdb.com/title/$imdbCode/"
        DLog.d("url: $url")

        var document: Document? = null
        var element: Element? = null

        try {
            document = Jsoup.connect(url).get()
            DLog.d("document: $document")
            DLog.d("-----------------------------")
            DLog.d("-----------------------------")
            DLog.d("-----------------------------")
            DLog.d("-----------------------------")

            val e: Element = document.select("script").first()!!
            val s = e.html()


            element = document.selectFirst("script[type=application/ld+json]")
            DLog.d("---------------------------------------element: $element")
        } catch (e: IOException) {
            e.printStackTrace()
            DLog.e("error: $e")
        }
        //assert(element != null)
        val sor = element?.text()
        cont.resume(sor.toString())
    }
}