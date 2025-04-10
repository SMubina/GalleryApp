package com.example.galleryapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.galleryapp.R
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.databinding.FragmentAlbumBinding
import com.example.galleryapp.ui.adapter.AlbumAdapter
import com.example.galleryapp.utils.PermissionHelper
import com.example.galleryapp.viewmodel.AlbumViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment class responsible for showing all the album present under the file system
 */
@AndroidEntryPoint
class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val albumViewModel by viewModels<AlbumViewModel>()
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var albumAdapter: AlbumAdapter

    // Define grid span count constant
    private val GRID_SPAN_COUNT = 3

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
        setupMenu()
        initGalleryAdapter()
        initObserver()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                albumViewModel.albums.collect {
                    albumAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                albumViewModel.isGrid.collect { isGrid ->
                    binding.rvAlbum.layoutManager =
                        if (isGrid) GridLayoutManager(context, GRID_SPAN_COUNT) else LinearLayoutManager(context)
                    albumAdapter.toggleLayout(isGrid)
                }
            }
        }
    }

    //region to set up the album adapter and navigation to detail

    /**
     * initializes the gallery adapter
     */
    private fun initGalleryAdapter() {
        albumAdapter = AlbumAdapter(isGrid = true) { album ->
            navigateToAlbumDetail(album)
        }
        binding.rvAlbum.adapter = albumAdapter
    }

    /**
     * navigate to album detail
     */
    private fun navigateToAlbumDetail(album: Album) {
        val action = AlbumFragmentDirections.actionAlbumFragmentToAlbumDetailFragment(album)
        findNavController().navigate(action)
    }

    //endregion

    //region for the menu setup to switch between grid and list view

    /**
     * this set up the menu to show the grid and list icons and its click event
     * uses Menu Provider
     */
    private fun setupMenu() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.album_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_toggle_layout -> {
                        albumViewModel.toggleLayout()
                        requireActivity().invalidateOptionsMenu() // Update icon
                        true
                    }
                    else -> false
                }
            }
            override fun onPrepareMenu(menu: Menu) {
                updateIcon(menu)
            }
        }, viewLifecycleOwner,Lifecycle.State.RESUMED)
    }

    /**
     * this updates the icon for list and grid
     */
    private fun updateIcon(menu: Menu) {
        val item = menu.findItem(R.id.action_toggle_layout)
        val isGrid = albumViewModel.isGrid.value
        item.icon = ContextCompat.getDrawable(
            requireContext(),
            if (isGrid) R.drawable.ic_grid_view else R.drawable.ic_list_view
        )
    }

    //endregion

    // region Ask for runtime permissions
    private fun requestPermission(){
        permissionHelper = PermissionHelper(
            caller = this,
            activity = requireActivity()
        ) { granted ->
            if (granted) {
                albumViewModel.loadAlbums()
            } else {
                if (!permissionHelper.shouldShowPermissionRationale()) {
                    showGoToSettingsDialog()
                } else {
                    checkAndRequestPermission()
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
                Toast.makeText(requireContext(), getString(R.string.str_permission_not_granted), Toast.LENGTH_SHORT).show()
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
            .setTitle(getString(R.string.str_permission_require))
            .setMessage(getString(R.string.str_grant_access))
            .setPositiveButton(getString(R.string.str_go_to_settings)) { _, _ ->
                permissionHelper.openAppSettings()
            }
            .setNegativeButton(getString(R.string.str_cancel), null)
            .show()
    }

    //endregion

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}