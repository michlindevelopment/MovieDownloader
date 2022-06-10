package com.michlindev.moviedownloader.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.databinding.FragmentSplashBinding

//TODO
/*
Go over to dos
*/

class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentSplashBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.check()

        viewModel.navigate.observe(viewLifecycleOwner){
            it?.let { it1 -> findNavController().navigate(it1) }
        }

        return binding.root
    }
}