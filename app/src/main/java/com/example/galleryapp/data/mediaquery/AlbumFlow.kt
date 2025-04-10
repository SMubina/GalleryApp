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

class AlbumFlow(private val context: Context) : QueryFlow<Album>() {

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
        return context.contentResolver.queryFlow(
            uri,
            projection,
            queryArgs
        ).flowOn(Dispatchers.IO)
    }

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
                val relativePathIndex =
                    it.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH)

                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val bucketId = it.getInt(albumIdIndex)
                    val label = it.getString(labelIndex) ?: Build.MODEL
                    val mimeType = it.getString(mimeTypeIndex).orEmpty()
                    val path = it.getString(pathIndex).orEmpty()
                    val relativePath = it.getString(relativePathIndex).orEmpty()

                    val isImage = mimeType.contains("image")
                    val isVideo = mimeType.contains("video")
                    val contentUri = when {
                        isImage && isMediaFileAllowed(path) -> {
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


    private fun isMediaFileAllowed(path: String?): Boolean {
        if (path.isNullOrBlank()) return false

        val lowerPath = path.lowercase()

        // Exclude known non-user media folders
        val excludedPaths =
            listOf("/cache", "/.thumbnails", "/temp", "/.trash", "/snapchat", "/whatsapp/.shared")
        if (excludedPaths.any { it in lowerPath }) return false else return true


        //TODO below logic gives error need to find out the exact root cause

//        return try {
//            val parentDir = File(path).parentFile
//            if (parentDir == null || !parentDir.exists() || !parentDir.isDirectory) return true
//
//            val files = parentDir.listFiles() ?: return true // If listing fails, assume it's okay
//            files.none { it.name.equals(".nomedia", ignoreCase = true) }
//        } catch (e: SecurityException) {
//            true // Permission issues? Don’t block the file
//        } catch (e: Exception) {
//            true // Fail-safe in any unexpected case
//        }
    }


}