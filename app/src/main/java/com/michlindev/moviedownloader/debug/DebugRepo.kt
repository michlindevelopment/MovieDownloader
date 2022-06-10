package com.michlindev.moviedownloader.debug

import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.api.ApiClient
import com.michlindev.moviedownloader.api.ApiService
import com.michlindev.moviedownloader.data.DefaultData
import com.michlindev.moviedownloader.data.MoviesResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

object DebugRepo {
    fun getMovies() {

        val mDisposable = CompositeDisposable()
        val apiService = ApiClient.getInstance().create(ApiService::class.java)

        mDisposable.add(
            apiService.getWithParameters(6, DefaultData.PAGE_LIMIT, 2,  "").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<MoviesResponse?>() {
                    override fun onSuccess(movies: MoviesResponse) {
                        //var res = movies.data.movies
                    }

                    override fun onError(e: Throwable) {
                        DLog.d("")
                        /* Log.e(MainActivity.TAG, String.format("onError: %s", e.message), e)
                         setEnabledBar(true)*/
                    }
                })
        )
    }
}