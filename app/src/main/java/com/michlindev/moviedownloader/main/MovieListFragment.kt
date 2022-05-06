package com.michlindev.moviedownloader.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.MovieListFragmentBinding

class MovieListFragment : Fragment() {

    private val viewModel: MovieListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = MovieListFragmentBinding.inflate(layoutInflater)

        binding.adapter = ConnectedItemAdapter(listOf(), viewModel)
        viewModel.itemList.observe(viewLifecycleOwner) {
            binding.adapter?.notifyDataSetChanged()
        }

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
            R.id.action_debug_menu -> findNavController().navigate(R.id.action_movieListFragment_to_debugFragment)
            R.id.action_upload_file -> DLog.d("B")
            R.id.action_search -> DLog.d("C")
            else -> {}
        }
        return true
    }


}