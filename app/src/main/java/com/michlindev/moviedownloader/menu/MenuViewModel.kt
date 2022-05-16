package com.michlindev.moviedownloader.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.SingleLiveEvent

class MenuViewModel : ViewModel() {

    var pageNumbersArray = MutableLiveData(generateRange(1,25))
    var ratingArray = MutableLiveData(generateRange(0,9))
    var yearArray = MutableLiveData(generateRange(2000,2023))
    var englishOnly = MutableLiveData(SharedPreferenceHelper.englishOnly)

    var pageNumbersPosition = MutableLiveData(pageNumbersArray.value?.indexOf(SharedPreferenceHelper.pagesNumber))
    var ratingPosition = MutableLiveData(ratingArray.value?.indexOf(SharedPreferenceHelper.minRating))
    var yearPosition = MutableLiveData(yearArray.value?.indexOf(SharedPreferenceHelper.minYear))

    var showDialog = SingleLiveEvent<Any>()


    private fun generateRange(min: Int, max: Int):MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in min..max) {
            list.add(i)
        }
        return list
    }

    fun showDialog() {
        showDialog.call()
    }



}