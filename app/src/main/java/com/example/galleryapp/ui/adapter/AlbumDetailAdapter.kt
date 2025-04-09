package com.example.galleryapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.galleryapp.data.model.Media
import com.example.galleryapp.databinding.ItemAlbumDetailBinding

class AlbumDetailAdapter(private val onClick: () -> Unit) :
    ListAdapter<Media, AlbumDetailAdapter.AlbumDetailViewHolder>(AlbumDetailDiffCallback()) {

    inner class AlbumDetailViewHolder(private val binding: ItemAlbumDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Media) {

            // Load thumbnail using Glide or Coil (Glide here)
            Glide.with(binding.ivMediaThumbNail.context)
                .load(album.uri)
                .centerCrop()
                .into(binding.ivMediaThumbNail)

            binding.root.setOnClickListener {
                onClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumDetailViewHolder {
        val binding =
            ItemAlbumDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlbumDetailDiffCallback : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean =
            oldItem == newItem
    }
}
