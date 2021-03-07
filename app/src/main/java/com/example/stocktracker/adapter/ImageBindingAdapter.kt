package com.example.stocktracker.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object ImageBindingAdapter {
    @BindingAdapter("bind:imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String) {
        if (url != "") {
            Picasso.get().load(url).into(imageView)
        }
    }
}