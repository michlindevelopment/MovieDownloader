package com.michlindev.moviedownloader.login

import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.MovieDownloader
import com.michlindev.moviedownloader.R
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object LoginModel {

    var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(MovieDownloader.applicationContext().getString(R.string.default_web_client_id))
        .requestEmail().build()

    suspend fun signIn(result: ActivityResult): Boolean? = suspendCoroutine { cont ->

        val googleSignIn = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount? = googleSignIn.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        DLog.d("UID: ${task.result.user?.uid}")
                        DLog.d(account.email.toString())
                        DLog.d(account.displayName.toString())
                        DLog.d(account.id.toString())
                        DLog.d("isSuccessful")
                        cont.resume(true)
                    } else {
                        cont.resume(false)
                    }
                }
            } else {
                cont.resume(false)
            }
        } catch (e: ApiException) {
            DLog.e("exception: $e")
            cont.resumeWithException(e)
        }

    }
}