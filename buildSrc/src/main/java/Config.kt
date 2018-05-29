import org.gradle.api.JavaVersion

/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object Versions {
    val java = JavaVersion.VERSION_1_8
    val androidGradle = "3.2.0-alpha15"
    val kotlin = "1.2.41"
    val coroutines = "0.22.3"
    val appcompat = "1.0.0-alpha1"
    val recyclerView = "1.0.0-alpha1"
    val material = "1.0.0-alpha1"
    val constraintLayout = "1.1.0"
    val lifecyle = "2.0.0-alpha1"
    val room = "2.0.0-alpha1"
    val timber = "4.5.1"
    val moshi = "1.5.0"
    val anko = "0.10.5"
    val koin = "1.0.0-alpha-11"
    val ktx = "1.0.0-alpha1"
    val debugDb = "1.0.3"
    val numberPicker = "2.4.6"
}

object Build {
    val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradle}"
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Android {
    val buildToolsVersion = "27.0.3"
    val minSdkVersion = 21
    val targetSdkVersion = 27
    val compileSdkVersion = "android-P"
    val applicationId = "com.sbgapps.scoreit"
    val versionCode = 1
    val versionName = "0.1"
}

object Libs {
    val kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val anko = "org.jetbrains.anko:anko-commons:${Versions.anko}"

    val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    val vectorDrawable = "androidx.vectordrawable:vectordrawable:${Versions.appcompat}"
    val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecyle}"
    val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecyle}"
    val room = "androidx.room:room-runtime:${Versions.room}"
    val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    val ktx = "androidx.core:core-ktx:${Versions.ktx}"

    val material = "com.google.android.material:material:${Versions.material}"

    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"

    val numberPicker = "com.shawnlin:number-picker:${Versions.numberPicker}"

    val koinAndroid = "org.koin:koin-androidx:${Versions.koin}"

    val debugDb = "com.amitshekhar.android:debug-db:${Versions.debugDb}"
}
