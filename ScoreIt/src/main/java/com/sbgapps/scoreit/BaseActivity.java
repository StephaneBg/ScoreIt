/*
 * Copyright (C) 2013 SBG Apps
 * http://baiget.fr
 * stephane@baiget.fr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by sbaiget on 08/01/14.
 */
public class BaseActivity extends Activity {

    public static final float DEFAULT_ALPHA = 0.85f;
    private Drawable mAccentBackground;
    private SystemBarTintManager mTintManager;

    public SystemBarTintManager getTintManager() {
        return mTintManager;
    }

    public Drawable getAccentBackground() {
        return mAccentBackground;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault("fonts/Roboto-Light.ttf");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    public void setAccentDecor() {
        final Resources resources = getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            win.setAttributes(winParams);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            int black = resources.getColor(R.color.black);
            mTintManager.setStatusBarTintColor(black);
            mTintManager.setNavigationBarTintEnabled(true);
            mTintManager.setNavigationBarTintColor(black);
        }

        // Init action bar background
        mAccentBackground = resources.getDrawable(R.drawable.scoreit_background_accent);
        getActionBar().setBackgroundDrawable(mAccentBackground);
        setDecorAlpha(DEFAULT_ALPHA);
    }

    public void setDecorAlpha(float alpha) {
        if (null != mTintManager) mTintManager.setTintAlpha(alpha);
        if (null != mAccentBackground) mAccentBackground.setAlpha((int) (255 * alpha));
    }
}
