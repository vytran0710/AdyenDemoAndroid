package com.example.adyendemo

import android.app.Application
import com.example.adyendemo.module.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AdyenDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AdyenDemoApplication)
            modules(appModule)
        }
    }
}