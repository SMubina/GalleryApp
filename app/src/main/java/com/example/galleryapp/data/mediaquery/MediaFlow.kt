package com.example.galleryapp.data.mediaquery

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.example.galleryapp.data.model.Media
import com.example.galleryapp.utils.MediaQueryUtils
import com.example.galleryapp.utils.ext.queryFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

open class MediaFlow(private val context: Context, private val albumId:String) : QueryFlow<Media>() {

    override fun flowCursor(): Flow<Cursor?> {
        val uri = MediaQueryUtils.MediaFileUri
        val projection = MediaQueryUtils.AlbumsProjection
        val selection = MediaQueryUtils.AlbumMediaSelection
        val selectionArgs = arrayOf(albumId,
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
        val sortOrder = MediaQueryUtils.AlbumsSortOrder

        val queryArgs = Bundle().apply {
            putAll(
                bundleOf(
                    ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to selectionArgs,
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

    override fun flowData(): Flow<List<Media>> = flowCursor().map { cursor ->
        buildList { cursor?.use {
                val idIndex = it.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val albumIdIndex = it.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_ID)
                val albumNameIndex = it.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                val pathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                val relativePathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH)
                val mimeTypeIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val albumId =  it.getLong(albumIdIndex)
                    val albumName = it.getString(albumNameIndex) ?: Build.MODEL
                    val path = it.getString(pathIndex).orEmpty()
                    val relativePath = it.getString(relativePathIndex).orEmpty()
                    val mimeType = it.getString(mimeTypeIndex).orEmpty()
                    var isVideo = false
                    val contentUri = when {
                        mimeType.contains("image") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        mimeType.contains("video") -> {
                            isVideo = true
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }

                        else -> continue
                    }
                    add(Media(
                        id = id,
                        uri = ContentUris.withAppendedId(contentUri, id),
                        pathToThumbNail = path,
                        relativePath = relativePath,
                        albumID = albumId,
                        albumName = albumName,
                        isVideo = isVideo
                    ))
                }
            }
        }.toList()
    }.flowOn(Dispatchers.IO)

}