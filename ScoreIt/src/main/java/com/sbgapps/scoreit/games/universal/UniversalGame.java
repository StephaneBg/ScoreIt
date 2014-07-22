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

import android.content.Context;
import android.content.res.Resources;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 24/06/2014.
 */
public class UniversalGame extends Game<UniversalLap> {

    public UniversalGame(Context context, int playerCount) {
        mLaps = new ArrayList<>();
        mPlayers = new ArrayList<>(playerCount);
        Resources r = context.getResources();
        switch (playerCount) {
            case 2:
                mPlayers.add(new Player("Riri", r.getColor(R.color.color_player1)));
                mPlayers.add(new Player("Fifi", r.getColor(R.color.color_player2)));
                break;
            case 3:
                mPlayers.add(new Player("Riri", r.getColor(R.color.color_player1)));
                mPlayers.add(new Player("Fifi", r.getColor(R.color.color_player2)));
                mPlayers.add(new Player("Loulou", r.getColor(R.color.color_player3)));
                break;
            case 4:
                mPlayers.add(new Player("Riri", r.getColor(R.color.color_player1)));
                mPlayers.add(new Player("Fifi", r.getColor(R.color.color_player2)));
                mPlayers.add(new Player("Loulou", r.getColor(R.color.color_player3)));
                mPlayers.add(new Player("Toto", r.getColor(R.color.color_player4)));
                break;
            case 5:
                mPlayers.add(new Player("Riri", r.getColor(R.color.color_player1)));
                mPlayers.add(new Player("Fifi", r.getColor(R.color.color_player2)));
                mPlayers.add(new Player("Loulou", r.getColor(R.color.color_player3)));
                mPlayers.add(new Player("Toto", r.getColor(R.color.color_player4)));
                mPlayers.add(new Player("Titi", r.getColor(R.color.color_player5)));
                break;
        }
    }
}
