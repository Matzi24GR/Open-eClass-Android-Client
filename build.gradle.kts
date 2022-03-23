// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version = "1.6.10"

    repositories {
        google()
        jcenter()
        
    }
    dependencies {
        val nav_version = "2.4.1"
        val hilt_version = "2.40.5"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")


        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}
