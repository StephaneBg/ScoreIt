/*
 * Copyright (c) 2016 SBG Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.WindowManager;

/**
 * Created by Stéphane on 31/07/2014.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) setSupportActionBar(mToolbar);

        setupDialog();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected abstract int getLayoutResource();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDialog() {
        TypedValue tv = new TypedValue();
        boolean isDialog = getTheme().resolveAttribute(R.attr.isDialog, tv, true) && (0 != tv.data);
        if (isDialog) {
            DisplayMetrics dm = getResources().getDisplayMetrics();

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            params.height = Math.min(
                    getResources().getDimensionPixelSize(R.dimen.dialog_max_height),
                    dm.heightPixels * 3 / 4);
            params.alpha = 1.0f;
            params.dimAmount = 0.5f;
            getWindow().setAttributes(params);
        }

        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isDialog);
            getSupportActionBar().setHomeButtonEnabled(!isDialog);
        }
    }
}
