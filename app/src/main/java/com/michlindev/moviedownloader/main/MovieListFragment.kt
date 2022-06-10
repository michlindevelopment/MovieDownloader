package com.michlindev.moviedownloader.main

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.SharedPreferenceHelper
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.database.DataBaseHelper
import com.michlindev.moviedownloader.databinding.MovieListFragmentBinding
import kotlinx.coroutines.launch


class MovieListFragment : Fragment() {

    private val viewModel: MovieListViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = MovieListFragmentBinding.inflate(layoutInflater)

        binding.adapter = MovieItemAdapter(listOf(), viewModel)


        with(viewModel) {
            itemList.observe(viewLifecycleOwner) {
                binding.adapter?.notifyDataSetChanged()
            }
            notifyAdapter.observe(viewLifecycleOwner) {
                it?.let { binding.adapter?.notifyItemChanged(it) }
            }
            qualitySelectionDialog.observe(viewLifecycleOwner) {
                it?.let { movie -> createQualityDialog(movie) }
            }
            imdbClick.observe(viewLifecycleOwner) {
                val bundle = Bundle()
                bundle.putString("imdbCode", it)
                findNavController().navigate(R.id.action_movieListFragment_to_imdbPage, bundle)
            }
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun createQualityDialog(it: Movie) {
        val qualitiesList = it.let { movie -> MovieListRepo.generateQualities(movie) }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Quality")
            .setItems(qualitiesList.toTypedArray()) { _: DialogInterface?, which: Int ->
                val torrent = it.torrents[which]
                lifecycleScope.launch {
                    torrent.let { selectedTorrent ->
                        SharedPreferenceHelper.uploadRequired = true
                        DataBaseHelper.addTorrents(it.id, it.title, selectedTorrent) }
                }
            }

        val dialog = builder.create()
        dialog.show()
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
                lifecycleScope.launch {
                    DataBaseHelper.clearDb()
                }
            }
            //TODO Move this
            R.id.action_rss_url -> {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Rss file url")
                builder.setMessage(generateRssUrl())

                builder.setPositiveButton("Copy") { _, _ ->
                    val clipboard: ClipboardManager? = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    val clip = ClipData.newPlainText("Rss", generateRssUrl())
                    clipboard?.setPrimaryClip(clip)
                }

                builder.setNegativeButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }

        }
        return true
    }

    private fun generateRssUrl(): String {
        return "https://firebasestorage.googleapis.com/v0/b/moviedownloader-9661e.appspot.com/o/${SharedPreferenceHelper.uid}.rss?alt=media"

    }


}