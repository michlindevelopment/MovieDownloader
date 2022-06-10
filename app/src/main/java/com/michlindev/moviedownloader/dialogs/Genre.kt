package com.michlindev.moviedownloader.dialogs

import androidx.lifecycle.MutableLiveData
import com.michlindev.moviedownloader.main.ListAdapterItem
import java.io.Serializable

data class Genre(
    override val id: Long = 0,
    var genre: String,
    var enabled : MutableLiveData<Boolean>
) : ListAdapterItem, Serializable




