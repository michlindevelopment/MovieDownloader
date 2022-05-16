package com.michlindev.moviedownloader.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.databinding.SplashFragmentBinding

//TODO
/*
1. Create menu
2. Arrange movies by menu selection
3. Create search pop up
4. Name rss file correctly
5. Make option to clear file
6. Background check with DB
7. Imdb parser
8. Beautify app
9. Move to arrays from string
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