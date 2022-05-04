package com.michlindev.moviedownloader.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.LoginFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        DLog.d("$result")
        if (result.resultCode == Activity.RESULT_OK) {
            CoroutineScope(Dispatchers.IO).launch {
                val res = LoginRepo.signIn(result)
                //TODO add if true
                DLog.d("Res: $res")

                if (res == true) {
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_loginFragment_to_movieListFragment)
                        //findNavController(fragment).navigate(SignInFragmentDirections.actionSignInFragmentToUserNameFragment())

                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = LoginFragmentBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.signIn.observe(viewLifecycleOwner) {
            resultLauncher.launch(GoogleSignIn.getClient(requireActivity(), LoginRepo.gso).signInIntent)
        }

        return binding.root
    }


}