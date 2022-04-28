package com.michlindev.moviedownloader.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.michlindev.moviedownloader.FileManager
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.SplashFragmentBinding

class SplashFragment : Fragment(), ISplashView {

    /* companion object {
         fun newInstance() = SplashFragment()
     }*/

    private val viewModel: SplashViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = SplashFragmentBinding.inflate(layoutInflater)

        viewModel.splashView = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.check()

        FileManager.createFile()

        return binding.root
    }

    override fun navigateToMain() {
        findNavController().navigate(R.id.action_splashFragment_to_movieListFragment)
    }

    override fun navigateToLogin() {
        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
    }

}