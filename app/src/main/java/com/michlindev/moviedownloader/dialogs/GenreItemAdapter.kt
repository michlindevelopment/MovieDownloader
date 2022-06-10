package com.michlindev.moviedownloader.dialogs

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

interface ItemListener