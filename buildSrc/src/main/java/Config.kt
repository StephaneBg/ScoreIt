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
    const val androidGradle = "3.6.0-rc01"
    const val kotlin = "1.3.61"
    const val coroutines = "1.2.2"
    const val appCompat = "1.1.0"
    const val recyclerView = "1.0.0"
    const val material = "1.2.0-alpha03"
    const val constraintLayout = "1.1.3"
    const val lifecyleViewmodel = "2.1.0"
    const val coreKtx = "1.1.0"
    const val fragmentKtx = "1.1.0"
    const val navigation = "2.1.0"
    const val preferences = "1.1.0"
    const val moshi = "1.9.2"
    const val timber = "4.7.1"
    const val koinAndroidX = "2.0.1"
    const val uniflowAndroidX = "0.9.4"
    const val storage = "2.1.0"
    const val jsr310 = "1.2.2"
}

object Build {
    val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradle}"
}

object Android {
    const val minSdkVersion = 21
    const val targetSdkVersion = 29
    const val compileSdkVersion = 29
    const val buildToolsVersion = "29.0.2"
}

object Libs {
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val lifecyleViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecyleViewmodel}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
    const val navFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val preferences = "androidx.preference:preference-ktx:${Versions.preferences}"

    const val material = "com.google.android.material:material:${Versions.material}"

    const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val moshiAdapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val koinAndroidX = "org.koin:koin-androidx-viewmodel:${Versions.koinAndroidX}"
    const val uniflowAndroidX = "io.uniflow:uniflow-androidx:${Versions.uniflowAndroidX}"
    const val storage = "com.snatik:storage:${Versions.storage}"
    const val jsr310 = "com.jakewharton.threetenabp:threetenabp:${Versions.jsr310}"
}
