package com.domain.usecase

import com.domain.model.Show
import com.domain.repository.IShowRepository

class SearchShowsUseCase(private val repository: IShowRepository) {
    suspend operator fun invoke(query: String): List<Show> = repository.searchShows(query)
}
