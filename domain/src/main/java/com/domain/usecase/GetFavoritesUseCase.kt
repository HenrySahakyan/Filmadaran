package com.domain.usecase

import com.domain.repository.IShowRepository
import kotlinx.coroutines.flow.StateFlow

class GetFavoritesUseCase(private val repository: IShowRepository) {
    operator fun invoke(): StateFlow<Set<Int>> = repository.favorites
}
