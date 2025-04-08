package com.example.galleryapp.utils

import android.net.Uri
import android.provider.MediaStore


/**
 * This class represent the type of Media
 * @param externalContentUri
 * @param mediaStoreType
 */
enum class MediaType(
    val externalContentUri: Uri,
    val mediaStoreType:Int
) {
    IMAGE(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
    ),
    VIDEO(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
    )

}