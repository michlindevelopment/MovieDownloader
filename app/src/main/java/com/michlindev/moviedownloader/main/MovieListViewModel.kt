package com.michlindev.moviedownloader.main

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.SingleLiveEvent
import com.michlindev.moviedownloader.data.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class MovieListViewModel : ViewModel(), ItemListener {

    var itemList = MutableLiveData<List<Movie?>>()
    var isLoading = MutableLiveData(false)
    var imdbClick = SingleLiveEvent<Intent>()
    var notifyAdapter = SingleLiveEvent<Int>()
    var qualitySelectionDialog = SingleLiveEvent<Movie>()

    val maxValue: Int
        get() = SharedPreferenceHelper.pagesNumber

    var progress = MutableLiveData(0)

    init {
        getMovies()
    }

    fun getMovies() {

        val movies = mutableListOf<Movie?>()
        itemList.postValue(movies)
        progress.postValue(0)
        isLoading.postValue(true)

        CoroutineScope(Dispatchers.IO).launch {
            DLog.d("Start G")

            movies.addAll(MovieListRepo.getMoviesAsync(progress))
            DLog.d("End G - Total: ${movies.size}")

            val englishOnly = SharedPreferenceHelper.englishOnly

            //TODO Check year null

            withContext(Dispatchers.Main) {
                DLog.d("Removing ${SharedPreferenceHelper.minYear}")

                val genres = SharedPreferenceHelper.genres
                movies.removeIf {
                    it == null ||
                    it.year < SharedPreferenceHelper.minYear
                            || checkContainment(it, genres)
                            || (englishOnly && it.language != "en")
                }
                movies.sortByDescending { it?.date_uploaded_unix }
                DLog.d("After filter: ${movies.size}")
                itemList.postValue(movies)
                isLoading.postValue(false)
            }
        }

    }

    private fun checkContainment(movie: Movie, genres: MutableSet<String>?): Boolean {

        //var remove = false

        /*  DLog.d("-----------------------------------")
          DLog.d("Movie Name: ${movie.title}")
          DLog.d("Genres: ${movie.genres}")*/

        if (movie.genres.isEmpty()) {
            //DLog.d("List empty")
            return true
        } else {
            movie.genres.forEach {
                if (genres?.contains(it) == false) {
                    //DLog.d("Ret true")
                    return true
                }
            }
            //DLog.d("Ret false")
            return false
        }
    }

    override fun downloadClick(item: Movie) {
        qualitySelectionDialog.postValue(item)
    }


    override fun imdbLogoClick(item: Movie) {
        //TODO change to view model scope
        val itemIndex = itemList.value?.indexOf(item)
        itemIndex?.let { itemList.value?.get(it)?.progressing = true }

        CoroutineScope(Dispatchers.IO).launch {

            withContext(Dispatchers.Main) {
                itemIndex?.let { notifyAdapter.postValue(it) }
            }

            val rt = MovieListRepo.getRealRating(item.imdb_code)

            withContext(Dispatchers.Main) {
                itemIndex?.let { itemList.value?.get(it)?.realRating = rt }
                itemIndex?.let { itemList.value?.get(it)?.progressing = false }
                itemIndex?.let { notifyAdapter.postValue(it) }
            }

            DLog.d("Rating: $rt")
        }
    }
    /* var pagesNumber: Int
         get() = SharedPreferenceHelper.preferences.getInt(SharedPreferenceHelper.PAGES_NUMBER, 10)
         set(value) = SharedPreferenceHelper.preferences.edit().putInt(SharedPreferenceHelper.PAGES_NUMBER, value).apply()*/


    override fun posterClick(item: String) {

        val site = "https://www.imdb.com/title/$item"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(site))
        //startActivity(browserIntent)

        imdbClick.postValue(browserIntent)
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}