package com.michlindev.moviedownloader.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    var searchField = MutableLiveData<String>()

    //var searchInput = MutableLiveData("")

    var maxProgressValue = MutableLiveData(SharedPreferenceHelper.pagesNumber)

  /*  val maxValue: Int
        get() = SharedPreferenceHelper.pagesNumber*/

    var progress = MutableLiveData(0)

    init {
        getMovies()
    }

    fun searchMovie() {
        //TODO make better way to clear
        val movies = mutableListOf<Movie>()
        maxProgressValue.postValue(3)
        progress.postValue(0)
        itemList.postValue(movies)
        viewModelScope.launch {
            val movies1 = MovieListRepo.getMoviesAsync(searchField,progress)
            itemList.postValue(movies1)
        }

    }

    fun getMovies() {

        var movies = mutableListOf<Movie>()
        maxProgressValue.postValue(SharedPreferenceHelper.pagesNumber)
        searchVisible.postValue(false)
        itemList.postValue(movies)
        progress.postValue(0)
        isLoading.postValue(true)

        viewModelScope.launch {

            movies.addAll(MovieListRepo.getMoviesAsync(progress))
            movies = MovieListRepo.applyFilters(movies)

            withContext(Dispatchers.Main) {

                if (movies.isNotEmpty()) {
                    val max = movies.maxOf { it.id }
                    SharedPreferenceHelper.lastMovie = max
                    itemList.postValue(movies)
                    isLoading.postValue(false)
                }
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