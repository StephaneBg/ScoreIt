package com.sbgapps.scoreit.app.utils;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by sbaiget on 10/01/2017.
 */

public abstract class LinearListHelper<T> {

    public ArrayList<T> populateItems(LinearLayout parent, @LayoutRes int resId, int count) {
        ArrayList<T> items = new ArrayList<>(count);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        for (int i = 0; i < count; i++) {
            View view = inflater.inflate(resId, parent, false);
            T item = onCreateItem(view, i);
            items.add(item);
            parent.addView(view);
        }

        return items;
    }

    abstract public T onCreateItem(View view, int player);
}
