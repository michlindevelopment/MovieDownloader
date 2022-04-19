package com.michlindev.moviedownloader.login

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LoginModel {

    suspend fun signIn(): Boolean? = suspendCoroutine { cont ->

        for (i in 0..10) {
            //println(i)
            Log.d("DTAG", "Cnt $i")
            runBlocking {
                delay(1000)
            }

        }
        cont.resume(true)
    }

    /*fun signIn() {
        for ()
    }*/


}