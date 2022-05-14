package com.michlindev.moviedownloader.main

import com.michlindev.moviedownloader.DLog
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
    suspend fun getMovies1(page: Int): List<Movie> = suspendCoroutine { cont ->

        val mDisposable = CompositeDisposable()
        val apiService = ApiClient.getInstance().create(ApiService::class.java)

        mDisposable.add(
            //TODO error with 9
            apiService.getWithParameters("", 6, DefaultData.PAGE_LIMIT, page, "year", "").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<MoviesResponse?>() {
                    override fun onSuccess(movies: MoviesResponse) {
                        //moviesList.addAll(movies.getData().getData())
                        val res = movies.data.movies


                        //DLog.d("")
                        /*globalCounter++
                        if (globalCounter == pages) {
                            allDone()
                        }
                        progressBar.setProgress(globalCounter, true)*/

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

    suspend fun getMovies2(page: Int): List<Movie> = suspendCoroutine { cont ->
        val movies = mutableListOf<Movie>()
        var cnt = 0
        for (i in 1..page) {
            DLog.d("Firing $i")
            CoroutineScope(Dispatchers.IO).launch {
                DLog.d("Start $i")
                movies.addAll(getMovies1(i))
                DLog.d("End $i")
                cnt++
                if (cnt==page) {
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
            element = document.selectFirst("span[sc-7ab21ed2-1 jGRxWM]")
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