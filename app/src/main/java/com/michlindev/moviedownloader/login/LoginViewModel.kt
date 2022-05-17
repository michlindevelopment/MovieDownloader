package com.michlindev.moviedownloader.login

import androidx.lifecycle.ViewModel
import com.michlindev.moviedownloader.SingleLiveEvent


class LoginViewModel : ViewModel() {

    var signIn = SingleLiveEvent<Any>()

    fun signIn() {
        signIn.call()
    }
}