package com.example.galleryapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * model class for album
 */
@Parcelize
data class Album(
    val id: Long = 0,
    val name: String,
    var uri: Uri,
    var count: Int = 0
) : Parcelable