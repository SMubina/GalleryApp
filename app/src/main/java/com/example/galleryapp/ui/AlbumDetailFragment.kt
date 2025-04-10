package com.example.galleryapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryapp.databinding.FragmentAlbumDetailBinding
import com.example.galleryapp.ui.adapter.AlbumDetailAdapter
import com.example.galleryapp.utils.MediaQueryUtils
import com.example.galleryapp.viewmodel.AlbumDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {

    private var _binding: FragmentAlbumDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val args: AlbumDetailFragmentArgs by navArgs()
    private val albumDetailViewModel by viewModels<AlbumDetailViewModel>()
    private lateinit var adapter: AlbumDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val album = args.album
        initAdapter()
        initObserver()
        loadData(album.id)
    }

    private fun loadData(albumId: Long) {
        when (albumId.toInt()) {
            MediaQueryUtils.ALL_IMAGE_BUCKET_ID -> {
                albumDetailViewModel.loadAllImages()
            }
            MediaQueryUtils.ALL_VIDEO_BUCKET_ID -> {
                albumDetailViewModel.loadAllVideos()
            }
            else -> {
                albumDetailViewModel.loadAlbumMedia(albumId = albumId.toString())
            }
        }
    }

    private fun initAdapter() {
        binding.rvAlbumMedia.layoutManager = GridLayoutManager(requireContext(), 4)
        adapter = AlbumDetailAdapter {
            //TODO can define further events here like image display for editing and video play and many more....
        }
        binding.rvAlbumMedia.adapter = adapter
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                albumDetailViewModel.mediaList.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}