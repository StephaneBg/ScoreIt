/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.app.header;

import android.support.annotation.ColorInt;

import com.hannesdorfmann.mosby.mvp.MvpView;

interface HeaderViewActions extends MvpView {

    void setName(int player, String name);

    void setScore(int player, int score);

    void setColor(int player, @ColorInt int color);

    void setIndicator(int player);

    void setupPlayerCount(int count);

    void showColorSelectorDialog(@ColorInt int initialColor);

    void showNameActionsDialog();
}
