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

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(private val repository: MediaRepository) : ViewModel() {

    private val _mediaList = MutableStateFlow<List<Media>>(emptyList())
    val mediaList: StateFlow<List<Media>> = _mediaList.asStateFlow()

    fun loadAlbumDetail(albumId:String) {
        viewModelScope.launch {
            repository.getMediaByAlbumId(albumId).collect {
                _mediaList.value = it
            }
        }
    }
}