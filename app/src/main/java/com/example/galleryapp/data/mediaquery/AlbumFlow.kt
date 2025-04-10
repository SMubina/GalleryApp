package com.example.galleryapp.data.mediaquery

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.utils.MediaQueryUtils
import com.example.galleryapp.utils.ext.queryFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * This class is responsible for fetching all the album folder present
 */
class AlbumFlow(private val context: Context) : QueryFlow<Album>() {

    /**
     * this fun builds the query to fetch the albums data
     * basically fetches all the media and video present on the system
     * returns flow of cursor data
     */
    override fun flowCursor(): Flow<Cursor?> {
        val uri = MediaQueryUtils.MediaFileUri
        val projection = MediaQueryUtils.AlbumsProjection
        val selection = MediaQueryUtils.AlbumsSelection
        val selectionArgs = MediaQueryUtils.AlbumsSelectionArgs
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
        /**
         * This build the content resolver query and fetch the flow of cursor data.
         */
        return context.contentResolver.queryFlow(
            uri,
            projection,
            queryArgs
        ).flowOn(Dispatchers.IO)
    }

    /**
     * This function is responsible for doing various operations on the flow of cursor
     * mainly categorise media files into album folders
     * here we are adding "All Images" and " All Videos" manually
     * as these album folders are not present onto system so need append it
     * returns flow of data responsible to show to data in UI
     */
    override fun flowData(): Flow<List<Album>> = flowCursor().map { cursor ->
        buildMap<Int, Album> {
            val allImageAlbum = Album(
                id = MediaQueryUtils.ALL_IMAGE_BUCKET_ID.toLong(),
                name = "All Images",
                uri = Uri.EMPTY
            )
            val allVideoAlbum = Album(
                id = MediaQueryUtils.ALL_VIDEO_BUCKET_ID.toLong(),
                name = "All Videos",
                uri = Uri.EMPTY
            )
            put(MediaQueryUtils.ALL_IMAGE_BUCKET_ID, allImageAlbum)
            put(MediaQueryUtils.ALL_VIDEO_BUCKET_ID, allVideoAlbum)
            cursor?.use {
                val idIndex = it.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val albumIdIndex = it.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_ID)
                val labelIndex = it.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                val mimeTypeIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val pathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
5
                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val bucketId = it.getInt(albumIdIndex)
                    val label = it.getString(labelIndex) ?: Build.MODEL
                    val mimeType = it.getString(mimeTypeIndex).orEmpty()
                    val path = it.getString(pathIndex).orEmpty()

                    val isImage = mimeType.contains("image")
                    val isVideo = mimeType.contains("video")
                    val contentUri = when {
                        isImage && isMediaFileAllowed(path) -> {
                            /**
                             * this will increase the media count for "All Images"
                             * append the first image Uri
                             */
                            allImageAlbum.apply {
                                count++
                                if (count == 1) uri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                            }
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }

                        isVideo -> {
                            /**
                             * this will increase the media count for "All Videos"
                             * append the first image Uri
                             */
                            allVideoAlbum.apply {
                                count++
                                if (count == 1) uri = ContentUris.withAppendedId(
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                            }
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }

                        else -> continue
                    }
                    val existingAlbum = get(bucketId)
                    if (existingAlbum != null) {
                        existingAlbum.count++
                    } else {
                        put(
                            bucketId, Album(
                                id = bucketId.toLong(),
                                name = label,
                                uri = ContentUris.withAppendedId(contentUri, id),
                                count = 1
                            )
                        )
                    }
                }
            }
        }.values.toList()
    }.flowOn(Dispatchers.IO)


    /**
     * This function will exclude the images present on the below paths.
     * All Images folder does not contain images present at below paths
     */
    private fun isMediaFileAllowed(path: String?): Boolean {
        if (path.isNullOrBlank()) return false
        val lowerPath = path.lowercase()
        // Exclude known non-user media folders
        val excludedPaths =
            listOf("/cache", "/.thumbnails", "/temp", "/.trash", "/snapchat", "/whatsapp/.shared")
        return !excludedPaths.any { it in lowerPath }
    }

}