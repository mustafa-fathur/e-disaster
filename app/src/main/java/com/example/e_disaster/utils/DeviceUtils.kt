package com.example.e_disaster.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.example.e_disaster.BuildConfig

object DeviceUtils {
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getDeviceName(): String = Build.MODEL

    fun getAppVersion(): String = BuildConfig.VERSION_NAME
}
