package com.michlindev.moviedownloader.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel : ViewModel() {

    var signIn = SingleLiveEvent<Any>()

    fun signIn() {
        signIn.call()
    }
}