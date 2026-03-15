package com.presentation.di

import com.presentation.ui.details.DetailsViewModel
import com.presentation.ui.list.ListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::ListViewModel)
    viewModelOf(::DetailsViewModel)
}
