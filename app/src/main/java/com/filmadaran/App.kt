package com.filmadaran

import com.core.CoreConfig
import com.core.BaseApp
import com.data.di.dataModule
import com.domain.di.domainModule
import com.presentation.di.presentationModule
import org.koin.core.module.Module

class App : BaseApp() {


    override fun getConfig(): CoreConfig {
        return CoreConfig(
            mainContainer = R.id.fragment_container,
            baseUrl = "https://api.tvmaze.com/"
        )
    }

    override fun provideAdditionalModules(): List<Module> {
        return listOf(dataModule, domainModule, presentationModule)
    }
}
