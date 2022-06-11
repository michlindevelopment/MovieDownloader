package com.michlindev.moviedownloader.main

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.SingleLiveEvent
import com.michlindev.moviedownloader.data.Constants.SEARCH_PAGES
import com.michlindev.moviedownloader.data.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieListViewModel : ViewModel(), ItemListener {

    var itemList = MutableLiveData<List<Movie?>>()
    var isLoading = MutableLiveData(false)
    var searchVisible = MutableLiveData(false)
    var imdbClick = SingleLiveEvent<String>()
    var notifyAdapter = SingleLiveEvent<Int>()
    var showToast = SingleLiveEvent<String>()
    var qualitySelectionDialog = SingleLiveEvent<Movie>()
    var searchField = MutableLiveData<String>()
    var maxProgressValue = MutableLiveData(SharedPreferenceHelper.pagesNumber)
    var progress = MutableLiveData(0)

    init {
        getMovies()
    }

    fun searchMovie() {
        maxProgressValue.postValue(SEARCH_PAGES)
        progress.postValue(0)
        itemList.postValue(mutableListOf<Movie>())
        viewModelScope.launch(Dispatchers.IO) {
            itemList.postValue(MovieListRepo.getMoviesAsync(searchField, progress, this))
        }

    }

    fun getMovies() {
        var movies = mutableListOf<Movie>()
        maxProgressValue.postValue(SharedPreferenceHelper.pagesNumber)
        searchVisible.postValue(false)
        itemList.postValue(movies)
        progress.postValue(0)
        isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {

            movies.addAll(MovieListRepo.getMoviesAsync(progress, this))
            movies = MovieListRepo.applyFilters(movies)

            withContext(Dispatchers.Main) {
                if (movies.isNotEmpty()) {
                    val max = movies.maxOf { it.id }
                    SharedPreferenceHelper.lastMovie = max
                    itemList.postValue(movies)
                } else {
                    showToast.postValue("Error getting movies")

                }
                isLoading.postValue(false)
            }
        }

    }

    override fun downloadClick(item: Movie) {
        qualitySelectionDialog.postValue(item)
    }


    override fun imdbLogoClick(item: Movie) {
        val itemIndex = itemList.value?.indexOf(item)
        itemIndex?.let { itemList.value?.get(it)?.progressing = true }

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                itemIndex?.let { notifyAdapter.postValue(it) }
            }

            val rt = MovieListRepo.getRealRating(item.imdb_code)

            withContext(Dispatchers.Main) {
                itemIndex?.let { itemList.value?.get(it)?.realRating = rt }
                itemIndex?.let { itemList.value?.get(it)?.progressing = false }
                itemIndex?.let { notifyAdapter.postValue(it) }
            }
        }
    }

    override fun posterClick(item: String) {
        imdbClick.postValue(item)
    }
}