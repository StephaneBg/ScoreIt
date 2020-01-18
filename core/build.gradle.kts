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

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(Android.compileSdkVersion)

    defaultConfig {
        minSdkVersion(Android.minSdkVersion)
        targetSdkVersion(Android.targetSdkVersion)
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }
}

dependencies {
    implementation(kotlin("stdlib", Build.Versions.kotlin))
    implementation(Coroutines.core)
    implementation(Coroutines.android)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.fragmentKtx)
    implementation(AndroidX.lifecyleViewmodel)
    implementation(Libs.material)
    implementation(Libs.koinAndroidX)
    implementation(Libs.uniflowAndroidX)
    implementation(Libs.timber)
}
