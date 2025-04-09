plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp.android)
    alias(libs.plugins.androidx.navigation.safe.args)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.galleryapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.galleryapp"
        minSdk = 30
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dagger - Hilt
    implementation(libs.dagger.hilt)
    implementation(libs.androidx.hilt.common)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Kotlin - Coroutines
    implementation(libs.kotlinx.coroutines.core)
    runtimeOnly(libs.kotlinx.coroutines.android)

    // Core - Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Glide
    implementation(libs.glide)
}