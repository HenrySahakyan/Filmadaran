package com.domain.repository

import com.domain.model.Show
import kotlinx.coroutines.flow.StateFlow

interface IShowRepository {
    val favorites: StateFlow<Set<Int>>
    val favoriteShows: StateFlow<List<Show>>
    suspend fun getShows(page: Int): List<Show>
    suspend fun getShowDetails(id: Int): Show
    suspend fun searchShows(query: String): List<Show>
    fun toggleFavorite(show: Show)
}
