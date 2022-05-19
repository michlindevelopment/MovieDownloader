package com.michlindev.moviedownloader

import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.gms.common.SignInButton
import com.michlindev.moviedownloader.main.BaseAdapter
import com.michlindev.moviedownloader.main.ListAdapterItem


@BindingAdapter("app:onClickSign")
fun SignInButton.bindSignInClick(method: () -> Unit) {
    this.setOnClickListener { method.invoke() }
}

@BindingAdapter("setAdapter")
fun setAdapter(
    recyclerView: RecyclerView,
    adapter: BaseAdapter<ViewDataBinding, ListAdapterItem>?) {
    adapter?.let {
        recyclerView.adapter = it
    }
}

/*@Suppress("UNCHECKED_CAST")
@BindingAdapter("submitList")
fun submitList(recyclerView: RecyclerView, list: List<ListAdapterItem>?) {
    val adapter = recyclerView.adapter as BaseAdapter<ViewDataBinding, ListAdapterItem>?
    adapter?.updateData(list ?: listOf())
}*/

@BindingAdapter("submitList")
fun RecyclerView.submitList(list: List<ListAdapterItem>?) {
    val adapter = this.adapter as BaseAdapter<ViewDataBinding, ListAdapterItem>?
    adapter?.updateData(list ?: listOf())
}

@BindingAdapter("loadWithGlide")
fun ImageView.loadUrlWithGlide(url: String?) {
    val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    Glide.with(this)
        .setDefaultRequestOptions(RequestOptions().placeholder(R.drawable.placeholder))
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .into(this)
}

@BindingAdapter("stringArray")
fun TextView.stringArray(list: List<String>) {
    text = list.joinToString (separator = ", ") { it }
}

@BindingAdapter("selectedValue")
fun Spinner.setSelectedValue(selectedValue: String?) {
    //this.setSelectedValue(selectedValue)
    setSelection(6)
}