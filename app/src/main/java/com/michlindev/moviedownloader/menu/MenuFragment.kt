package com.michlindev.moviedownloader.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.data.Constants.MIN_RATING
import com.michlindev.moviedownloader.data.Constants.MIN_YEAR
import com.michlindev.moviedownloader.data.Constants.PAGES
import com.michlindev.moviedownloader.databinding.FragmentMenuBinding
import com.michlindev.moviedownloader.dialogs.GenreDialogFragment

class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentMenuBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        viewModel.pageNumbersPosition.observe(viewLifecycleOwner) {
            SharedPreferenceHelper.pagesNumber = it?.let { it1 -> viewModel.pageNumbersArray.value?.get(it1) } ?: PAGES
        }

        viewModel.ratingPosition.observe(viewLifecycleOwner) {
            SharedPreferenceHelper.minRating = it?.let { it1 -> viewModel.ratingArray.value?.get(it1) } ?: MIN_RATING
        }

        viewModel.yearPosition.observe(viewLifecycleOwner) {
            SharedPreferenceHelper.minYear = it?.let { it1 -> viewModel.yearArray.value?.get(it1) } ?: MIN_YEAR
        }

        viewModel.englishOnly.observe(viewLifecycleOwner) {
            SharedPreferenceHelper.englishOnly = it
        }

        viewModel.showDialog.observe(viewLifecycleOwner) {
            val genres = resources.getStringArray(R.array.genres)
            GenreDialogFragment.newInstance(genres).show(childFragmentManager, GenreDialogFragment.TAG)

        }

        return binding.root
    }

}