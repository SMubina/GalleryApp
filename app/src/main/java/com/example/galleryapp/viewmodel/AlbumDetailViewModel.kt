package com.example.galleryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.model.Media
import com.example.galleryapp.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * detail view model class to fetch the media in album
 */
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(private val repository: MediaRepository) : ViewModel() {


    /**
     * flow to send album media list
     */
    private val _mediaList = MutableStateFlow<List<Media>>(emptyList())
    val mediaList: StateFlow<List<Media>> = _mediaList.asStateFlow()


    /**
     * function to load all media present under the album
     */
    fun loadAlbumMedia(albumId:String) {
        viewModelScope.launch {
            repository.getMediaByAlbumId(albumId).collect {
                _mediaList.value = it
            }
        }
    }

    /**
     * function to load media for "All Images" album
     */
    fun loadAllImages() {
        viewModelScope.launch {
            repository.getAllImages().collect {
                _mediaList.value = it
            }
        }
    }

    /**
     * function to load media for "All Video" album
     */
    fun loadAllVideos() {
        viewModelScope.launch {
            repository.getAllVideos().collect {
                _mediaList.value = it
            }
        }
    }
}