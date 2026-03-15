package com.domain.usecase

import com.domain.model.Show
import com.domain.repository.IShowRepository

class GetShowDetailsUseCase(private val repository: IShowRepository) {
    suspend operator fun invoke(id: Int): Show = repository.getShowDetails(id)
}
