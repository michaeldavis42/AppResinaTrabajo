plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.appresina"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.appresina"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Forzando la versión de kotlinx-metadata-jvm para evitar conflictos
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0")

    // Jetpack Compose + Material 3
    implementation("androidx.compose.material:material-icons-extended-android:1.6.7") // Or the latest version
    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.7") // Añadida la dependencia que faltaba
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.7")

    // Ciclo de vida y ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Navegación Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Room (Base de datos local)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // DataStore Preferences (almacenamiento local)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // Permisos
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")


    // Activity Result API
    implementation("androidx.activity:activity-compose:1.8.2")

    // Coil para carga de imágenes
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.7")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Also update other Retrofit libraries you are using
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}
