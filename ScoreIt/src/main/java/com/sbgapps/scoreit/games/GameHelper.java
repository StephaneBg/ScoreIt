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

package com.sbgapps.scoreit.games;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sbgapps.scoreit.games.belote.BeloteGame;
import com.sbgapps.scoreit.games.coinche.CoincheGame;
import com.sbgapps.scoreit.games.tarot.TarotFiveGame;
import com.sbgapps.scoreit.games.tarot.TarotFourGame;
import com.sbgapps.scoreit.games.tarot.TarotThreeGame;
import com.sbgapps.scoreit.games.universal.UniversalGame;
import com.sbgapps.scoreit.util.FileSaveUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by sbaiget on 01/11/13.
 */
public class GameHelper {

    public static final String KEY_SELECTED_GAME = "selected_game";
    public static final String KEY_UNIVERSAL_PLAYER_CNT = "universal_player_count";
    public static final String KEY_TAROT_PLAYER_CNT = "tarot_player_count";

    private Game mGame;
    private Context mContext;
    private int mPlayedGame;
    private SharedPreferences mPreferences;

    public GameHelper(Activity activity) {
        mContext = activity;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mPlayedGame = mPreferences.getInt(KEY_SELECTED_GAME, Game.UNIVERSAL);
    }

    public SharedPreferences getPreferences() {
        return mPreferences;
    }

    public Context getContext() {
        return mContext;
    }

    public int getPlayedGame() {
        return mPlayedGame;
    }

    public void setPlayedGame(int playedGame) {
        saveGame();
        mPlayedGame = playedGame;
        mPreferences
                .edit()
                .putInt(KEY_SELECTED_GAME, playedGame)
                .apply();
        loadLaps();
    }

    public void saveGame() {
        save(FileSaveUtil.getLastSavedFile(this), mGame);
    }

    public void saveGame(String file) {
        saveGame();
        switch (mPlayedGame) {
            default:
            case Game.UNIVERSAL:
                file = "universal_" + getPlayerCount() + "_" + file;
                break;
            case Game.BELOTE:
                file = "belote_" + file;
                break;
            case Game.COINCHE:
                file = "coinche_" + file;
                break;
            case Game.TAROT:
                file = "tarot_" + getPlayerCount() + "_" + file;
                break;
        }
        FileSaveUtil.setLastSavedFile(file, this);
        save(file, mGame);
    }

    public int getPlayerCount() {
        switch (mPlayedGame) {
            default:
                // Belote and Coinche
                return 2;
            case Game.UNIVERSAL:
                return mPreferences.getInt(KEY_UNIVERSAL_PLAYER_CNT, 5);
            case Game.TAROT:
                return mPreferences.getInt(KEY_TAROT_PLAYER_CNT, 5);
        }
    }

    public void setPlayerCount(int count) {
        switch (mPlayedGame) {
            default:
                break;
            case Game.UNIVERSAL:
                saveGame();
                mPreferences
                        .edit()
                        .putInt(KEY_UNIVERSAL_PLAYER_CNT, count + 2)
                        .apply();
                loadLaps();
                break;
            case Game.TAROT:
                saveGame();
                mPreferences
                        .edit()
                        .putInt(KEY_TAROT_PLAYER_CNT, count + 3)
                        .apply();
                loadLaps();
                break;
        }
    }

    public void loadLaps() {
        String file = FileSaveUtil.getLastSavedFile(this);
        switch (mPlayedGame) {
            default:
            case Game.UNIVERSAL:
                mGame = load(file, UniversalGame.class);
                if (null == mGame) {
                    mGame = new UniversalGame(mContext, getPlayerCount());
                }
                break;
            case Game.BELOTE:
                mGame = load(file, BeloteGame.class);
                if (null == mGame) {
                    mGame = new BeloteGame(mContext);
                }
                break;
            case Game.COINCHE:
                mGame = load(file, CoincheGame.class);
                if (null == mGame) {
                    mGame = new CoincheGame(mContext);
                }
                break;
            case Game.TAROT:
                switch (getPlayerCount()) {
                    case 3:
                        mGame = load(file, TarotThreeGame.class);
                        if (null == mGame) {
                            mGame = new TarotThreeGame(mContext);
                        }
                        break;
                    case 4:
                        mGame = load(file, TarotFourGame.class);
                        if (null == mGame) {
                            mGame = new TarotFourGame(mContext);
                        }
                        break;
                    case 5:
                        mGame = load(file, TarotFiveGame.class);
                        if (null == mGame) {
                            mGame = new TarotFiveGame(mContext);
                        }
                        break;
                }
                break;
        }
        mGame.initScores();
    }

    public void addLap(Lap lap) {
        mGame.getLaps().add(lap);
    }

    public void removeLap(Lap lap) {
        mGame.getLaps().remove(lap);
    }

    public void deleteAll() {
        mGame.getLaps().clear();
    }

    public List<Lap> getLaps() {
        return mGame.getLaps();
    }

    public List<Player> getPlayers() {
        return mGame.getPlayers();
    }

    public Player getPlayer(int player) {
        return getPlayers().get(player);
    }

    private <T> T load(final String file, final Class<T> clazz) {
        try {
            final Gson g = new Gson();
            final FileInputStream is = mContext.openFileInput(file);
            final BufferedReader r = new BufferedReader(
                    new InputStreamReader(is));
            return g.fromJson(r, clazz);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void save(final String file, Object laps) {
        try {
            final Gson g = new Gson();
            FileOutputStream os = mContext.openFileOutput(file, Context.MODE_PRIVATE);
            os.write(g.toJson(laps).getBytes());
            os.close();
        } catch (Exception e) {
        }
    }
}
