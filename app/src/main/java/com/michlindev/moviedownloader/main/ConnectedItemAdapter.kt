package com.michlindev.moviedownloader.main

import android.view.View
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.databinding.ListLayoutBinding


//Connected item adapter for each item
class ConnectedItemAdapter(list: List<Movie>, private val itemListener: ItemListener) : BaseAdapter<ListLayoutBinding, Movie>(list) {

    override val layoutId: Int = R.layout.list_layout

    override fun bind(binding: ListLayoutBinding, item: Movie) {
        binding.apply {
            connectedItem = item
            listener = itemListener
            executePendingBindings()
        }
    }
}

interface ItemListener {
    fun onItemCheckedClicked(item: Movie, view: View)
    fun zoomImage(view: View, image: Int)
    fun infoImage(item: Movie)
}