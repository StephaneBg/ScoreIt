/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Player;
import com.sbgapps.scoreit.core.model.universal.UniversalGame;

import java.util.ArrayList;

public class GameManager {

    public static final String KEY_PLAYED_GAME = "selected_game";
    public static final String KEY_UNIVERSAL_PLAYER_CNT = "universal_player_count";
    public static final String KEY_TAROT_PLAYER_CNT = "tarot_player_count";
    public static final String KEY_UNIVERSAL_TOTAL = "universal_show_total";
    public static final String KEY_BELOTE_ROUND = "belote_round_score";
    public static final String KEY_COINCHE_ROUND = "coinche_round_score";

    private final Context mContext;
    private Game mGame;

    public GameManager(Context context) {
        mContext = context;
    }

    public Game getGame() {
        if (null == mGame) {
            ArrayList<Player> players = new ArrayList<>(3);
            players.add(new Player("Titi", Color.parseColor("#F44336")));
            players.add(new Player("Riri", Color.parseColor("#9C27B0")));
            players.add(new Player("Fifi", Color.parseColor("#8BC34A")));
            mGame = new UniversalGame(players, new ArrayList<>());
        }
        return mGame;
    }

    public int getPlayerCount() {
        switch (getPlayedGame()) {
            case Game.BELOTE:
            case Game.COINCHE:
                return 2;
            default:
            case Game.UNIVERSAL:
                //return getPreferences().getInt(KEY_UNIVERSAL_PLAYER_CNT, 5) + (showTotal() ? 1 : 0);
                return 3;
            case Game.TAROT:
                return getPreferences().getInt(KEY_TAROT_PLAYER_CNT, 5);
        }
    }

    @SuppressWarnings("WrongConstant")
    public int getPlayedGame() {
        //return getPreferences().getInt(KEY_PLAYED_GAME, Game.UNIVERSAL);
        return Game.UNIVERSAL;
    }

    public void setPlayedGame(int game) {
        getPreferences().edit().putInt(KEY_PLAYED_GAME, game).apply();
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public boolean isRounded() {
        switch (getPlayedGame()) {
            default:
            case Game.TAROT:
            case Game.UNIVERSAL:
                return false;
            case Game.BELOTE:
                return getPreferences().getBoolean(KEY_BELOTE_ROUND, false);
            case Game.COINCHE:
                return getPreferences().getBoolean(KEY_COINCHE_ROUND, false);
        }
    }

    public boolean showTotal() {
        switch (getPlayedGame()) {
            default:
            case Game.TAROT:
            case Game.BELOTE:
            case Game.COINCHE:
                return false;
            case Game.UNIVERSAL:
                return getPreferences().getBoolean(KEY_UNIVERSAL_TOTAL, false);
        }
    }
}
