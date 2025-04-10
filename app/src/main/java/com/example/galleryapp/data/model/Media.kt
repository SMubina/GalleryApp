package com.example.galleryapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media (
    val id: Long = 0,
    val uri: Uri,
    val pathToThumbNail:String,
    val relativePath:String,
    var albumID: Long,
    var albumName: String,
    var isVideo: Boolean = false
) :Parcelable