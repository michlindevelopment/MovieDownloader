package com.michlindev.moviedownloader.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.SingleLiveEvent

class GenreDialogViewModel : ViewModel(), ItemListener {

    var itemList = MutableLiveData<List<Genre>>()
    var allEnabled = MutableLiveData<Boolean>()
    var dismissDialog = SingleLiveEvent<Any>()

    fun saveSelected() {
        val pop = mutableSetOf<String>()
        itemList.value?.forEach {
            if (it.enabled.value == true) {
                pop.add(it.genre)
            }
        }
        SharedPreferenceHelper.genres = pop
        dismissDialog.call()
    }

    fun populate(stringArray: Array<String>?) {
        val newGenres = mutableListOf<Genre>()
        val genres = SharedPreferenceHelper.genres

        stringArray?.forEach {
            val toPut = genres != null && genres.contains(it)
            newGenres.add(Genre(genre = it, enabled = MutableLiveData(toPut)))
        }
        //allEnabled.postValue(false)
        itemList.postValue(newGenres)
    }
}