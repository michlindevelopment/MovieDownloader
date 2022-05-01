package com.michlindev.moviedownloader.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.MovieListFragmentBinding

class MovieListFragment : Fragment() {

    private val viewModel: MovieListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = MovieListFragmentBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear_file -> DLog.d("A")
            R.id.action_upload_file -> DLog.d("B")
            R.id.app_bar_switch_id -> DLog.d("C")
            else -> {}
        }
        return true
    }







}