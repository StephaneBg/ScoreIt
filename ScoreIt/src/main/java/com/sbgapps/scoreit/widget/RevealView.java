package com.sbgapps.scoreit.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
        this(context, null);
    }

    public RevealView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
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

        mFront.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                reveal();
                return true;
            }
        });

        mFront.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsOpen) hide();
            }
        });

        mFront.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
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

    public interface RevealViewListener {
        public void onViewRevealed(RevealView revealView);
    }
}
