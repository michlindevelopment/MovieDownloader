package com.michlindev.moviedownloader.main

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.data.Constants.IMDB_CODE
import com.michlindev.moviedownloader.database.DataBaseHelper
import com.michlindev.moviedownloader.databinding.FragmentMovieListBinding
import com.michlindev.moviedownloader.dialogs.DialogsBuilder
import kotlinx.coroutines.launch


class MovieListFragment : Fragment() {

    private val viewModel: MovieListViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentMovieListBinding.inflate(layoutInflater)

        binding.adapter = MovieItemAdapter(listOf(), viewModel)


        with(viewModel) {
            itemList.observe(viewLifecycleOwner) {
                binding.adapter?.notifyDataSetChanged()
            }
            notifyAdapter.observe(viewLifecycleOwner) {
                it?.let { binding.adapter?.notifyItemChanged(it) }
            }
            qualitySelectionDialog.observe(viewLifecycleOwner) {
                it?.let {
                    DialogsBuilder.createQualityDialog(it,requireContext(),lifecycleScope){
                        viewModel.updateMovieDownloaded(it)
                    }
                }
            }
            imdbClick.observe(viewLifecycleOwner) {
                val bundle = Bundle()
                bundle.putString(IMDB_CODE, it)
                findNavController().navigate(R.id.action_movieListFragment_to_imdbPage, bundle)
            }
            showToast.observe(viewLifecycleOwner){
                Toast.makeText(requireActivity(),it, Toast.LENGTH_SHORT).show()
            }


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
            R.id.action_search -> viewModel.searchVisible.value?.let { viewModel.searchVisible.postValue(!it) }
            R.id.action_clear_db -> {
                DialogsBuilder.clearDBDialog(requireContext()) {
                    lifecycleScope.launch {
                        DataBaseHelper.clearDb()
                    }
                }
            }
            R.id.action_rss_url ->  DialogsBuilder.showRssUrl(requireActivity())
        }
        return true
    }




}