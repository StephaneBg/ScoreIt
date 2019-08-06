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

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.widget.ContentLoadingProgressBar
import com.sbgapps.scoreit.core.R
import com.sbgapps.scoreit.core.ext.find
import com.sbgapps.scoreit.core.ext.hide
import com.sbgapps.scoreit.core.ext.show
import com.sbgapps.scoreit.core.widget.ErrorView

@Suppress("UNCHECKED_CAST")
open class BaseLceFragment<T : View>(@LayoutRes layoutId: Int) : BaseFragment() {

    lateinit var contentView: T
    lateinit var loadingView: ContentLoadingProgressBar
    lateinit var errorView: ErrorView

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contentView = find<View>(R.id.contentView) as T
        loadingView = find(R.id.loadingView)
        errorView = find(R.id.errorView)
    }

    @CallSuper
    protected open fun showContent() {
        errorView.hide()
        loadingView.hide()
        contentView.show()
    }

    @CallSuper
    protected open fun showLoading() {
        contentView.hide()
        errorView.hide()
        loadingView.show()
    }

    @CallSuper
    protected open fun showError(throwable: Throwable?, action: () -> Unit) {
        contentView.hide()
        loadingView.hide()
        errorView.show(getErrorDrawable(throwable), getErrorMessage(throwable), action)
    }
}
