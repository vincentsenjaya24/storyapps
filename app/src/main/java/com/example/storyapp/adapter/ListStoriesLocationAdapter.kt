package com.example.storyapp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.ListStory
import com.example.storyapp.R
import com.example.storyapp.databinding.ItemRowStoryMapsBinding
import com.example.storyapp.helper.LocationConverter
import com.google.android.gms.maps.model.LatLng


class ListStoryLocationAdapter(private val listStory: List<ListStory>) :
    RecyclerView.Adapter<ListStoryLocationAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryMapsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[holder.bindingAdapterPosition])
        }
    }

    class ListViewHolder(private var binding: ItemRowStoryMapsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ListStory) {
            val latLng: LatLng? = LocationConverter.toLatlng(data.lat, data.lon)
            binding.iconLocationAvailable.visibility =
                if (latLng != null) View.VISIBLE else View.GONE
            binding.tvName.text = data.name
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(binding.imgPhoto)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStory)
    }

    override fun getItemCount(): Int = listStory.size
}
