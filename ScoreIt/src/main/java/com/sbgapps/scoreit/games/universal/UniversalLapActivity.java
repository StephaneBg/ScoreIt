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

package com.sbgapps.scoreit.games.universal;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.LapActivity;
import com.sbgapps.scoreit.games.Player;

/**
 * Created by sbaiget on 02/02/14.
 */
public class UniversalLapActivity extends LapActivity {

    @Override
    public UniversalLap getLap() {
        return (UniversalLap) super.getLap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lap_universal);

        if (null == savedInstanceState) {
            if (-1 != mPosition) {
                mLap = getGameHelper().getLaps().get(mPosition);
            } else {
                mLap = new UniversalLap(getGameHelper().getPlayerCount());
            }

            final FragmentManager fragmentManager = getSupportFragmentManager();
            int i = 0;
            for (Player player : getGameHelper().getPlayers()) {
                UniversalInputFragment fragment = UniversalInputFragment.newInstance(i);
                fragmentManager
                        .beginTransaction()
                        .add(R.id.ll_universal, fragment, UniversalInputFragment.TAG + i)
                        .commit();
                i++;
            }
        }
    }
}
