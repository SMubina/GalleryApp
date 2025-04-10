package com.example.galleryapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.databinding.FragmentAlbumBinding
import com.example.galleryapp.ui.adapter.AlbumAdapter
import com.example.galleryapp.utils.PermissionHelper
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
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        initAdapter()
        initObserver()
    }

    private fun initAdapter() {
        binding.rvAlbum.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = AlbumAdapter { album ->
            navigateToAlbumDetail(album)
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

    private fun navigateToAlbumDetail(album: Album) {
        val action = AlbumFragmentDirections.actionAlbumFragmentToAlbumDetailFragment(album)
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun requestPermission(){
        permissionHelper = PermissionHelper(
            caller = this,
            activity = requireActivity(),
            lifecycleOwner = viewLifecycleOwner
        ) { granted ->
            if (granted) {
                albumViewModel.loadAlbums()
            } else {
                if (!permissionHelper.shouldShowPermissionRationale()) {
                    showGoToSettingsDialog()
                } else {
                    checkAndRequestPermission()
//                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

        checkAndRequestPermission()
    }

    override fun onResume() {
        super.onResume()
        if (permissionHelper.cameFromSettings) {
            permissionHelper.cameFromSettings = false
            if (permissionHelper.hasMediaPermissions()) {
                albumViewModel.loadAlbums()
            } else {
                Toast.makeText(requireContext(), "Permission still not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestPermission() {
        if (permissionHelper.hasMediaPermissions()) {
            albumViewModel.loadAlbums()
        } else {
            permissionHelper.requestMediaPermissions()
        }
    }

    private fun showGoToSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("Please grant access to media in Settings to continue.")
            .setPositiveButton("Go to Settings") { _, _ ->
                permissionHelper.openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}