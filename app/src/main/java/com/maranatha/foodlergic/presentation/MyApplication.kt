package com.maranatha.foodlergic.presentation

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {

    private val cloudName = "dvy9oap2x"
    override fun onCreate() {
        super.onCreate()
        initCloudinary()
    }

    private fun initCloudinary() {
        val config = mapOf("cloud_name" to cloudName)
        MediaManager.init(this, config)
    }
}