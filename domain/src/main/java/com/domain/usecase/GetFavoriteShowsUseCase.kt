package com.domain.usecase

import com.domain.model.Show
import com.domain.repository.IShowRepository
import kotlinx.coroutines.flow.StateFlow

class GetFavoriteShowsUseCase(private val repository: IShowRepository) {
    operator fun invoke(): StateFlow<List<Show>> = repository.favoriteShows
}
