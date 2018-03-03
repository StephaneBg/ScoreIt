object Versions {
    val androidGradle = "3.0.1"
    val kotlin = "1.2.30"
    val coroutines = "0.22.3"
    val supportLibrary = "27.1.0"
    val constraintLayout = "1.0.2"
    val archLifecyle = "1.1.0"
    val archRoom = "1.0.0"
    val timber = "4.5.1"
    val moshi = "1.5.0"
    val anko = "0.10.1"
    val koin = "0.8.2"
    val androidKtx = "0.2"
}

object Build {
    val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradle}"
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Android {
    val buildToolsVersion = "27.0.3"
    val minSdkVersion = 21
    val targetSdkVersion = 27
    val compileSdkVersion = 27
    val applicationId = "com.sbgapps.scoreit"
    val versionCode = 1
    val versionName = "0.1"
}

object Libs {
    val kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val ankoAppcompat = "org.jetbrains.anko:anko-appcompat-v7-commons:${Versions.anko}"

    val appcompat = "com.android.support:appcompat-v7:${Versions.supportLibrary}"
    val recyclerview = "com.android.support:recyclerview-v7:${Versions.supportLibrary}"
    val cardview = "com.android.support:cardview-v7:${Versions.supportLibrary}"
    val design = "com.android.support:design:${Versions.supportLibrary}"
    val vectorDrawable = "com.android.support:support-vector-drawable:${Versions.supportLibrary}"
    val constraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constraintLayout}"

    val archExtensions = "android.arch.lifecycle:extensions:${Versions.archLifecyle}"
    val archRoom = "android.arch.persistence.room:runtime:${Versions.archRoom}"
    val archRoomCompiler = "android.arch.persistence.room:compiler:${Versions.archRoom}"
    val androidKtx = "androidx.core:core-ktx:${Versions.androidKtx}"

    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    val koin = "org.koin:koin-android-architecture:${Versions.koin}"
    val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
}
