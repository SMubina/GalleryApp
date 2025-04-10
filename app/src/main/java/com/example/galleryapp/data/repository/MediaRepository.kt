package com.example.galleryapp.data.repository

import com.example.galleryapp.data.model.Album
import com.example.galleryapp.data.model.Media
import kotlinx.coroutines.flow.Flow

/**
 * media repository interface
 */
interface MediaRepository {

    fun getAlbums(): Flow<List<Album>>

    fun getMediaByAlbumId(albumId:String): Flow<List<Media>>

    fun getAllImages(): Flow<List<Media>>

    fun getAllVideos(): Flow<List<Media>>

}