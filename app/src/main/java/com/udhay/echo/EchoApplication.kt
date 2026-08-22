package com.udhay.echo

import android.app.Application
import com.udhay.echo.core.di.EchoApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.plugin.module.dsl.startKoin


class EchoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<EchoApp>{
            androidLogger()
            androidContext(this@EchoApplication)
        }
    }
}