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

package com.sbgapps.scoreit.app.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.sbgapps.scoreit.app.BuildConfig
import com.sbgapps.scoreit.app.databinding.ActivityAboutBinding
import com.sbgapps.scoreit.core.ui.BaseActivity


class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.logo, View.ALPHA, 0.2f, 1f),
                ObjectAnimator.ofFloat(binding.logo, View.SCALE_X, 0.5f, 1f),
                ObjectAnimator.ofFloat(binding.logo, View.SCALE_Y, 0.5f, 1f),
                ObjectAnimator.ofFloat(binding.appName, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(binding.appVersionName, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(binding.author, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(binding.email, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(binding.github, View.ALPHA, 0f, 1f)
            )
            interpolator = FastOutSlowInInterpolator()
            duration = 1200
            start()
        }

        binding.appVersionName.text = BuildConfig.VERSION_NAME

        binding.email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, "stephane@baiget.fr")
                putExtra(Intent.EXTRA_SUBJECT, "ScoreIt")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        binding.github.setOnClickListener {
            val webPage: Uri = Uri.parse("https://github.com/StephaneBg/ScoreIt")
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }
}
