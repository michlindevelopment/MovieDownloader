package com.michlindev.moviedownloader.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashViewModel : ViewModel() {

    var splashView: ISplashView? = null

    //init {}

    fun check() {

        Log.d("DTAG", "${Firebase.auth.currentUser}")

        if (Firebase.auth.currentUser == null) {
            splashView?.navigateToLogin()
        } else {
            Log.d("DTAG", "User: ${Firebase.auth.currentUser!!.uid}")
            //splashView?.navigateToMain()

        }
    }

}