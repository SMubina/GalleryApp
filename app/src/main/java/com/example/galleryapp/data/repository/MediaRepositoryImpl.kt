package com.example.galleryapp.data.repository

import android.content.Context
import com.example.galleryapp.data.mediaquery.AlbumFlow
import com.example.galleryapp.data.mediaquery.AllMediaFlow
import com.example.galleryapp.data.mediaquery.MediaFlow
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.data.model.Media
import kotlinx.coroutines.flow.Flow
/**
 * repository class to get the flow data of album and its media
 * particularly uses QueryFlow to fetch the required data
 */
class MediaRepositoryImpl(
    private val context: Context
) : MediaRepository {

    override fun getAlbums(): Flow<List<Album>> = AlbumFlow(context = context).flowData()

    override fun getMediaByAlbumId(albumId: String): Flow<List<Media>> =
        MediaFlow(context = context, albumId = albumId).flowData()

    override fun getAllImages(): Flow<List<Media>>  = AllMediaFlow(context = context, loadAllImages = true).flowData()

    override fun getAllVideos(): Flow<List<Media>> = AllMediaFlow(context = context).flowData()
}