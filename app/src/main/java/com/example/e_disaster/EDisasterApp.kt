package com.example.e_disaster

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EDisasterApp : Application() {
    // This class is often left empty.
    // Its purpose is to hold the @HiltAndroidApp annotation,
    // which triggers Hilt's code generation.
}
    