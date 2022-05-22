package com.michlindev.moviedownloader.main

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.FileManager
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.MovieListFragmentBinding

class MovieListFragment : Fragment() {

    private val viewModel: MovieListViewModel by activityViewModels()
    lateinit var rssDataList: List<String>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = MovieListFragmentBinding.inflate(layoutInflater)

        rssDataList = FileManager.readFromRssFile()

        binding.adapter = MovieItemAdapter(listOf(), viewModel)
        viewModel.itemList.observe(viewLifecycleOwner) {
            binding.adapter?.notifyDataSetChanged()
        }

        viewModel.notifyAdapter.observe(viewLifecycleOwner) {
            it?.let { binding.adapter?.notifyItemChanged(it) }
        }

        viewModel.qualitySelectionDialog.observe(viewLifecycleOwner) { it ->

            val qualitiesList = mutableListOf<String>()
            it?.torrents?.forEach { torrent ->
                when (torrent.quality) {
                    "bluray" -> qualitiesList.add("BluRay")
                    "web" -> qualitiesList.add("Web")
                }
            }


            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Select Quality")
                .setItems(qualitiesList.toTypedArray()) { _: DialogInterface?, which: Int ->
                    val link = it?.torrents?.get(which)?.url
                    (rssDataList as MutableList<String>).add(rssDataList.size - 2, "<item>")
                    (rssDataList as MutableList<String>).add(rssDataList.size - 2, "<title>")
                    it?.title?.let { it1 -> (rssDataList as MutableList<String>).add(rssDataList.size - 2, it1) }
                    (rssDataList as MutableList<String>).add(rssDataList.size - 2, "</title>")
                    (rssDataList as MutableList<String>).add(rssDataList.size - 2, "<enclosure url=\"$link\" type=\"application/x-bittorrent\" length=\"10000\"/>")
                    (rssDataList as MutableList<String>).add(rssDataList.size - 2, "</item>")
                    FileManager.writeToRssFile(rssDataList)

                }

            val dialog = builder.create()
            dialog.show()

        }






    viewModel.imdbClick.observe(viewLifecycleOwner)
    {
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