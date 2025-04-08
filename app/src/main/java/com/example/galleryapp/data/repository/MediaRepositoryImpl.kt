package com.example.galleryapp.data.repository

import android.content.Context
import com.example.galleryapp.data.Album
import kotlinx.coroutines.flow.Flow

class MediaRepositoryImpl(
    private val context: Context
) : MediaRepository {

    private val contentResolver = context.contentResolver


    override fun getAlbums(): Flow<List<Album>> {
        TODO("Not yet implemented")
    }
}