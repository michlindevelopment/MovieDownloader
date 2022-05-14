package com.michlindev.moviedownloader.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.SharedPreferenceHelper

class MenuViewModel : ViewModel() {

    var pageNumbersArray = MutableLiveData(getPageNumbering())
    var pageNumbersPosition = MutableLiveData(pageNumbersArray.value?.indexOf(SharedPreferenceHelper.pagesNumber))

    private fun getPageNumbering():Array<String> {
        val list = mutableListOf<String>()
        for (i in 1..25) {
            list.add(i.toString())
        }
        return list.toTypedArray()

    }
}