/*
 * Copyright 2020 Stéphane Baiget
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

object Build {

    object Versions {
        const val kotlin = "1.3.72"
        const val androidGradle = "4.0.1"
        val java = JavaVersion.VERSION_1_8
    }

    const val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradle}"
}

object Android {
    const val minSdkVersion = 21
    const val targetSdkVersion = 29
    const val compileSdkVersion = 29
}

object Libs {

    private object Versions {
        const val material = "1.2.0"
        const val playCore = "1.8.1"
        const val billing = "3.0.0"
        const val timber = "4.7.1"
        const val koinAndroidX = "2.1.0"
        const val uniflowAndroidX = "0.11.1"
        const val storage = "2.1.0"
        const val jsr310 = "1.2.2"
    }

    const val material = "com.google.android.material:material:${Versions.material}"
    const val playCore = "com.google.android.play:core-ktx:${Versions.playCore}"
    const val billingKtx = "com.android.billingclient:billing-ktx:${Versions.billing}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val koinAndroidX = "org.koin:koin-androidx-viewmodel:${Versions.koinAndroidX}"
    const val uniflowAndroidX = "io.uniflow:uniflow-androidx:${Versions.uniflowAndroidX}"
    const val storage = "com.snatik:storage:${Versions.storage}"
    const val jsr310 = "com.jakewharton.threetenabp:threetenabp:${Versions.jsr310}"
}

object AndroidX {

    private object Versions {
        const val appCompat = "1.2.0"
        const val recyclerView = "1.1.0"
        const val constraintLayout = "1.1.3"
        const val lifecyleViewmodel = "2.2.0"
        const val coreKtx = "1.3.1"
        const val fragmentKtx = "1.2.5"
        const val preference = "1.1.1"
        const val annotation = "1.1.0"
    }

    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val lifecyleViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecyleViewmodel}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
    const val preference = "androidx.preference:preference-ktx:${Versions.preference}"
    const val annotation = "androidx.annotation:annotation:${Versions.annotation}"
}

object Moshi {

    private const val version = "1.9.3"

    const val kotlin = "com.squareup.moshi:moshi-kotlin:$version"
    const val adapters = "com.squareup.moshi:moshi-adapters:$version"
    const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
}

object Coroutines {

    private const val version = "1.3.8"

    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
}

object Tests {

    private object Version {
        const val junit = "4.12"
        const val mockk = "1.9"
    }

    const val junit = "junit:junit:${Version.junit}"
    const val mockk = "io.mockk:mockk:${Version.mockk}"
}