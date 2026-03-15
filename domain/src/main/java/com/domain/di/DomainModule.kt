package com.domain.di

import com.domain.usecase.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetShowsUseCase)
    factoryOf(::SearchShowsUseCase)
    factoryOf(::GetShowDetailsUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::GetFavoritesUseCase)
    factoryOf(::GetFavoriteShowsUseCase)
}
