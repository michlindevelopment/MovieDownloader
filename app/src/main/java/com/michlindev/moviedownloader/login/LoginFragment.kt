package com.michlindev.moviedownloader.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.LoginFragmentBinding


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

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
                            /*SavedPreference.setEmail(this,account.email.toString())
                            SavedPreference.setUsername(this,account.displayName.toString())
*/
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
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = LoginFragmentBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.signInButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()

            val mSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            firebaseAuth = FirebaseAuth.getInstance()

            val signIntent = mSignInClient.signInIntent
            resultLauncher.launch(signIntent)
        }

        return binding.root
    }


}