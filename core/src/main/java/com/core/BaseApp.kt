package com.core

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.core.di.module.apiModule
import com.core.di.module.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import timber.log.Timber

abstract class BaseApp : Application() {

    companion object {
        lateinit var context: BaseApp
            private set

        fun isInitialized(): Boolean = ::context.isInitialized
    }

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        
        startKoin {
            androidContext(this@BaseApp)
            modules(apiModule + appModule + provideAdditionalModules())
        }
        
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "(${element.fileName}:${element.lineNumber})${element.methodName}"
                }
            })
        }
    }

    abstract fun getConfig(): CoreConfig
    
    protected open fun provideAdditionalModules(): List<Module> = emptyList()
}
