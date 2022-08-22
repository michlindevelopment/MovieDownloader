package com.michlindev.moviedownloader.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.R
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
                    DialogsBuilder.createQualityDialog(it, requireContext(), lifecycleScope) {
                        viewModel.updateMovieDownloaded(it)
                    }
                }
            }
            imdbClick.observe(viewLifecycleOwner) {
                val bundle = Bundle()
                bundle.putString(IMDB_CODE, it)
                findNavController().navigate(R.id.action_movieListFragment_to_imdbPage, bundle)
            }
            showToast.observe(viewLifecycleOwner) {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            }


        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.app_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_debug_menu -> {
                        findNavController().navigate(R.id.action_movieListFragment_to_debugFragment)
                        true
                    }
                    R.id.action_app_settings -> {
                        findNavController().navigate(R.id.action_movieListFragment_to_menuFragment)
                        true
                    }
                    R.id.action_search -> {
                        viewModel.searchVisible.value?.let { viewModel.searchVisible.postValue(!it) }
                        true
                    }
                    R.id.action_clear_db -> {
                        DialogsBuilder.clearDBDialog(requireContext()) {
                            lifecycleScope.launch {
                                DataBaseHelper.clearDb()
                            }
                        }
                        true
                    }
                    R.id.action_rss_url -> {
                        DialogsBuilder.showRssUrl(requireActivity())
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}