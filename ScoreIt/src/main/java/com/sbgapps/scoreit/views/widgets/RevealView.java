/*
 * Copyright (c) 2015 SBG Apps
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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 20/01/2015.
 */
public class RevealView extends FrameLayout {

    private boolean mIsOpen = false;
    private View mFront;
    private View mBack;

    private RevealViewListener mRevealViewListener;

    public RevealView(Context context) {
        super(context);
    }

    public RevealView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RevealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRevealViewListener(RevealViewListener revealViewListener) {
        mRevealViewListener = revealViewListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mFront = findViewById(R.id.front);
        mBack = findViewById(R.id.back);

        mFront.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        mFront.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toggle();
                return true;
            }
        });
    }

    public void reveal() {
        if (!mIsOpen) {
            mIsOpen = true;
            float width = mBack.getWidth();
            ObjectAnimator oa = ObjectAnimator.ofFloat(mFront, "x", -width);
            oa.start();
            if (null != mRevealViewListener) mRevealViewListener.onViewRevealed(this);
        }
    }

    public void hide() {
        if (mIsOpen) {
            mIsOpen = false;
            ObjectAnimator oa = ObjectAnimator.ofFloat(mFront, "x", 0);
            oa.start();
        }
    }

    private void toggle() {
        if (mIsOpen) {
            hide();
        } else {
            reveal();
        }
    }

    public interface RevealViewListener {
        public void onViewRevealed(RevealView revealView);
    }
}
