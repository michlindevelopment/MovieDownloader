package com.michlindev.moviedownloader.main

import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.databinding.ItemListMovieBinding


//Connected item adapter for each item
class MovieItemAdapter(list: List<Movie>, private val itemListener: ItemListener) : BaseAdapter<ItemListMovieBinding, Movie>(list) {

    override val layoutId: Int = R.layout.item_list_movie

    override fun bind(binding: ItemListMovieBinding, item: Movie) {
        binding.apply {
            movie = item
            listener = itemListener
            executePendingBindings()
        }
    }
}

interface ItemListener {
    fun downloadClick(item: Movie)
    fun imdbLogoClick(item: Movie)
    fun posterClick(item: String)
}