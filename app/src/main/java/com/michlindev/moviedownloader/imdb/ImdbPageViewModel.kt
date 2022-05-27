package com.michlindev.moviedownloader.imdb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.michlindev.moviedownloader.main.MovieListRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImdbPageViewModel : ViewModel() {

    var movie = MutableLiveData<Imdb?>()
    var loading = MutableLiveData(true)


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


}