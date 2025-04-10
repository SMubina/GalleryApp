package com.example.galleryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(private val repository: MediaRepository) : ViewModel() {


    /**
     * state flow to maintain state between grid and list layout of albums
     */
    private val _isGrid = MutableStateFlow(true)
    val isGrid: StateFlow<Boolean> = _isGrid

    fun toggleLayout() {
        _isGrid.value = !_isGrid.value
    }

    /**
     * state to load items into grid view
     */
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    fun loadAlbums() {
        viewModelScope.launch {
            repository.getAlbums().collect {
                _albums.value = it
            }
        }
    }
}