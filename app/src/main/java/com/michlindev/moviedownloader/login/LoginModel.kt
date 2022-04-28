package com.michlindev.moviedownloader.login

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.MovieDownloader
import com.michlindev.moviedownloader.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LoginModel {

    var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(MovieDownloader.applicationContext().getString(R.string.default_web_client_id))
        .requestEmail().build()


    suspend fun signIn(result: ActivityResult): Boolean? = suspendCoroutine { cont ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                // UpdateUI(account)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                //firebaseAuth = FirebaseAuth.getInstance()
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {

                        Firebase.auth.currentUser?.uid

                        DLog.d("UID: ${task2.result.user?.uid}")
                        DLog.d(account.email.toString())
                        DLog.d(account.displayName.toString())
                        DLog.d(account.id.toString())
                        DLog.d("isSuccessful")
                        cont.resume(true)
                    }
                }


            }
        } catch (e: ApiException) {
            DLog.d("exception: $e")
        }

    }

    /*fun registerForActivityResult(loginFragment: LoginFragment){
    //fun registerForActivityResult(loginFragment: LoginFragment) {
        lapupu = loginFragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            DLog.d( "$result")

            if (result.resultCode == Activity.RESULT_OK) {
                // parse result and perform action
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                    if (account != null) {
                        // UpdateUI(account)
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {

                                Firebase.auth.currentUser?.uid

                                DLog.d( "UID: ${task2.result.user?.uid}")
                                DLog.d( account.email.toString())
                                DLog.d( account.displayName.toString())
                                DLog.d( account.id.toString())
                                DLog.d( "isSuccessful")

                                cont.resume(true)
                            }
                        }


                    }
                } catch (e: ApiException) {
                    DLog.d( "exception: $e")
                }
            }
        }
    }*/


}