package com.michlindev.moviedownloader.dialogs

import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.SingleLiveEvent

class GenreDialogViewModel : ViewModel(), ItemListener {

    var itemList = MutableLiveData<List<Genre>>()
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
            val toPut = genres.contains(it)
            newGenres.add(Genre(genre = it, enabled = MutableLiveData(toPut)))
        }
        itemList.postValue(newGenres)
    }

    fun selectAll(view: View) {
        val checkBox = view as CheckBox
        itemList.value?.forEach {
            it.enabled.value = checkBox.isChecked
        }
        itemList.notifyObserver()


    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

}