package com.example.galleryapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryapp.R
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.data.model.Media
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
    private val binding get() = _binding!!


    private val args: AlbumDetailFragmentArgs by navArgs()
    private val albumDetailViewModel by viewModels<AlbumDetailViewModel>()
    private lateinit var adapter: AlbumDetailAdapter
    private lateinit var album: Album

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        album = args.album
        initToolBar()
        initAdapter()
        initObserver()
        loadData()
    }

    private fun initToolBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = album.name
        binding.toolbar.subtitle = "${album.count} ".plus(getString(R.string.str_items))
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadData() {
        when (album.id.toInt()) {
            MediaQueryUtils.ALL_IMAGE_BUCKET_ID -> {
                albumDetailViewModel.loadAllImages()
            }

            MediaQueryUtils.ALL_VIDEO_BUCKET_ID -> {
                albumDetailViewModel.loadAllVideos()
            }

            else -> {
                albumDetailViewModel.loadAlbumMedia(albumId = album.id.toString())
            }
        }
    }

    private fun initAdapter() {
        binding.rvAlbumMedia.layoutManager = GridLayoutManager(requireContext(), 4)
        adapter = AlbumDetailAdapter {
         openMedia(it)
        }
        binding.rvAlbumMedia.adapter = adapter
    }

    /**
     * function to open respective media in external application
     */
    private fun openMedia(media: Media) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(media.uri, if (media.isVideo) "video/*" else "image/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No app found to open media", Toast.LENGTH_SHORT).show()
        }
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