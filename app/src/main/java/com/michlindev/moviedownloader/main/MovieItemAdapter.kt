package com.michlindev.moviedownloader.main

import android.view.View
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.databinding.ListLayoutNewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//Connected item adapter for each item
class MovieItemAdapter(list: List<Movie>, private val itemListener: ItemListener) : BaseAdapter<ListLayoutNewBinding, Movie>(list) {

    override val layoutId: Int = R.layout.list_layout_new

    override fun bind(binding: ListLayoutNewBinding, item: Movie) {
        binding.apply {
            movie = item
            listener = itemListener
            DLog.d("Trig: ${item.title} Rt: ${item.realRating}")

            //TODO maybe item should pull data here
            if (item.realRating.isNullOrEmpty())
                itemListener.imdbLogoClick(item)
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