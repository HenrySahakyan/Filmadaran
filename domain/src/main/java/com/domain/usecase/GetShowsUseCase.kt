package com.domain.usecase

import com.domain.model.Show
import com.domain.repository.IShowRepository

class GetShowsUseCase(private val repository: IShowRepository) {
    suspend operator fun invoke(page: Int): List<Show> = repository.getShows(page)
}
