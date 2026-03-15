package com.presentation.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.domain.model.Show
import com.presentation.R
import com.presentation.databinding.ItemShowBinding
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy

data class ShowUIModel(
    val show: Show,
    val isFavorite: Boolean
)

class ShowAdapter(
    private val onShowClick: (Int) -> Unit,
    private val onFavoriteClick: (Show) -> Unit
) : ListAdapter<ShowUIModel, ShowAdapter.ViewHolder>(ShowDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemShowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ShowUIModel) {
            val show = model.show
            binding.showName.text = show.name
            binding.showRating.text = "Rating: ${show.rating?.average ?: "N/A"}"
            
            Glide.with(binding.showImage)
                .load(show.image?.medium)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.showImage)
            
            binding.showImage.setBackgroundColor(
                binding.root.context.getColor(R.color.md_theme_surfaceVariant)
            )

            binding.favoriteButton.setIconResource(
                if (model.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            )

            binding.root.setOnClickListener { onShowClick(show.id) }
            binding.favoriteButton.setOnClickListener { onFavoriteClick(show) }
        }
    }

    class ShowDiffCallback : DiffUtil.ItemCallback<ShowUIModel>() {
        override fun areItemsTheSame(oldItem: ShowUIModel, newItem: ShowUIModel): Boolean = 
            oldItem.show.id == newItem.show.id
            
        override fun areContentsTheSame(oldItem: ShowUIModel, newItem: ShowUIModel): Boolean = 
            oldItem == newItem
    }
}
