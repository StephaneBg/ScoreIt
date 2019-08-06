/*
 * Copyright 2019 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.cache.model.universal;

import android.content.Context;

import com.sbgapps.scoreit.cache.model.Game;
import com.sbgapps.scoreit.cache.model.Player;

import java.util.List;

/**
 * Created by sbaiget on 24/06/2014.
 */
public class UniversalGame extends Game<UniversalLap> {

    public UniversalGame(Context context, int playerCount) {
        super(context, playerCount);
    }

    public UniversalGame(List<Player> players, List<UniversalLap> laps) {
        super(laps, players);
    }
}
