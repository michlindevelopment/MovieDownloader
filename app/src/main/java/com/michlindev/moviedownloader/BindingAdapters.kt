package com.michlindev.moviedownloader

import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
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


@BindingAdapter("onClickSign")
fun SignInButton.bindSignInClick(method: () -> Unit) {
    this.setOnClickListener { method.invoke() }
}

@BindingAdapter("setAdapter")
fun setAdapter(
    recyclerView: RecyclerView,
    adapter: BaseAdapter<ViewDataBinding, ListAdapterItem>?
) {
    adapter?.let {
        recyclerView.adapter = it
    }
}

@Suppress("UNCHECKED_CAST")
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
    text = list.joinToString(separator = ", ") { it }
}

@BindingAdapter("htmlString")
fun TextView.htmlString(htmlText: String?) {
    this.text = htmlText?.let {
        Html.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    } ?: ""


}

@BindingAdapter("visibilityGone")
fun setVisibility(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}

/*@BindingAdapter("textChangedListener")
fun bindTextWatcher(editText: EditText, textWatcher: TextWatcher) {
    editText.addTextChangedListener(textWatcher)
}*/
