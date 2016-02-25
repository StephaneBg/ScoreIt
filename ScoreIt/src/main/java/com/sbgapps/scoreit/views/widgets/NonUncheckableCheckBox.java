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

package com.sbgapps.scoreit.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ToggleButton;

/**
 * Created by Stéphane on 14/10/2014.
 */
public class NonUncheckableCheckBox extends CheckBox {

    public NonUncheckableCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NonUncheckableCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonUncheckableCheckBox(Context context) {
        super(context);
    }

    @Override
    public void setChecked(boolean checked) {
        if (!isChecked()) super.setChecked(checked);
    }

    public void forceSetChecked(boolean checked) {
        super.setChecked(checked);
    }
}
