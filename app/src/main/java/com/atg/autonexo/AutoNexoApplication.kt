package com.atg.autonexo

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AutoNexoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Leer la API key que pusiste como meta-data en el AndroidManifest
        val appInfo = packageManager.getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA
        )

        val mapsKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")

        Log.d("MapsKey", "Google Maps API key = $mapsKey")
    }
}
