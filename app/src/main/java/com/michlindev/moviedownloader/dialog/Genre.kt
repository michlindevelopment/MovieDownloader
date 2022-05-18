package com.michlindev.moviedownloader.dialog

import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName
import com.michlindev.moviedownloader.main.ListAdapterItem
import java.io.Serializable
import java.util.*

data class Genre(
    override val id: Long = 0,
    var genre: String,
    //var enabled : MutableLiveData<Boolean>
    var enabled : Boolean
) : ListAdapterItem, Serializable




