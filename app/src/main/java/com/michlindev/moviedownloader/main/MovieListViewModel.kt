package com.michlindev.moviedownloader.main

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.SingleLiveEvent
import com.michlindev.moviedownloader.data.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieListViewModel : ViewModel(), ItemListener {

    var itemList = MutableLiveData<List<Movie?>>()
    var isLoading = MutableLiveData(false)
    var searchVisible = MutableLiveData(false)
    var imdbClick = SingleLiveEvent<String>()
    var notifyAdapter = SingleLiveEvent<Int>()
    var qualitySelectionDialog = SingleLiveEvent<Movie>()
    //var searchInput = MutableLiveData("")

    val maxValue: Int
        get() = SharedPreferenceHelper.pagesNumber

    var progress = MutableLiveData(0)

    init {
        getMovies()
    }

    val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    val movies = MovieListRepo.searchMovie(s.toString())
                    itemList.postValue(movies)
                } //else itemList.postValue(mutableListOf())
            }
        }
        override fun afterTextChanged(s: Editable) {        }
    }

    fun getMovies() {

        val movies = mutableListOf<Movie>()
        itemList.postValue(movies)
        progress.postValue(0)
        isLoading.postValue(true)

        CoroutineScope(Dispatchers.IO).launch {
            DLog.d("Start G")

            movies.addAll(MovieListRepo.getMoviesAsync(progress))
            withContext(Dispatchers.Main) {

                val max = movies.maxOf { it.id }
                SharedPreferenceHelper.lastMovie = max

                itemList.postValue(movies)
                isLoading.postValue(false)
            }
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

    override fun posterClick(imdbCode: String) {
        /*val site = "https://www.imdb.com/title/$item"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(site))
        imdbClick.postValue(browserIntent)*/
        imdbClick.postValue(imdbCode)
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}