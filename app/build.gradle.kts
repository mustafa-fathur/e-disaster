plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.e_disaster"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.e_disaster"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core.splashscreen)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("androidx.compose.runtime:runtime-livedata")


    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.telecom)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.core.i18n)
    kapt(libs.hilt.compiler)

    // Hilt and Jetpack Compose Integration
    implementation(libs.androidx.hilt.navigation.compose)

    // Lifecycle ViewModel KTX (for viewModelScope and Hilt injection)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Networking dependencies
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Image loading
    implementation(libs.coil.compose)

    // Jetpack DataStore for storing the auth token
    implementation(libs.androidx.datastore.preferences)

    // FCM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")


// Don't need for now
//    implementation(libs.volley)
//    implementation(libs.androidx.core.telecom)
//    implementation(libs.androidx.media3.exoplayer)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}