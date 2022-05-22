package com.michlindev.moviedownloader.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.databinding.SplashFragmentBinding

//TODO
/*
Progress bar + pull
Create search pop up
Name rss file correctly
Make option to clear file
Background check with DB
Beautify app
Create imdb page
Write to file with popup
 */

class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = SplashFragmentBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.check()

        viewModel.navigate.observe(viewLifecycleOwner){
            it?.let { it1 -> findNavController().navigate(it1) }
        }

        return binding.root
    }
}