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

package com.sbgapps.scoreit.navigationdrawer;

public class NavigationDrawerItem {

    private String mItemName;
    private boolean mSeparator;
    private boolean mSelected;

    public NavigationDrawerItem(boolean separator) {
        this(null, separator);
    }

    public NavigationDrawerItem(String itemName) {
        this(itemName, false);
    }

    public NavigationDrawerItem(String itemName, boolean separator) {
        this.mItemName = itemName;
        this.mSeparator = separator;
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        this.mItemName = itemName;
    }

    public boolean isSeparator() {
        return mSeparator;
    }

    public void setSeparator(boolean separator) {
        this.mSeparator = separator;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }
}
