plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

// POSSIBLE future implementation using retrofit and room
//alias(libs.plugins.ksp)

android {
    namespace = "it.polito.mad.lab5g10.seekscape"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.polito.mad.lab5g10.seekscape"
        minSdk = 28
        targetSdk = 34
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

dependencies {

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.dynamic.links)

    implementation(libs.google.play.services.base)
    implementation(libs.google.play.services.auth)


    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.gson)
    implementation(libs.accompanist.systemuicontroller)


    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)




    // POSSIBLE future implementation using retrofit and room
    //implementation(libs.firebase.firestore.ktx)
    //implementation(libs.androidx.room.runtime.android)
    //implementation(libs.androidx.runtime.livedata)
    //implementation("com.google.firebase:firebase-bom:32.8.1") // or latest
    //implementation("com.google.firebase:firebase-storage-ktx")
    //implementation("com.google.firebase:firebase-auth-ktx")
    //implementation("com.google.firebase:firebase-firestore-ktx")

    //implementation(libs.androidx.navigation.fragment.ktx)
    //implementation(libs.androidx.navigation.ui.ktx)

    //implementation(libs.androidx.navigation.compose)
    // Room
    //implementation(libs.androidx.room.runtime)
    //ksp(libs.androidx.room.compiler)
    //implementation(libs.androidx.room.ktx)
    //NOT if you use KAPT or KSP
//    annotationProcessor("androidx.room:room-compiler:$room_version")
    // Retrofit Converter (for JSON - Gson)
    //implementation(libs.converter.gson)
    //implementation(libs.gson.v2101)

    // Retrofit Converter for Kotlin Serialization
    //implementation(libs.retrofit2.kotlinx.serialization.converter)
    // Retrofit
    //implementation(libs.retrofit)

    // Kotlin Serialization
    //implementation(libs.kotlinx.serialization.core)
    //implementation(libs.kotlinx.serialization.json)

    // Coroutines
    //implementation(libs.kotlinx.coroutines.android)
    //implementation(libs.kotlinx.coroutines.core)

    // Logging Interceptor (for debugging)
    //implementation(libs.logging.interceptor)
    //implementation(libs.okhttp)
    //implementation(libs.thdev.flow.call.adapter.factory)
}