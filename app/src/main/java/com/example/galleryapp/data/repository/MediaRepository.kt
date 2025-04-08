package com.example.galleryapp.data.repository

import com.example.galleryapp.data.Album
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun getAlbums(): Flow<List<Album>>

}