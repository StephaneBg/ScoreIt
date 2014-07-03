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

package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.tarot.TarotAnnounce;

/**
 * Created by sbaiget on 03/07/2014.
 */
public class TarotAnnounceWidget extends FrameLayout {

    private TarotAnnounce mAnnounce;

    public TarotAnnounceWidget(Context context) {
        this(context, null);
    }

    public TarotAnnounceWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TarotAnnounceWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lap_tarot_announce, this, true);

        ImageButton button = (ImageButton) view.findViewById(R.id.btn_remove_announce);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView((View) v.getParent());
            }
        });

        //Spinner spinner
    }

    public TarotAnnounce getAnnounce() {
        return mAnnounce;
    }

    public void setAnnounce(TarotAnnounce announce) {
        mAnnounce = announce;
    }

    class AnnounceItem {

        final int mAnnounce;

        AnnounceItem(int announce) {
            mAnnounce = announce;
        }

        public int getAnnounce() {
            return mAnnounce;
        }

        @Override
        public String toString() {
            Resources r = getResources();
            switch (mAnnounce) {
                case TarotAnnounce.TYPE_PETIT_AU_BOUT:
                    return r.getString(R.string.take);
                case TarotAnnounce.TYPE_POIGNEE_SIMPLE:
                    return r.getString(R.string.take);
                case TarotAnnounce.TYPE_POIGNEE_DOUBLE:
                    return r.getString(R.string.take);
                case TarotAnnounce.TYPE_MISERE_ATOUT:
                    return r.getString(R.string.take);
                case TarotAnnounce.TYPE_MISERE_TETE:
                    return r.getString(R.string.take);
            }
            return null;
        }
    }
}
