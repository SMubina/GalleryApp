package com.example.galleryapp.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album (
    val name: String,
    val thumbNailUri: Uri,
    val pathToThumbNail:String,
    val relativePath:String,
    val count: Int
) :Parcelable