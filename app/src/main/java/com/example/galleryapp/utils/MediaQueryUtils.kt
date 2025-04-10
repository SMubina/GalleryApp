package com.example.galleryapp.utils

import android.net.Uri
import android.provider.MediaStore


/**
 * this class defines the required content resolver query params
 *
 * projection
 * selection
 * selection arguments
 * sorting order
 */
object MediaQueryUtils {

    /**
     * defining static bucket id as we are adding "All Images" and "All Videos"
     * folder manually not present inside device
     */
    const val ALL_IMAGE_BUCKET_ID = -1
    const val ALL_VIDEO_BUCKET_ID = -2

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

    /**
     * this represents the uri path for media
     */
    val MediaFileUri: Uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)


    /**
     * selection describes the query to get the albums data contain only image and video
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



}