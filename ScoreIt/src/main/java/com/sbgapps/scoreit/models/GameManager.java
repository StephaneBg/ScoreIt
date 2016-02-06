/*
 * Copyright (c) 2016 SBG Apps
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

package com.sbgapps.scoreit.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.models.Game;
import com.sbgapps.scoreit.models.Lap;
import com.sbgapps.scoreit.models.Player;
import com.sbgapps.scoreit.models.belote.BeloteGame;
import com.sbgapps.scoreit.models.coinche.CoincheGame;
import com.sbgapps.scoreit.models.tarot.TarotFiveGame;
import com.sbgapps.scoreit.models.tarot.TarotFourGame;
import com.sbgapps.scoreit.models.tarot.TarotThreeGame;
import com.sbgapps.scoreit.models.universal.UniversalGame;
import com.sbgapps.scoreit.utils.FileUtils;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by sbaiget on 01/11/13.
 */
public class GameManager {

    public static final String KEY_SELECTED_GAME = "selected_game";
    public static final String KEY_UNIVERSAL_PLAYER_CNT = "universal_player_count";
    public static final String KEY_TAROT_PLAYER_CNT = "tarot_player_count";
    public static final String KEY_UNIVERSAL_TOTAL = "universal_show_total";

    final private Context mContext;
    final private SharedPreferences mPreferences;
    final private Storage mStorage;
    final private FileUtils mFileUtils;
    final private int[] mColors = new int[Player.PLAYER_COUNT + 1];
    private Game mGame;
    private int mPlayedGame;
    private Player mPlayerTotal;

    public GameManager(Activity activity) {
        mContext = activity;
        mStorage = SimpleStorage.getInternalStorage(mContext);
        mFileUtils = new FileUtils(this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mPlayedGame = mPreferences.getInt(KEY_SELECTED_GAME, Game.UNIVERSAL);

        Resources r = activity.getResources();
        final TypedArray ta = r.obtainTypedArray(R.array.player_colors);
        for (int i = 0; i < mColors.length; i++)
            mColors[i] = ta.getColor(i, 0);
        ta.recycle();
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
        loadGame();
    }

    public Storage getStorage() {
        return mStorage;
    }

    public FileUtils getFileUtils() {
        return mFileUtils;
    }

    public void saveGame() {
        final File file = mFileUtils.getPlayedFile();
        save(file);
    }

    public void saveGame(String fileName) {
        File file = mFileUtils.createFile(fileName);
        save(file);
    }

    public int getPlayerCount() {
        return getPlayerCount(false);
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
                loadGame();
                break;
            case Game.TAROT:
                saveGame();
                mPreferences
                        .edit()
                        .putInt(KEY_TAROT_PLAYER_CNT, count + 3)
                        .apply();
                loadGame();
                break;
        }
    }

    public int getPlayerCount(boolean withTotal) {
        switch (mPlayedGame) {
            default:
                // Belote and Coinche
                return 2;
            case Game.UNIVERSAL:
                int count = mPreferences.getInt(KEY_UNIVERSAL_PLAYER_CNT, 5);
                if (withTotal && mPreferences.getBoolean(KEY_UNIVERSAL_TOTAL, false)) {
                    count++;
                }
                return count;
            case Game.TAROT:
                return mPreferences.getInt(KEY_TAROT_PLAYER_CNT, 5);
        }
    }

    public void loadGame() {
        switch (mPlayedGame) {
            default:
            case Game.UNIVERSAL:
                mGame = load(UniversalGame.class);
                break;
            case Game.BELOTE:
                mGame = load(BeloteGame.class);
                break;
            case Game.COINCHE:
                mGame = load(CoincheGame.class);
                break;
            case Game.TAROT:
                switch (getPlayerCount()) {
                    case 3:
                        mGame = load(TarotThreeGame.class);
                        break;
                    case 4:
                        mGame = load(TarotFourGame.class);
                        break;
                    case 5:
                        mGame = load(TarotFiveGame.class);
                        break;
                }
                break;
        }
        if (null == mGame) {
            createGame();
        }
        mGame.initScores();
    }

    public void createGame() {
        switch (mPlayedGame) {
            default:
            case Game.UNIVERSAL:
                mGame = new UniversalGame(mContext, getPlayerCount());
                break;
            case Game.BELOTE:
                mGame = new BeloteGame(mContext);
                break;
            case Game.COINCHE:
                mGame = new CoincheGame(mContext);
                break;
            case Game.TAROT:
                switch (getPlayerCount()) {
                    case 3:
                        mGame = new TarotThreeGame(mContext);
                        break;
                    case 4:
                        mGame = new TarotFourGame(mContext);
                        break;
                    case 5:
                        mGame = new TarotFiveGame(mContext);
                        break;
                }
                break;
        }
        mFileUtils.setPlayedFile("default");
    }

    public void addLap(Lap lap) {
        mGame.getLaps().add(lap);
    }

    public int removeLap(Lap lap) {
        int p = mGame.getLaps().indexOf(lap);
        mGame.getLaps().remove(lap);
        return p;
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
        if (player >= getPlayers().size()) {
            if (null == mPlayerTotal) mPlayerTotal = new Player(mContext.getString(R.string.total));
            return mPlayerTotal;
        } else {
            return getPlayers().get(player);
        }
    }

    public int getPlayerColor(int player) {
        return mColors[player];
    }

    private <T> T load(final Class<T> clazz) {
        try {
            final Gson g = new Gson();
            final File file = mFileUtils.getPlayedFile();
            final FileInputStream is = new FileInputStream(file);
            final BufferedReader r = new BufferedReader(
                    new InputStreamReader(is));
            return g.fromJson(r, clazz);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void save(final File file) {
        try {
            final Gson g = new Gson();
            final FileOutputStream os = new FileOutputStream(file);
            os.write(g.toJson(mGame).getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
