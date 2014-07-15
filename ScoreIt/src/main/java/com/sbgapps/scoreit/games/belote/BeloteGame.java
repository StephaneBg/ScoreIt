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

package com.sbgapps.scoreit.games.belote;

import android.content.Context;
import android.content.res.Resources;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 24/06/2014.
 */
public class BeloteGame extends Game<BeloteClassicLap> {

    public BeloteGame(Context context) {
        mLaps = new ArrayList<>();
        mPlayers = new ArrayList<>(2);
        Resources r = context.getResources();
        mPlayers.add(new Player(r.getString(R.string.them), r.getColor(R.color.color_player1)));
        mPlayers.add(new Player(r.getString(R.string.us), r.getColor(R.color.color_player2)));
    }
}
