package com.michlindev.moviedownloader.imdb

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.FragmentImdbPageBinding
import com.michlindev.moviedownloader.databinding.SplashFragmentBinding
import com.michlindev.moviedownloader.main.MovieListRepo
import com.michlindev.moviedownloader.splash.SplashViewModel
import kotlinx.coroutines.launch

class ImdbPageFragment : Fragment() {

    private val viewModel: ImdbPageViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentImdbPageBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //TODO make string static
        viewModel.getImdb(arguments?.getString("imdbCode"))

        return binding.root
    }

}