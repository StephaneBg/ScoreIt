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
import com.sbgapps.scoreit.games.belote.BeloteClassicGame;
import com.sbgapps.scoreit.games.belote.BeloteCoincheGame;
import com.sbgapps.scoreit.games.tarot.TarotFiveGame;
import com.sbgapps.scoreit.games.tarot.TarotFourGame;
import com.sbgapps.scoreit.games.tarot.TarotThreeGame;
import com.sbgapps.scoreit.games.universal.UniversalGame;

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
    public static final String KEY_UNIVERSAL_PLAYER_CNT = "player_count";

    private static GameHelper sInstance = new GameHelper();
    private Game mGame;
    private Context mContext;
    private int mPlayedGame;
    private SharedPreferences mPreferences;

    private GameHelper() {
    }

    public static GameHelper getInstance() {
        return sInstance;
    }

    public GameHelper init(Activity activity) {
        mContext = activity;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mPlayedGame = mPreferences.getInt(KEY_SELECTED_GAME, Game.UNIVERSAL);
        loadLaps();
        return this;
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
        String file = FileSaveUtil.getLastSavedFile(this);
        save(file, mGame);
    }

    public int getPlayerCount() {
        switch (mPlayedGame) {
            default:
                return mGame.getPlayers().size();
            case Game.UNIVERSAL:
                return mPreferences.getInt(KEY_UNIVERSAL_PLAYER_CNT, 5);
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
                        .putInt(KEY_UNIVERSAL_PLAYER_CNT, count)
                        .apply();
                loadLaps();
                break;
        }
    }

    private void loadLaps() {
        String file = FileSaveUtil.getLastSavedFile(this);
        switch (mPlayedGame) {
            default:
            case Game.UNIVERSAL:
                mGame = load(file, UniversalGame.class);
                if (null == mGame) {
                    mGame = new UniversalGame(mContext);
                }
                break;
            case Game.BELOTE_CLASSIC:
                mGame = load(file, BeloteClassicGame.class);
                if (null == mGame) {
                    mGame = new BeloteClassicGame(mContext);
                }
                break;
            case Game.BELOTE_COINCHE:
                mGame = load(file, BeloteCoincheGame.class);
                if (null == mGame) {
                    mGame = new BeloteCoincheGame(mContext);
                }
                break;
            case Game.TAROT_3_PLAYERS:
                mGame = load(file, TarotThreeGame.class);
                if (null == mGame) {
                    mGame = new TarotThreeGame(mContext);
                }
                break;
            case Game.TAROT_4_PLAYERS:
                mGame = load(file, TarotFourGame.class);
                if (null == mGame) {
                    mGame = new TarotFourGame(mContext);
                }
                break;
            case Game.TAROT_5_PLAYERS:
                mGame = load(file, TarotFiveGame.class);
                if (null == mGame) {
                    mGame = new TarotFiveGame(mContext);
                }
                break;
        }
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

    public String getPlayerName(int player) {
        return ((Player) mGame.getPlayers().get(player)).getName();
    }

    public void setPlayerName(int player, String name) {
        ((Player) mGame.getPlayers().get(player)).setName(name);
    }

    public int getPlayerColor(int player) {
        return ((Player) mGame.getPlayers().get(player)).getColor();
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
