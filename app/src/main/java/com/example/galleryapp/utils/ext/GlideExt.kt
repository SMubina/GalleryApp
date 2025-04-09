package com.example.galleryapp.utils.ext


import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(imageUri: Uri) {
    Glide.with(context)
        .load(imageUri)
        .centerCrop()
        .into(this)
}
