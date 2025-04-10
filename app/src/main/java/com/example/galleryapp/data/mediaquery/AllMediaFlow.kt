package com.example.galleryapp.data.mediaquery

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.example.galleryapp.utils.MediaQueryUtils
import com.example.galleryapp.utils.ext.queryFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class AllMediaFlow(private val context: Context, private val loadAllImages: Boolean = false) :
    MediaFlow(albumId = "", context = context) {

    override fun flowCursor(): Flow<Cursor?> {
        val uri =
            if (loadAllImages) MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) else
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection = MediaQueryUtils.AlbumsProjection
        val sortOrder = MediaQueryUtils.AlbumsSortOrder

        val queryArgs = Bundle().apply {
            putAll(
                bundleOf(
                    ContentResolver.QUERY_ARG_SQL_SORT_ORDER to sortOrder
                )
            )
        }
        return context.contentResolver.queryFlow(
            uri,
            projection,
            queryArgs
        ).flowOn(Dispatchers.IO)
    }
}