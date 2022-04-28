package com.michlindev.moviedownloader.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.michlindev.moviedownloader.DLog

class SplashViewModel : ViewModel() {

    var splashView: ISplashView? = null

    //init {}

    fun check() {


        DLog.d( "${Firebase.auth.currentUser}")

        if (Firebase.auth.currentUser == null) {
            splashView?.navigateToLogin()
        } else {
            DLog.d( "User: ${Firebase.auth.currentUser!!.uid}")
            //splashView?.navigateToMain()

        }
    }

}