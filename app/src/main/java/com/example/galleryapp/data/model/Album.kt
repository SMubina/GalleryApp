package com.example.galleryapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val id: Long = 0,
    val name: String,
    val uri: Uri,
    var count: Int,
    val pathToThumbNail: String = "",
    val relativePath: String = "",
) : Parcelable