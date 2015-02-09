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

package com.sbgapps.scoreit.fragments;

import android.widget.ArrayAdapter;

import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.GenericBeloteLap;

/**
 * Created by sbaiget on 29/10/2014.
 */
public class GenericBeloteLapFragment extends LapFragment {

    public GenericBeloteLap getLap() {
        return (GenericBeloteLap) super.getLap();
    }

    protected ArrayAdapter<PlayerItem> getPlayerArrayAdapter() {
        ArrayAdapter<PlayerItem> playerItemArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        playerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_1));
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_2));
        return playerItemArrayAdapter;
    }
}
