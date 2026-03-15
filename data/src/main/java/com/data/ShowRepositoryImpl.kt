package com.data

import com.core.data.BaseRepository
import com.data.api.ApiService
import com.data.local.dao.FavoriteDao
import com.data.local.entity.FavoriteShowEntity
import com.domain.model.Image
import com.domain.model.Rating
import com.domain.model.Show
import com.domain.repository.IShowRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ShowRepositoryImpl(
    private val apiService: ApiService,
    private val favoriteDao: FavoriteDao
) : BaseRepository(), IShowRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val favoriteShows: StateFlow<List<Show>> = favoriteDao.getAllFavorites()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override val favorites: StateFlow<Set<Int>> = favoriteDao.getAllFavorites()
        .map { entities -> entities.map { it.id }.toSet() }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    override suspend fun getShows(page: Int): List<Show> {
        return apiService.getShows(page)
    }

    override suspend fun getShowDetails(id: Int): Show {
        return apiService.getShowDetails(id)
    }

    override suspend fun searchShows(query: String): List<Show> {
        return apiService.searchShows(query).map { it.show }
    }

    override fun toggleFavorite(show: Show) {
        repositoryScope.launch {
            val isFav = favorites.value.contains(show.id)
            if (isFav) {
                favoriteDao.deleteFavoriteById(show.id)
            } else {
                favoriteDao.insertFavorite(show.toEntity())
            }
        }
    }

    private fun FavoriteShowEntity.toDomain(): Show {
        return Show(
            id = id,
            name = name,
            type = type,
            language = language,
            genres = genres,
            status = status,
            premiered = premiered,
            officialSite = officialSite,
            rating = Rating(ratingAverage),
            image = Image(imageMedium, imageOriginal),
            summary = summary
        )
    }

    private fun Show.toEntity(): FavoriteShowEntity {
        return FavoriteShowEntity(
            id = id,
            name = name,
            type = type,
            language = language,
            genres = genres,
            status = status,
            premiered = premiered,
            officialSite = officialSite,
            ratingAverage = rating?.average,
            imageMedium = image?.medium,
            imageOriginal = image?.original,
            summary = summary
        )
    }
}
