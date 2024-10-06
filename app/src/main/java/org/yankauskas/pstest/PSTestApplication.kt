package org.yankauskas.pstest

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.yankauskas.pstest.presentation.di.dataModule
import org.yankauskas.pstest.presentation.di.domainModule

class PSTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@PSTestApplication)
            modules(dataModule, domainModule)
        }
    }
}