package com.example.galleryapp.utils

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore


/**
 * this class defines the mime type for image and video
 */
object MediaUtils {

    /**
     * this represents the uri path for media
     */
    val MediaFileUri: Uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)


    /**
     * selection describes the query to get only image and video using query
     */
    val AlbumsSelection: String = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR " +
            "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"

    /**
     * selection describes the query to get image and video inside particular album folder
     */
    val AlbumMediaSelection: String = "${MediaStore.Files.FileColumns.BUCKET_ID} = ? AND " +
            "(${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?)"

    /**
     * This represents the query selection args
     */
    val AlbumsSelectionArgs =  arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )

    /**
     * This defines the sort order for the fetch data
     */
    val AlbumsSortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"

    /**
     * projection describes the kind of column table data we want to read for media
     */
    val AlbumsProjection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.RELATIVE_PATH,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.BUCKET_ID,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Files.FileColumns.DATE_TAKEN,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MIME_TYPE,
    )

    private const val MIME_TYPE_IMAGE_ANY = "image/*"
    private const val MIME_TYPE_VIDEO_ANY = "video/*"
    private const val MIME_TYPE_ANY = "*/*"

    fun getMimeType(intent: Intent?) = when (intent?.action) {
        Intent.ACTION_SET_WALLPAPER -> MIME_TYPE_IMAGE_ANY
        else -> (intent?.type ?: MIME_TYPE_ANY).let {
            when (it) {
                MediaStore.Images.Media.CONTENT_TYPE -> MIME_TYPE_IMAGE_ANY
                MediaStore.Video.Media.CONTENT_TYPE -> MIME_TYPE_VIDEO_ANY
                else -> when {
                    it == MIME_TYPE_ANY
                            || it.startsWith("image/")
                            || it.startsWith("video/") -> it

                    else -> null
                }
            }
        }
    }

    fun getMediaTypeFromMimeType(mimeType: String?) = when (mimeType) {
        MIME_TYPE_IMAGE_ANY -> MediaType.IMAGE
        MIME_TYPE_VIDEO_ANY -> MediaType.VIDEO
        else -> null
    }


}