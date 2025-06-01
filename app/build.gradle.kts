plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.charchil.reminderpro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.charchil.reminderpro"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.compose.material3:material3:1.3.1")

    // Compose Dependencies
//    implementation("androidx.compose.ui:ui:1.7.8")
//    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
//    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation(libs.androidx.animation.core.android)
//    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")

    // Hilt (Dagger)
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.activity)
    implementation(libs.play.services.location)
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    kapt("androidx.room:room-compiler:2.6.1")
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
//    implementation("androidx.compose.animation:animation-core:1.7.8")

    implementation(platform("androidx.compose:compose-bom:2024.03.00"))

    implementation("androidx.compose.material:material-icons-extended:1.7.8")




    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Gson
    implementation("com.google.code.gson:gson:2.12.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // ✅ Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

// ✅ Gson Converter for parsing JSON to data class
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// ✅ Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // ✅ Weather & Location related missing dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

}
