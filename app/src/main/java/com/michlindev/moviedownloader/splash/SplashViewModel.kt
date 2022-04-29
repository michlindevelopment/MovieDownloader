package com.michlindev.moviedownloader.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.SingleLiveEvent

class SplashViewModel : ViewModel() {



    var navigate = SingleLiveEvent<Int>()


    fun check() {
        DLog.d( "${Firebase.auth.currentUser}")

        if (Firebase.auth.currentUser == null) {
            navigate.postValue(R.id.action_splashFragment_to_loginFragment)
        } else {
            navigate.postValue(R.id.action_splashFragment_to_movieListFragment)
        }
    }

}