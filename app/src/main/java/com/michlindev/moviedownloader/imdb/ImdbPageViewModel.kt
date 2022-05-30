package com.michlindev.moviedownloader.imdb

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michlindev.moviedownloader.SingleLiveEvent
import com.michlindev.moviedownloader.main.MovieListRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImdbPageViewModel : ViewModel() {

    var movie = MutableLiveData<Imdb?>()
    var loading = MutableLiveData(true)
    var imdbClick = SingleLiveEvent<Intent>()

    fun getImdb(arg: String?) {
        loading.postValue(true)
        movie.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            arg?.let {
                val pop = MovieListRepo.getImdbPage(it)
                movie.postValue(pop)
                loading.postValue(false)
            }
        }

    }

    //TODO combine functions
    fun openImdb() {

        val site = "https://www.imdb.com/${movie.value?.url}"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(site))
        imdbClick.postValue(browserIntent)
    }

    fun openTrailer() {

        val site = "https://www.imdb.com/${movie.value?.trailer?.url}"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(site))
        imdbClick.postValue(browserIntent)

    }


}