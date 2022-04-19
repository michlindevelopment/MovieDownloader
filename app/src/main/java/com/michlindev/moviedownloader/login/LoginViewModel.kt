package com.michlindev.moviedownloader.login

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val repo = LoginModel

    fun signIn() {
        //do some shit

        CoroutineScope(Dispatchers.IO).launch {
            repo.signIn()
            Log.d("DTAG","Resumed")

            //when done notify fragment
        }
    }
}