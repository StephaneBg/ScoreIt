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

package com.sbgapps.scoreit.core.ext

import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit

fun FragmentActivity.addFragment(
    frameId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false
) = supportFragmentManager.commit {
    add(frameId, fragment)
    if (addToBackStack) addToBackStack(null)
}

fun FragmentActivity.addFragment(
    frameId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    animIn: Int,
    animOut: Int
) = supportFragmentManager.commit {
    setCustomAnimations(animIn, animOut, animIn, animOut)
    add(frameId, fragment)
    if (addToBackStack) addToBackStack(null)
}

fun FragmentActivity.replaceFragment(
    frameId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false
) = supportFragmentManager.commit {
    replace(frameId, fragment)
    if (addToBackStack) addToBackStack(null)
}

fun FragmentActivity.replaceFragment(
    frameId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    animIn: Int,
    animOut: Int
) = supportFragmentManager.commit {
    setCustomAnimations(animIn, animOut, animIn, animOut)
    replace(frameId, fragment)
    if (addToBackStack) addToBackStack(null)
}

fun FragmentActivity.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = window.decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flags
    }
}
