package com.domain.usecase

import com.domain.model.Show
import com.domain.repository.IShowRepository

class ToggleFavoriteUseCase(private val repository: IShowRepository) {
    operator fun invoke(show: Show) = repository.toggleFavorite(show)
}
