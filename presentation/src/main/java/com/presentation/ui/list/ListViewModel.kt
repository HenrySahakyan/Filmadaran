package com.presentation.ui.list

import androidx.lifecycle.viewModelScope
import com.core.presentation.BaseViewModel
import com.core.presentation.ViewState
import com.core.presentation.ViewModelEvent
import com.domain.model.Show
import com.domain.usecase.GetFavoriteShowsUseCase
import com.domain.usecase.GetFavoritesUseCase
import com.domain.usecase.GetShowsUseCase
import com.domain.usecase.SearchShowsUseCase
import com.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update

data class ListPaginationState(
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val isSearching: Boolean = false,
    val isLoadingMore: Boolean = false
)

class ListViewModel(
    private val getShowsUseCase: GetShowsUseCase,
    private val searchShowsUseCase: SearchShowsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val getFavoriteShowsUseCase: GetFavoriteShowsUseCase
) : BaseViewModel() {

    private val _rawShows = MutableStateFlow<List<Show>>(emptyList())
    
    private val _isFilterActive = MutableStateFlow(false)
    val isFilterActive: StateFlow<Boolean> = _isFilterActive.asStateFlow()

    private val _paginationState = MutableStateFlow(ListPaginationState())

    val favorites: StateFlow<Set<Int>> = getFavoritesUseCase()

    val shows: StateFlow<List<ShowUIModel>> = combine(
        _rawShows, 
        _isFilterActive,
        getFavoriteShowsUseCase(),
        favorites
    ) { raw, filter, favs, favIds ->
        val list = if (filter) favs else raw
        list.map { show ->
            ShowUIModel(show, favIds.contains(show.id))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadShows()
    }

    fun loadShows() {
        val currentState = _paginationState.value
        if (currentState.isLastPage || currentState.isLoadingMore || _isFilterActive.value) return
        
        _paginationState.update { it.copy(isLoadingMore = true) }
        
        call(
            request = { getShowsUseCase(currentState.currentPage) },
            onSuccess = { shows ->
                _paginationState.update { 
                    it.copy(
                        isLoadingMore = false,
                        isLastPage = shows.isEmpty(),
                        currentPage = if (shows.isNotEmpty()) it.currentPage + 1 else it.currentPage
                    )
                }
                if (shows.isNotEmpty()) {
                    _rawShows.value = _rawShows.value + shows
                }
            },
            onError = {
                _paginationState.update { it.copy(isLoadingMore = false) }
            }
        )
    }

    fun searchShows(query: String) {
        if (query.isBlank()) {
            if (_paginationState.value.isSearching) {
                _paginationState.update { it.copy(isSearching = false) }
                resetAndReload()
            }
            return
        }

        _paginationState.update { it.copy(isSearching = true) }
        call(
            request = { searchShowsUseCase(query) },
            onSuccess = { 
                _rawShows.value = it
                _paginationState.update { it.copy(isLastPage = true) }
            }
        )
    }

    fun refreshShows() {
        if (_isFilterActive.value) {
            sendEvent(ViewModelEvent.ShowToast("Refresh is unavailable in favorite mode"))
            _viewState.value = ViewState.Complete
        } else {
            resetAndReload()
        }
    }

    private fun resetAndReload() {
        _paginationState.update { it.copy(currentPage = 0, isLastPage = false) }
        _rawShows.value = emptyList()
        loadShows()
    }

    fun toggleFilter() {
        _isFilterActive.value = !_isFilterActive.value
    }

    fun toggleFavorite(show: Show) {
        toggleFavoriteUseCase(show)
    }
}
