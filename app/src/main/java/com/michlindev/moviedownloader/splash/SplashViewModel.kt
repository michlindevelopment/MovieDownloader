package com.michlindev.moviedownloader.splash

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashViewModel : ViewModel() {

    var splashView: ISplashView? = null

    init {

    }

    fun check(){
        if (Firebase.auth.currentUser == null) splashView?.navigateToLogin() else splashView?.navigateToMain()
    }

}