plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
       alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    // alias(libs.plugins.kotzilla) apply false
}

android {
  namespace = "com.greeffer.empty_activity"
    compileSdk = 36

    defaultConfig {
    applicationId = "com.greeffer.empty_activity"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = false
      buildConfig = false
      shaders = false
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {

    // Core Android dependencies
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // CameraX
  implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.media3)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)

    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    // Instrumented tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Local tests: jUnit, coroutines, Android runner
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented tests: jUnit rules and runners
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // Navigation
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    
    
    
    
    // androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // androidTestImplementation(libs.androidx.junit)
    // androidTestImplementation(platform(libs.androidx.compose.bom))
    // debugImplementation(libs.androidx.compose.ui.test.manifest)
    // debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.accompanist.permissions)
    // implementation(libs.androidx.activity.compose)
    // implementation(libs.androidx.camera.camera2)
    // implementation(libs.androidx.camera.compose)
    // implementation(libs.androidx.camera.core)
    // implementation(libs.androidx.camera.extensions)
    // implementation(libs.androidx.camera.lifecycle)
    // implementation(libs.androidx.camera.media3)
    // implementation(libs.androidx.camera.video)
    // implementation(libs.androidx.camera.view)
    // implementation(libs.androidx.compose.adaptive)
    // implementation(libs.androidx.compose.adaptive.layout)
    // implementation(libs.androidx.compose.material.icons.core)
    // implementation(libs.androidx.compose.material.icons.extended)
    // implementation(libs.androidx.compose.material3)
    // implementation(libs.androidx.compose.ui)
    // implementation(libs.androidx.compose.ui.graphics)
    // implementation(libs.androidx.compose.ui.tooling.preview)
    // implementation(libs.androidx.core.ktx)
    // implementation(libs.androidx.datastore.core) //  implementation(project(":libs"))
    // implementation(libs.androidx.exifinterface)
    // implementation(libs.androidx.lifecycle.runtime.compose)
    // implementation(libs.androidx.lifecycle.runtime.ktx)
    // implementation(libs.androidx.lifecycle.viewmodel.compose)
    // implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    // implementation(libs.androidx.material3)
    // implementation(libs.androidx.navigation.common.ktx)
    // implementation(libs.androidx.navigation3.runtime)
    // implementation(libs.androidx.navigation3.ui)
    // implementation(libs.androidx.room.ktx)
    // implementation(libs.androidx.room.runtime)
    // implementation(libs.coil.compose)
    // implementation(libs.datastore.preferences)
    // implementation(libs.koin.android)
    // implementation(libs.koin.androidx.compose)
    // implementation(libs.koin.androidx.compose.navigation)
    // implementation(libs.koin.annotations)
    // implementation(libs.koin.compose)
    // implementation(libs.koin.compose.navigation3)
    // implementation(libs.koin.compose.viewmodel)
    // implementation(libs.koin.core)
    // implementation(libs.koin.ktor)
    // implementation(libs.koin.test)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    // implementation(libs.kotzilla.sdk.compose)
    // implementation(libs.material)
    // implementation(libs.media3.exoplayer)
    // implementation(libs.media3.ui.compose)
    // implementation(libs.play.services.location)
    // implementation(platform(libs.androidx.compose.bom))
    // implementation(platform(libs.koin.bom))
    // implementation(project(":core:data"))
    // implementation(project(":core:domain"))
    // implementation(project(":core:ui"))
    // testImplementation(libs.androidx.core)
    // testImplementation(libs.androidx.junit)
    // testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    // //    implementation(libs.koin.androidx.startup)
}

