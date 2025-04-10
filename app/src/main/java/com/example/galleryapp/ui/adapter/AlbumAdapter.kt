package com.example.galleryapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryapp.R
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.databinding.ItemGridBinding
import com.example.galleryapp.databinding.ItemListBinding
import com.example.galleryapp.utils.ext.loadImage

class AlbumAdapter(private var isGrid: Boolean, private val onItemClick: (Album) -> Unit) :
    ListAdapter<Album, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        const val VIEW_TYPE_GRID = 0
        const val VIEW_TYPE_LIST = 1
    }

    fun toggleLayout(newIsGrid: Boolean) {
        if (isGrid != newIsGrid) {
            isGrid = newIsGrid
            // Rebind views with same list but different layout
            submitList(currentList.toList())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGrid) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GRID) {
            val binding =
                ItemGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GridViewHolder(binding, onItemClick)
        } else {
            val binding =
                ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ListViewHolder(binding, onItemClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is GridViewHolder -> holder.bind(item)
            is ListViewHolder -> holder.bind(item)
        }
    }

    class GridViewHolder(
        private val binding: ItemGridBinding,
        private val onItemClick: (Album) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album) {
            binding.tvAlbumName.text = album.name
            binding.tvAlbumCount.text =
                "${album.count} ${binding.root.context.getString(R.string.str_items)}"
            binding.ivAlbumThumbNail.loadImage(album.uri)
            binding.root.setOnClickListener { onItemClick(album) }
        }
    }

    class ListViewHolder(
        private val binding: ItemListBinding,
        private val onItemClick: (Album) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album) {
            binding.tvAlbumName.text = album.name
            binding.tvAlbumCount.text =
                "${album.count} ${binding.root.context.getString(R.string.str_items)}"
            binding.ivAlbumThumbNail.loadImage(album.uri)
            binding.root.setOnClickListener { onItemClick(album) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.uri == newItem.uri
        override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
    }
}
