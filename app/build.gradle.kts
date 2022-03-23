plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp") version "1.6.10-1.0.4" // Depends on your kotlin version
}

kotlin {
    sourceSets {
        debug {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        release {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
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
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    compileSdk = 31
    defaultConfig {
        applicationId = "com.geomat.openeclassclient"
        minSdk = 21
        targetSdk = 31
        versionCode = 10
        versionName = "0.7.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            // TODO fix crash when true
            // possibly network related (tikxml?)
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    val tikXml = "0.8.13"
    val moshi = "1.13.0"
    val coroutines = "1.6.0-native-mt"
    val room = "2.4.2"
    val retrofit = "2.9.0"
    val work = "1.0.1"
    val compose = "1.1.1"
    val hilt = "2.41"
    val destinations = "1.3.3-beta"

//    implementation fileTree(dir: "libs", include: ["*.jar"])

    //Timber Logging Library
    implementation("com.jakewharton.timber:timber:5.0.1")

    //Material
    implementation("com.google.android.material:material:1.5.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    //noinspection GradleDependency
    implementation("com.tickaroo.tikxml:retrofit-converter:$tikXml")

    // Moshi
    implementation("com.squareup.moshi:moshi:$moshi")
    implementation("com.squareup.moshi:moshi-kotlin:$moshi")

    //  TikXml
    implementation("com.tickaroo.tikxml:annotation:$tikXml")
    //noinspection GradleDependency
    implementation("com.tickaroo.tikxml:core:$tikXml")

    //noinspection GradleDependency
    kapt("com.tickaroo.tikxml:processor:$tikXml")

    // Glide
    implementation("com.github.skydoves:landscapist-glide:1.1.7")

    // JSoup
    implementation("org.jsoup:jsoup:1.14.3")

    // Android KTX
    implementation("androidx.core:core-ktx:1.7.0")

    // Room and Lifecycle dependencies
    implementation("androidx.room:room-runtime:$room")
    kapt("androidx.room:room-compiler:$room")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // WorkManager
    implementation("android.arch.work:work-runtime-ktx:$work")
    //   required to avoid crash on Android 12 API 31
    implementation("androidx.work:work-runtime-ktx:2.7.1")


    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")

    // Navigation
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:$hilt")
    kapt("com.google.dagger:hilt-android-compiler:$hilt")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.compose.material:material:1.1.1")
    implementation("androidx.compose.animation:animation:1.1.1")
    implementation("androidx.compose.ui:ui-tooling:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.1.1")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.compose.runtime:runtime-livedata:$compose")
    implementation("androidx.compose.material:material-icons-extended:$compose")

    // Compose Destinations
    implementation("io.github.raamcosta.compose-destinations:animations-core:$destinations")
    ksp("io.github.raamcosta.compose-destinations:ksp:$destinations")

    // DataStore
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

}
repositories {
    mavenCentral()
}
