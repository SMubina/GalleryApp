package com.example.galleryapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryapp.data.model.Album
import com.example.galleryapp.databinding.ItemAlbumBinding
import com.example.galleryapp.utils.ext.loadImage

class AlbumAdapter(
    private val onClick: (Album) -> Unit
) : ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(AlbumDiffCallback()) {

    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.tvAlbumName.text = album.name
            binding.tvAlbumCount.text = "${album.count}"

            binding.ivAlbumThumbNail.loadImage(album.uri)

            binding.root.setOnClickListener {
                onClick(album)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean =
            oldItem == newItem
    }
}
