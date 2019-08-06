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

package com.sbgapps.scoreit.core.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.sbgapps.scoreit.core.R
import com.sbgapps.scoreit.core.network.NoConnectivityException
import java.net.SocketTimeoutException

open class BaseFragment : Fragment() {

    @StringRes
    open fun getErrorMessage(throwable: Throwable?): Int = when (throwable) {
        is NoConnectivityException -> R.string.error_no_connectivity
        is SocketTimeoutException -> R.string.error_server
        else -> R.string.error_general
    }

    @DrawableRes
    open fun getErrorDrawable(throwable: Throwable?): Int = when (throwable) {
        is NoConnectivityException ->
            R.drawable.ic_signal_cellular_connected_no_internet_0_bar_black_24dp
        is SocketTimeoutException -> R.drawable.ic_error_outline_black_24dp
        else -> R.drawable.ic_error_outline_black_24dp
    }

    fun setTitle(@StringRes title: Int) {
        setTitle(getString(title))
    }

    fun setTitle(title: String?) {
        (requireActivity() as BaseActivity).supportActionBar?.title = title
    }

    fun setToolbar(toolbar: Toolbar?) {
        (requireActivity() as BaseActivity).setSupportActionBar(toolbar)
    }
}
