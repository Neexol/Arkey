package com.neexol.arkey

import android.app.Application
import com.neexol.arkey.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(
                databaseModule,
                repositoriesModule,
                viewModelsModule,
                adaptersModule,
                codersModule
            ))
        }
    }
}