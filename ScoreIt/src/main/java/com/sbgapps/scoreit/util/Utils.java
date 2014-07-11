/*
 * Copyright (c) 2014 SBG Apps
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

package com.sbgapps.scoreit.util;

import android.app.Dialog;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 04/07/2014.
 */
public class Utils {

    public static void colorizeDialog(Dialog dialog) {
        final Resources r = dialog.getContext().getResources();
        final int color = r.getColor(R.color.primary_accent);
        int id = r.getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(id);
        if (null != divider)
            divider.setBackgroundColor(color);
        id = r.getIdentifier("android:id/alertTitle", null, null);
        TextView tv = (TextView) dialog.findViewById(id);
        if (null != tv)
            tv.setTextColor(color);
    }
}
