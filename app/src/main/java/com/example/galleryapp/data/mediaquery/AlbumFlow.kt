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
import com.example.galleryapp.utils.MediaUtils
import com.example.galleryapp.utils.ext.queryFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.File

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
        ).flowOn(Dispatchers.IO)
    }

    override fun flowData(): Flow<List<Album>> = flowCursor().map { cursor ->
        buildMap<Int, Album> {
            val allImageAlbum = Album(
                id = MediaUtils.ALL_IMAGE_BUCKET_ID.toLong(),
                name = "All Images",
                uri = Uri.EMPTY,
                count = 1
            )
            val allVideoAlbum = Album(
                id = MediaUtils.ALL_VIDEO_BUCKET_ID.toLong(),
                name = "All Videos",
                uri = Uri.EMPTY,
                count = 1
            )
            put(MediaUtils.ALL_IMAGE_BUCKET_ID, allImageAlbum)
            put(MediaUtils.ALL_VIDEO_BUCKET_ID, allVideoAlbum)
            cursor?.use {
                val idIndex = it.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val albumIdIndex = it.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_ID)
                val labelIndex = it.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                val mimeTypeIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val pathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                val relativePathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH)

                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val label = it.getString(labelIndex) ?: Build.MODEL
                    val mimeType = it.getString(mimeTypeIndex).orEmpty()
                    val path = it.getString(pathIndex).orEmpty()
                    val relativePath = it.getString(relativePathIndex).orEmpty()

                    val contentUri = when {
                        mimeType.contains("image") -> {
                            if(isMediaFileAllowed(path)) {allImageAlbum.count += 1}
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }

                        mimeType.contains("video") -> {
                            allVideoAlbum.count+=1
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        else -> continue
                    }

                    val bucketId = it.getInt(albumIdIndex)
                    val album = get(bucketId)
                    if (album != null) {
                        album.count += 1
                    } else {
                        put(
                            bucketId, Album(
                                id = it.getLong(albumIdIndex),
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
        val excludedPaths = listOf("/cache", "/.thumbnails", "/temp", "/.trash", "/snapchat", "/whatsapp/.shared")
        if (excludedPaths.any { it in lowerPath }) return false else return true

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