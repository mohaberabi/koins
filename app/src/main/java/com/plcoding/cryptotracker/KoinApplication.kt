package com.plcoding.cryptotracker

import android.app.Application
import com.plcoding.cryptotracker.features.crypto.data.di.cryptoModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class KoinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinApplication)
            androidLogger()
            modules(
                cryptoModule,
            )
        }
    }
}