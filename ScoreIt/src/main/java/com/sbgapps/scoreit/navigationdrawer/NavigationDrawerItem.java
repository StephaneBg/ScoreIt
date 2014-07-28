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

/**
 * Created by Michal Bialas on 19/07/14.
 *
 * @author Michal Bialas
 */
public class NavigationDrawerItem {

    private String itemName;

    private int itemIcon;

    private boolean mainItem;

    private boolean selected;

    public NavigationDrawerItem(String itemName, int itemIcon, boolean mainItem) {
        this.itemName = itemName;
        this.itemIcon = itemIcon;
        this.mainItem = mainItem;
    }

    public NavigationDrawerItem(String itemName, boolean mainItem) {
        this(itemName, 0, mainItem);
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
    }

    public boolean isMainItem() {
        return mainItem;
    }

    public void setMainItem(boolean mainItem) {
        this.mainItem = mainItem;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
