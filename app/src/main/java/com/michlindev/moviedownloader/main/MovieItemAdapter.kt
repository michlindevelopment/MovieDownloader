package com.michlindev.moviedownloader.main

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.animations.Animations
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

            imageView3.setOnClickListener {
                item.expanded = toggleLayout(!item.expanded,it,binding.linearLayout2)
            }
        }

    }
}

private fun toggleLayout(isExpanded: Boolean, v: View, layoutExpand: LinearLayout): Boolean {
    Log.d("DTAG","isExpanded: $isExpanded")
    Animations.toggleArrow(v, isExpanded)
    if (isExpanded) {
        Animations.expand(layoutExpand)
    } else {
        Animations.collapse(layoutExpand)
    }
    return isExpanded
}

interface ItemListener {
    fun downloadClick(item: Movie)
    fun imdbLogoClick(item: Movie)
    fun posterClick(item: String)
}