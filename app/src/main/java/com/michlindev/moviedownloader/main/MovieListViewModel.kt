package com.michlindev.moviedownloader.main

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.FileManager
import com.michlindev.moviedownloader.MovieDownloader
import com.michlindev.moviedownloader.SingleLiveEvent
import com.michlindev.moviedownloader.data.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

class MovieListViewModel : ViewModel(), ItemListener {
    //Check if file exist on sd
    //Create if not with content
    //Upload it

    //Add fixed data
    //Upload it to firebase
    var itemList = MutableLiveData<List<Movie>>()

    fun getMovies() {
        CoroutineScope(Dispatchers.IO).launch {

            val tuta = MovieListRepo.getMovies1()

            DLog.d("H")
            withContext(Dispatchers.Main)
            {
                itemList.postValue(tuta)
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


}