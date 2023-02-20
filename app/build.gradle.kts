plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6" // Depends on your kotlin version
}

android {
    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
    }
    compileOptions {
        // Sets Java compatibility to Java 8
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    compileSdk = 33
    defaultConfig {
        applicationId = "com.geomat.openeclassclient"
        minSdk = 21
        targetSdk = 33
        versionCode = 23
        versionName = "0.9.10"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
        }
    }
    namespace = "com.geomat.openeclassclient"
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

dependencies {
    // UI
    val accompanist     = "0.25.1"
    val appCompat       = "1.5.1"
    val compose         = "1.2.1"
    val composeNav      = "1.0.0"
    val destinations    = "1.6.20-beta"
    val glide           = "2.0.0"

    // Network
    val jsoup           = "1.15.3"
    val moshi           = "1.14.0"
    val retrofit        = "2.9.0"
    val xmlUtil         = "0.84.3"
    val guava           = "31.1-jre"

    // Logic
    val coroutines      = "1.6.4"
    val dataStore       = "1.0.0"
    val hilt            = "2.44"
    val hiltWork        = "1.0.0"
    val room            = "2.4.3"
    val timber          = "5.0.1"
    val work            = "1.0.1"



    /*
    *   UI Dependencies
    */

    // App Compat
    implementation("androidx.appcompat:appcompat:$appCompat")

    // Jetpack Compose
    implementation("androidx.compose.material:material:$compose")
    implementation("androidx.compose.animation:animation:$compose")
    implementation("androidx.compose.ui:ui-tooling:$compose")
    implementation("androidx.compose.runtime:runtime-livedata:$compose")
    implementation("androidx.compose.material:material-icons-extended:$compose")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$compose")

    // Navigation
    implementation("androidx.hilt:hilt-navigation-compose:$composeNav")

    // Compose Destinations
    implementation("io.github.raamcosta.compose-destinations:animations-core:$destinations")
    ksp("io.github.raamcosta.compose-destinations:ksp:$destinations")

    // Accompanist
    implementation("com.google.accompanist:accompanist-placeholder:$accompanist")

    // Glide
    implementation("com.github.skydoves:landscapist-glide:$glide")
    implementation("com.github.skydoves:landscapist-animation:$glide")
    implementation("com.github.skydoves:landscapist-placeholder:$glide")



    /*
    *   Network Dependencies
    */

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    // XmlUtil
    implementation("io.github.pdvrieze.xmlutil:core-android:$xmlUtil")
    implementation("io.github.pdvrieze.xmlutil:serialization-android:$xmlUtil")

    // Moshi
    implementation("com.squareup.moshi:moshi:$moshi")
    implementation("com.squareup.moshi:moshi-kotlin:$moshi")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshi")

    // JSoup
    implementation("org.jsoup:jsoup:$jsoup")

    // Guava
    implementation("com.google.guava:guava:$guava")



    /*
    *   Logic Dependencies
    */

    //Timber Logging Library
    implementation("com.jakewharton.timber:timber:$timber")

    // Room and Lifecycle dependencies
    implementation("androidx.room:room-runtime:$room")
    kapt("androidx.room:room-compiler:$room")
    implementation ("androidx.room:room-ktx:$room")

    // WorkManager
    implementation("android.arch.work:work-runtime-ktx:$work")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")

    // Hilt
    implementation("com.google.dagger:hilt-android:$hilt")
    kapt("com.google.dagger:hilt-android-compiler:$hilt")
    implementation("androidx.hilt:hilt-work:$hiltWork")
    kapt("androidx.hilt:hilt-compiler:$hiltWork")

    // DataStore
    implementation("androidx.datastore:datastore:$dataStore")
    implementation("androidx.datastore:datastore-preferences:$dataStore")



    /*
    *   Test Dependencies
    */

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")


}
repositories {
    mavenCentral()
}
