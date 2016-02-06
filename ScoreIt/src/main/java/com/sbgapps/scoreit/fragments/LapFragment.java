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

package com.sbgapps.scoreit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.models.Lap;
import com.sbgapps.scoreit.models.GameManager;

/**
 * Created by sbaiget on 08/01/14.
 */
public class LapFragment extends Fragment {

    public static final String TAG = LapFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public GameManager getGameHelper() {
        return ((ScoreItActivity) getActivity()).getGameManager();
    }

    public Lap getLap() {
        return ((ScoreItActivity) getActivity()).getLap();
    }

    class PlayerItem {

        final int mPlayer;

        public PlayerItem(int player) {
            mPlayer = player;
        }

        @Override
        public String toString() {
            return getGameHelper().getPlayer(mPlayer).getName();
        }
    }
}
