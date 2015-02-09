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

package com.sbgapps.scoreit.games.universal;

import android.content.Context;
import android.content.res.Resources;

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
                mPlayers.add(new Player("Riri"));
                mPlayers.add(new Player("Fifi"));
                break;
            case 3:
                mPlayers.add(new Player("Riri"));
                mPlayers.add(new Player("Fifi"));
                mPlayers.add(new Player("Loulou"));
                break;
            case 4:
                mPlayers.add(new Player("Riri"));
                mPlayers.add(new Player("Fifi"));
                mPlayers.add(new Player("Loulou"));
                mPlayers.add(new Player("Toto"));
                break;
            case 5:
                mPlayers.add(new Player("Riri"));
                mPlayers.add(new Player("Fifi"));
                mPlayers.add(new Player("Loulou"));
                mPlayers.add(new Player("Toto"));
                mPlayers.add(new Player("Titi"));
                break;
            case 6:
                mPlayers.add(new Player("Riri"));
                mPlayers.add(new Player("Fifi"));
                mPlayers.add(new Player("Loulou"));
                mPlayers.add(new Player("Toto"));
                mPlayers.add(new Player("Titi"));
                mPlayers.add(new Player("Lulu"));
                break;
            case 7:
                mPlayers.add(new Player("Riri"));
                mPlayers.add(new Player("Fifi"));
                mPlayers.add(new Player("Loulou"));
                mPlayers.add(new Player("Toto"));
                mPlayers.add(new Player("Titi"));
                mPlayers.add(new Player("Lulu"));
                mPlayers.add(new Player("Lili"));
                break;
            case 8:
                mPlayers.add(new Player("Riri"));
                mPlayers.add(new Player("Fifi"));
                mPlayers.add(new Player("Loulou"));
                mPlayers.add(new Player("Toto"));
                mPlayers.add(new Player("Titi"));
                mPlayers.add(new Player("Lulu"));
                mPlayers.add(new Player("Lili"));
                mPlayers.add(new Player("Tutu"));
                break;
        }
    }
}
