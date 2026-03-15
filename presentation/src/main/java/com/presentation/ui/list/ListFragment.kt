package com.presentation.ui.list

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.core.navigation.AnimationType
import com.core.navigation.OpenType
import com.core.navigation.presentFragment
import com.core.presentation.BaseFragment
import com.core.presentation.ViewModelEvent
import com.core.presentation.ViewState
import com.presentation.R
import com.presentation.databinding.FragmentListBinding
import com.presentation.ui.details.DetailsFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListFragment : BaseFragment<FragmentListBinding>() {

    private val viewModel: ListViewModel by viewModel()
    private val adapter by lazy {
        ShowAdapter(
            onShowClick = { showId ->
                presentFragment<DetailsFragment>(
                    openType = OpenType.REPLACE,
                    animationType = AnimationType.LEFT_TO_RIGHT,
                    arguments = arrayOf("show_id" to showId)
                )
            },
            onFavoriteClick = { show ->
                viewModel.toggleFavorite(show)
            }
        )
    }

    override fun onInitView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
        }
        binding.recyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.titleText.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top + 16.dpToPx()
            }
            insets
        }

        setupSearch()
        setupPagination()
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null
            
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    viewModel.searchShows(s?.toString() ?: "")
                }
            }
        })
    }

    private fun setupPagination() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { 
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 && firstVisibleItemPosition >= 0) {
                        viewModel.loadShows()
                    }
                }
            }
        })
    }

    override fun onInitObservers() {
        binding.filterFavorite.setOnClickListener {
            viewModel.toggleFilter()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isFilterActive.collect { isActive ->
                        val icon = if (isActive) {
                            R.drawable.ic_star_filled
                        } else {
                            R.drawable.ic_star_outline
                        }
                        binding.filterFavorite.setImageResource(icon)
                        binding.searchLayout.visibility = if (isActive) View.GONE else View.VISIBLE
                    }
                }

                launch {
                    viewModel.shows.collect { shows ->
                        adapter.submitList(shows)
                        binding.emptyView.visibility = if (shows.isEmpty()) View.VISIBLE else View.GONE
                    }
                }


                launch {
                    viewModel.viewState.collect { state ->
                        if (state !is ViewState.Loading) {
                            binding.swipeRefresh.isRefreshing = false
                        }
                        
                        when (state) {
                            ViewState.Loading -> {
                                if (!binding.swipeRefresh.isRefreshing) {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                binding.errorView.visibility = View.GONE
                            }
                            ViewState.Complete -> {
                                binding.progressBar.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                                binding.errorView.visibility = View.GONE
                            }
                            is ViewState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.recyclerView.visibility = View.GONE
                                binding.errorView.visibility = View.VISIBLE
                                binding.errorText.text = state.message
                            }
                            ViewState.Idle -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }

                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is ViewModelEvent.ShowToast -> {
                                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }



        binding.swipeRefresh.setOnRefreshListener {
            binding.searchEditText.text?.clear()
            viewModel.refreshShows()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadShows()
        }
    }
}
