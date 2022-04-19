package com.michlindev.moviedownloader.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.LoginFragmentBinding


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // parse result and perform action
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = LoginFragmentBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        /*var signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()*/


        binding.signInButton.setOnClickListener {

           val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()

            val mSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            val signIntent = mSignInClient.signInIntent
            //startActivityForResult(signIntent, 10)




            //val signInIntent = mGoogleSignInClient.signInIntent
            resultLauncher.launch(signIntent)



           /* var resultLauncher = registerForActivityResult(signIntent) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                }
            }*/

        }

        return binding.root
    }


}