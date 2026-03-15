package com.presentation.ui.details

import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.core.presentation.BaseFragment
import com.core.presentation.ViewState
import com.presentation.R
import com.presentation.databinding.FragmentDetailsBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    private val viewModel: DetailsViewModel by viewModel()

    override fun onInitView() {
        val showId = arguments?.getInt("show_id") ?: return
        viewModel.loadShowDetails(showId)
        
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val topPadding = systemBars.top + 16.dpToPx()
            binding.backButton.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = topPadding
            }
            binding.favoriteButton.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = topPadding
            }
            insets
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    override fun onInitObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.show.collect { show ->
                        show?.let {
                            binding.showName.text = it.name
                            binding.showSummary.text = Html.fromHtml(it.summary ?: "", Html.FROM_HTML_MODE_COMPACT)
                            binding.showGenres.text = it.genres?.joinToString(", ") ?: "N/A"
                            binding.showStatus.text = "Status: ${it.status ?: "Unknown"}"
                            binding.showRating.text = "★ ${it.rating?.average ?: "N/A"}"
                            binding.showPremiered.text = "Premiered: ${it.premiered ?: "Unknown"}"
                            
                            Glide.with(binding.showImage)
                                .load(it.image?.original)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(android.R.color.transparent)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(binding.showImage)
                            
                            binding.showImage.setBackgroundColor(
                                requireContext().getColor(R.color.md_theme_surfaceVariant)
                            )
                        }
                    }
                }

                launch {
                    viewModel.favorites.collect { favorites ->
                        val showId = arguments?.getInt("show_id")
                        val isFavorite = showId?.let { favorites.contains(it) } ?: false
                        binding.favoriteButton.setImageResource(
                            if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                        )
                    }
                }

                launch {
                    viewModel.viewState.collect { state ->
                        when (state) {
                            ViewState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.contentView.visibility = View.GONE
                                binding.errorView.visibility = View.GONE
                            }
                            ViewState.Complete -> {
                                binding.progressBar.visibility = View.GONE
                                binding.contentView.visibility = View.VISIBLE
                                binding.errorView.visibility = View.GONE
                            }
                            is ViewState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.contentView.visibility = View.GONE
                                binding.errorView.visibility = View.VISIBLE
                                binding.errorText.text = state.message
                            }
                            ViewState.Idle -> {
                                binding.progressBar.visibility = View.GONE
                                binding.contentView.visibility = View.GONE
                                binding.errorView.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }


        binding.retryButton.setOnClickListener {
            val showId = arguments?.getInt("show_id") ?: return@setOnClickListener
            viewModel.loadShowDetails(showId)
        }
    }
}
