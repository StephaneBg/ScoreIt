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

package com.sbgapps.scoreit.models.belote;

import android.content.Context;
import android.content.res.Resources;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.models.Game;
import com.sbgapps.scoreit.models.Player;

/**
 * Created by sbaiget on 24/06/2014.
 */
public class BeloteGame extends Game<BeloteLap> {

    public final static int NB_PLAYERS = 2;

    public BeloteGame(Context context) {
        super(context, NB_PLAYERS);
    }

    @Override
    protected void initPlayers(Context context, int playerCount) {
        Resources r = context.getResources();
        mPlayers.add(new Player(r.getString(R.string.them)));
        mPlayers.add(new Player(r.getString(R.string.us)));
    }
}
