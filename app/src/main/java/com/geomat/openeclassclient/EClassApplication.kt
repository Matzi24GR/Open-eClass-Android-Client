package com.geomat.openeclassclient

import android.app.Application
import timber.log.Timber

class EClassApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}