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

package com.sbgapps.scoreit.ui.ext

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

fun FragmentActivity.addFragment(fragment: Fragment, frameId: Int, addBackStack: Boolean = false, tag: String? = null) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
        if (addBackStack) addToBackStack(tag)
    }
}

fun FragmentActivity.replaceFragment(fragment: Fragment, frameId: Int, addBackStack: Boolean = false, tag: String? = null) {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment)
        if (addBackStack) addToBackStack(tag)
    }
}
