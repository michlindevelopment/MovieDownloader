package com.michlindev.moviedownloader.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.dialog.ItemListener

class GenreDialogViewModel : ViewModel(), ItemListener {

    var itemList = MutableLiveData<List<Genre>>()
}