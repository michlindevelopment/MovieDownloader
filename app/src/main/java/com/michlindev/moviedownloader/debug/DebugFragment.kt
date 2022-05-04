package com.michlindev.moviedownloader.debug

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.michlindev.moviedownloader.databinding.DebugFragmentBinding

class DebugFragment : Fragment() {

    private val viewModel: DebugViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = DebugFragmentBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

}