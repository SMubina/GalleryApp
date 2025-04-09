package com.example.galleryapp.data.mediaquery

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.utils.MediaUtils
import com.example.galleryapp.utils.ext.queryFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AlbumFlow(private val context: Context) : QueryFlow<Album>() {


    override fun flowCursor(): Flow<Cursor?> {
        val uri = MediaUtils.MediaFileUri
        val projection = MediaUtils.AlbumsProjection
        val selection = MediaUtils.AlbumsSelection
        val selectionArgs = MediaUtils.AlbumsSelectionArgs
        val sortOrder = MediaUtils.AlbumsSortOrder

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
        )
    }

    override fun flowData(): Flow<List<Album>> = flowCursor().map { cursor ->
        buildMap<Int, Album> {
            cursor?.use {
                val idIndex = it.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val albumIdIndex = it.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_ID)
                val labelIndex = it.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                val pathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                val relativePathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH)
                val mimeTypeIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                while (it.moveToNext()) {
                    val bucketId = it.getInt(albumIdIndex)
                    val album = get(bucketId)
                    if (album != null) {
                        album.count += 1
                        continue
                    }
                    val id = it.getLong(idIndex)
                    val label = it.getString(labelIndex) ?: Build.MODEL
                    val path = it.getString(pathIndex).orEmpty()
                    val relativePath = it.getString(relativePathIndex).orEmpty()
                    val mimeType = it.getString(mimeTypeIndex).orEmpty()

                    val contentUri = when {
                        mimeType.contains("image") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        mimeType.contains("video") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        else -> continue
                    }

                    put(bucketId, Album(
                        id = it.getLong(albumIdIndex),
                        name = label,
                        uri = ContentUris.withAppendedId(contentUri, id),
                        pathToThumbNail = path,
                        relativePath = relativePath,
                        count = 1
                    ))
                }
            }
        }.values.toList()
    }.flowOn(Dispatchers.IO)

}