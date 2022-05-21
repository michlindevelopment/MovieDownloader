package com.michlindev.moviedownloader.main

import android.content.Intent
import android.net.Uri
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

        binding.adapter = MovieItemAdapter(listOf(), viewModel)
        viewModel.itemList.observe(viewLifecycleOwner) {
            binding.adapter?.notifyDataSetChanged()
        }

        //binding.recyclerView.recycledViewPool.setMaxRecycledViews(R.layout.list_layout_new, 0)
        viewModel.testVar.observe(viewLifecycleOwner) {
            DLog.d("Observing testVar")
        }


        viewModel.imdbClick.observe(viewLifecycleOwner) {
            DLog.d("Clicked")
            val site = "https://www.imdb.com/title/$it"
            DLog.d("Site $site")
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(site))
            startActivity(browserIntent)
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
            R.id.action_app_settings -> findNavController().navigate(R.id.action_movieListFragment_to_menuFragment)
            R.id.action_search -> DLog.d("C")
            else -> {}
        }
        return true
    }


}