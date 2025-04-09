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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryapp.databinding.FragmentAlbumBinding
import com.example.galleryapp.ui.adapter.AlbumAdapter
import com.example.galleryapp.viewmodel.AlbumViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val albumViewModel by viewModels<AlbumViewModel>()
    private lateinit var adapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initObserver()
        albumViewModel.loadAlbums()

    }

    private fun initAdapter() {
        binding.rvAlbum.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = AlbumAdapter { album ->
            navigateToAlbumDetail(album.id)
        }
        binding.rvAlbum.adapter = adapter
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                albumViewModel.albums.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun navigateToAlbumDetail(albumId: Long) {
        val action = AlbumFragmentDirections.actionAlbumFragmentToAlbumDetailFragment(albumId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}