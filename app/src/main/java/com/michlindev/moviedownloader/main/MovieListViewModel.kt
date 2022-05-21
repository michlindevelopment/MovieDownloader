package com.michlindev.moviedownloader.main

import android.util.Log
import android.view.View
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

    var itemList = MutableLiveData<List<Movie>>()
    var imdbClick = SingleLiveEvent<String>()

    var testVar = MutableLiveData<String>()

    fun getMovies() {

        val movies = mutableListOf<Movie>()
        itemList.postValue(movies)

        //TODO change to lifecycle
        CoroutineScope(Dispatchers.IO).launch {
            DLog.d("Start G")

            movies.addAll(MovieListRepo.getMovies())
            DLog.d("End G - Total: ${movies.size}")

            val englishOnly = SharedPreferenceHelper.englishOnly

            //TODO Check year null
            withContext(Dispatchers.Main) {
                DLog.d("Removing ${SharedPreferenceHelper.minYear}")

                val genres = SharedPreferenceHelper.genres

                movies.removeIf {
                    it.year < SharedPreferenceHelper.minYear
                            //|| it.genres?.contains("Documentary") ?: true
                            || checkContainment(it, genres)
                            || (englishOnly && it.language != "en")
                }
                movies.sortByDescending { it.date_uploaded_unix }
                DLog.d("After filter: ${movies.size}")
                itemList.postValue(movies)
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
        }
        else {
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



    override fun imdbLogoClick(item: Movie) {
        //TODO change to view model scope
        CoroutineScope(Dispatchers.IO).launch {
            val rt = MovieListRepo.getRealRating(item.imdb_code)

            withContext(Dispatchers.Main) {
                val rr = itemList.value?.indexOf(item)
                itemList.value!![rr!!].realRating = rt
                itemList.notifyObserver()
            }

            DLog.d("Rating: $rt")
        }
    }

    override fun posterClick(item: String) {
        imdbClick.postValue(item)
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}