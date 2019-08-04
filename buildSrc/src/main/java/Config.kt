/*
 * Copyright 2019 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.JavaVersion

object Versions {
    val java = JavaVersion.VERSION_1_8
   const val androidGradle = "3.4.2"
   const val googleServices = "4.3.0"
   const val kotlin = "1.3.41"
   const val coroutines = "1.2.2"
   const val appCompat = "1.1.0-rc01"
   const val vectorDrawable = "1.1.0-rc01"
   const val recyclerView = "1.1.0-beta01"
   const val material = "1.1.0-alpha09"
   const val constraintLayout = "2.0.0-beta2"
   const val lifecyleViewmodel = "2.1.0-rc01"
   const val coreKtx = "1.1.0-rc02"
   const val fragmentKtx = "1.1.0-rc03"
   const val navigation = "2.1.0-beta02"
   const val timber = "4.7.1"
   const val anko = "0.10.8"
   const val koin = "2.0.1"
   const val kaskade = "0.2.3"
   const val jsr310 = "1.2.1"
}

object Build {
    val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradle}"
    val googleServices = "com.google.gms:google-services:${Versions.googleServices}"
}

object Android {
    const val minSdkVersion = 21
    const val targetSdkVersion = 29
    const val compileSdkVersion = "android-29"
}

object Libs {
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val anko = "org.jetbrains.anko:anko-commons:${Versions.anko}"

    val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    val vectorDrawable = "androidx.vectordrawable:vectordrawable:${Versions.vectorDrawable}"
    val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    val lifecyleViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecyleViewmodel}"
    val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
    val navFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    val navUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

    val material = "com.google.android.material:material:${Versions.material}"

    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    val koinAndroid = "org.koin:koin-androidx-viewmodel:${Versions.koin}"
    val kaskadeCore = "com.github.gumil.kaskade:kaskade:${Versions.kaskade}"
    val kaskadeLiveData = "com.github.gumil.kaskade:kaskade-livedata:${Versions.kaskade}"
    val jsr310 = "com.jakewharton.threetenabp:threetenabp:${Versions.jsr310}"
}
