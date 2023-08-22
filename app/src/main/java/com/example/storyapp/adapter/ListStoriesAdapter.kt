package com.example.storyapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.ListStoryPaging
import com.example.storyapp.R
import com.example.storyapp.databinding.ItemRowStoryBinding
import com.example.storyapp.helper.LocationConverter


class ListStoryAdapter(val context: Context) :
    PagingDataAdapter<ListStoryPaging, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ListViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)

            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(item)
            }
        }
    }

    class ListViewHolder(private var binding: ItemRowStoryBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ListStoryPaging) {
            binding.tvName.text = data.name
            binding.tvLocation.text = LocationConverter.getStringAddress(
                LocationConverter.toLatlng(data.lat, data.lon),
                context
            )
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(binding.imgPhoto)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryPaging)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryPaging>() {
            override fun areItemsTheSame(
                oldItem: ListStoryPaging,
                newItem: ListStoryPaging
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryPaging,
                newItem: ListStoryPaging
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
