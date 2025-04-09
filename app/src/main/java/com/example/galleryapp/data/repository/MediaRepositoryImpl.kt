package com.example.galleryapp.data.repository

import android.content.Context
import com.example.galleryapp.data.mediaquery.AlbumFlow
import com.example.galleryapp.data.model.Album
import kotlinx.coroutines.flow.Flow


class MediaRepositoryImpl(
    private val context: Context
) : MediaRepository {

    override fun getAlbums(): Flow<List<Album>>  = AlbumFlow(context = context).flowData()
}