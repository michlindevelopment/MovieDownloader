package com.michlindev.moviedownloader.dialog

import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.databinding.ItemListGenreBinding
import com.michlindev.moviedownloader.main.BaseAdapter


//Connected item adapter for each item
class GenreItemAdapter(list: List<Genre>, private val itemListener: ItemListener) : BaseAdapter<ItemListGenreBinding, Genre>(list) {

    override val layoutId: Int = R.layout.item_list_genre

    override fun bind(binding: ItemListGenreBinding, item: Genre) {
        binding.apply {
            genre = item
            listener = itemListener
            executePendingBindings()
        }
    }


}

interface ItemListener {
    //fun onItemCheckedClicked(item: Movie, view: View)
    //fun zoomImage(view: View, image: Int)
    //fun infoImage(item: Movie)
    //fun rating(item: String)
}