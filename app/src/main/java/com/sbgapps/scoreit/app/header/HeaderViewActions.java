package com.sbgapps.scoreit.app.header;

import android.support.annotation.ColorInt;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by sbaiget on 21/12/2016.
 */

interface HeaderViewActions extends MvpView {

    void setName(int player, String name);

    void setScore(int player, int score);

    void setColor(int player, @ColorInt int color);

    void setIndicator(int player);

    void setupPlayerCount(int count);

    void showColorSelectorDialog(@ColorInt int initialColor);

    void showNameActionsDialog();
}
