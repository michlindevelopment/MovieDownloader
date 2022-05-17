package com.michlindev.moviedownloader.main

import android.view.View
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
    //Check if file exist on sd
    //Create if not with content
    //Upload it

    //Add fixed data
    //Upload it to firebase
    var itemList = MutableLiveData<List<Movie>>()
    var imdbClick = SingleLiveEvent<String>()

    /*fun getMovies() {
        CoroutineScope(Dispatchers.IO).launch {

            val movies = mutableListOf<Movie>()
            for (i in 1..10) {
                DLog.d("Start $i")
                movies.addAll(MovieListRepo.getMovies1(i))
                DLog.d("End $i")
            }
            val flt: List<Movie> = movies.filter { it.language == "en" }

            DLog.d("Size ${flt.size}")
            withContext(Dispatchers.Main)
            {
                itemList.postValue(flt)
            }
        }
    }*/

    /*fun getMovies() {

        val movies = mutableListOf<Movie>()
        for (i in 1..10) {
            CoroutineScope(Dispatchers.IO).launch {
                DLog.d("Start $i")
                movies.addAll(MovieListRepo.getMovies1(i))
                DLog.d("End $i")
            }

        }
        DLog.d("Aha")

    }*/


    fun getMovies() {

        val movies = mutableListOf<Movie>()
        itemList.postValue(movies)

        CoroutineScope(Dispatchers.IO).launch {
            DLog.d("Start G")

            movies.addAll(MovieListRepo.getMovies())
            DLog.d("End G - Total: ${movies.size}")

            val englishOnly = SharedPreferenceHelper.englishOnly

            //TODO Check year null
            withContext(Dispatchers.Main) {
                DLog.d("Removing ${SharedPreferenceHelper.minYear}")
                movies.removeIf {it.year < SharedPreferenceHelper.minYear
                        || it.genres?.contains("Documentary") ?: true
                        || (englishOnly && it.language!="en")}
                movies.sortByDescending { it.date_uploaded_unix }
                DLog.d("After filter: ${movies.size}")
                itemList.postValue(movies)
            }
        }

    }


    override fun onItemCheckedClicked(item: Movie, view: View) {
        TODO("Not yet implemented")
    }

    override fun zoomImage(view: View, image: Int) {
        TODO("Not yet implemented")
    }

    override fun infoImage(item: Movie) {
        DLog.d("Clicked: ${item.title_english}")
    }

    override fun rating(item: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val rt = MovieListRepo.getRealRating(item)
            DLog.d("Rating: $rt")
        }
        //DLog.d("H1")
        //imdbClick.postValue(item)
    }


}