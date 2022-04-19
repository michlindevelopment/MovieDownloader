package com.michlindev.moviedownloader.login

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.michlindev.moviedownloader.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LoginModel {

    suspend fun signIn(): Boolean? = suspendCoroutine { cont ->

        /*val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        val mSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        firebaseAuth = FirebaseAuth.getInstance()

        val signIntent = mSignInClient.signInIntent
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            Log.d("DTAG", "$result")

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

                                Log.d("DTAG", "UID: ${task2.result.user?.uid}")
                                Log.d("DTAG", account.email.toString())
                                Log.d("DTAG", account.displayName.toString())
                                Log.d("DTAG", account.id.toString())
                                Log.d("DTAG", "isSuccessful")
                            }
                        }


                    }
                } catch (e: ApiException) {
                    Log.d("DTAG", "exception: $e")
                }
            }
        }.launch(signIntent)*/



        for (i in 0..10) {
            //println(i)
            Log.d("DTAG", "Cnt $i")
            runBlocking {
                delay(1000)
            }

        }
        cont.resume(true)
    }



}