package com.core.di.module

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.core.BuildConfig
import com.core.BaseApp
import okhttp3.Cache
import java.io.File

private const val TIMEOUT = 60L

private val retrofitModule = module {

    single {
        val okHttpBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            okHttpBuilder.addInterceptor(HttpLoggingInterceptor().apply { 
                level = HttpLoggingInterceptor.Level.BODY 
            })
        }

        val cacheSize = 10 * 1024 * 1024L
        val cacheDir = File(BaseApp.context.cacheDir, "http_cache")
        okHttpBuilder.cache(Cache(cacheDir, cacheSize))

        okHttpBuilder.readTimeout(TIMEOUT, TimeUnit.SECONDS)
        okHttpBuilder.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        okHttpBuilder.callTimeout(TIMEOUT, TimeUnit.SECONDS)
        
        okHttpBuilder
    }

    single {
        val gsonBuilder = GsonBuilder().setLenient()
        val okHttp = get<OkHttpClient.Builder>().build()

        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .client(okHttp)
    }
}

val apiModule = listOf(retrofitModule)
