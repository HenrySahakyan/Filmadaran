package com.presentation.ui.details

import com.core.presentation.BaseViewModel
import com.core.presentation.ViewState
import com.domain.model.Show
import com.domain.usecase.GetFavoritesUseCase
import com.domain.usecase.GetShowDetailsUseCase
import com.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getShowDetailsUseCase: GetShowDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase
) : BaseViewModel() {

    private val _show = MutableStateFlow<Show?>(null)
    val show: StateFlow<Show?> = _show.asStateFlow()

    val favorites: StateFlow<Set<Int>> = getFavoritesUseCase()

    fun loadShowDetails(id: Int) {
        call(
            request = { getShowDetailsUseCase(id) },
            onSuccess = { _show.value = it }
        )
    }

    fun toggleFavorite() {
        _show.value?.let { 
            toggleFavoriteUseCase(it)
        }
    }
}
