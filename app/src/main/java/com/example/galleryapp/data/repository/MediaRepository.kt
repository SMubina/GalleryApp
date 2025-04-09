package com.example.galleryapp.data.repository

import com.example.galleryapp.data.model.Album
import com.example.galleryapp.data.model.Media
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun getAlbums(): Flow<List<Album>>

    fun getMediaByAlbumId(albumId:String): Flow<List<Media>>

}