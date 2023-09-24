package ru.netology.yandexmaps.app

import android.app.Application

import com.yandex.mapkit.MapKitFactory
import ru.netology.yandexmaps.BuildConfig



class MyApp: Application() {
    companion object {

    }

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPS_API_KEY)
    }
}