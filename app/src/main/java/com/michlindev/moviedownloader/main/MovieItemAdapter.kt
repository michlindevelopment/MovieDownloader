package com.michlindev.moviedownloader.main

import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.databinding.ListLayoutNewBinding


//Connected item adapter for each item
class MovieItemAdapter(list: List<Movie>, private val itemListener: ItemListener) : BaseAdapter<ListLayoutNewBinding, Movie>(list) {

    override val layoutId: Int = R.layout.list_layout_new

    override fun bind(binding: ListLayoutNewBinding, item: Movie) {
        binding.apply {
            movie = item
            listener = itemListener
            executePendingBindings()
        }
    }
}

interface ItemListener {
   /* fun onItemCheckedClicked(item: Movie, view: View)
    fun zoomImage(view: View, image: Int)
    fun infoImage(item: Movie)*/
    fun imdbLogoClick(item: Movie)
    fun posterClick(item: String)
}