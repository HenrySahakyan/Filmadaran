package com.data.di

import com.domain.repository.IShowRepository
import com.data.ShowRepositoryImpl
import com.data.api.ApiService
import org.koin.dsl.module
import retrofit2.Retrofit
import androidx.room.Room
import com.core.BaseApp
import com.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.bind

val dataModule = module {
    single<ApiService> {
        val config = BaseApp.context.getConfig()
        get<Retrofit.Builder>()
            .baseUrl(config.baseUrl)
            .build()
            .create(ApiService::class.java)
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "filmadaran_db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().favoriteDao() }

    singleOf(::ShowRepositoryImpl) { bind<IShowRepository>() }
}
