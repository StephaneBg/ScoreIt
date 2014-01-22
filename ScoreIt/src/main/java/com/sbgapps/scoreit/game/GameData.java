/*
 * Copyright (C) 2013 SBG Apps
 * http://baiget.fr
 * stephane@baiget.fr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sbgapps.scoreit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 01/11/13.
 */
public class GameData {

    public static final int BELOTE_CLASSIC = 0;
    public static final int BELOTE_COINCHE = 1;
    public static final int TAROT_3_PLAYERS = 2;
    public static final int TAROT_4_PLAYERS = 3;
    public static final int TAROT_5_PLAYERS = 4;
    private static GameData sInstance = new GameData();
    private final List<Lap> mLaps = new ArrayList<>();
    private Resources mResources;
    private int mGame;
    private SQLiteDatabase mDatabase;
    private SharedPreferences mPreferences;

    private GameData() {
    }

    public static GameData getInstance() {
        return sInstance;
    }

    public void init(Activity activity, int game) {
        mResources = activity.getResources();
        GameSQLiteHelper helper = new GameSQLiteHelper(activity);
        mDatabase = helper.getWritableDatabase();
        mPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        setGame(game);
    }

    public int getGame() {
        return mGame;
    }

    public void setGame(int game) {
        mGame = game;
        loadLaps();
    }

    public int getPlayerCount() {
        switch (mGame) {
            default:
            case BELOTE_CLASSIC:
            case BELOTE_COINCHE:
                return 2;
            case TAROT_3_PLAYERS:
                return 3;
            case TAROT_4_PLAYERS:
                return 4;
            case TAROT_5_PLAYERS:
                return 5;
        }
    }

    private void loadLaps() {
        Lap lap;
        mLaps.clear();
        Cursor cursor = mDatabase.query(
                getTable(),
                getColumns(),
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            switch (mGame) {
                default:
                case BELOTE_CLASSIC:
                    lap = new ClassicBeloteLap(cursor);
                    break;
                case BELOTE_COINCHE:
                    lap = new CoincheBeloteLap(cursor);
                    break;
                case TAROT_3_PLAYERS:
                    lap = new ThreePlayerTarotLap(cursor);
                    break;
                case TAROT_4_PLAYERS:
                    lap = new FourPlayerTarotLap(cursor);
                    break;
                case TAROT_5_PLAYERS:
                    lap = new FivePlayerTarotLap(cursor);
                    break;
            }
            mLaps.add(lap);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void addLap(Lap lap) {
        long id = mDatabase.insert(
                getTable(),
                null,
                lap.getValues());
        lap.setId(id);
        mLaps.add(lap);
    }

    public void removeLap(Lap lap) {
        long id = lap.getId();
        mDatabase.delete(
                getTable(),
                GameSQLiteHelper.COLUMN_ID + "=" + id,
                null);
        mLaps.remove(lap);
    }

    public void editLap(Lap lap) {
        long id = lap.getId();
        mDatabase.update(
                getTable(),
                lap.getValues(),
                GameSQLiteHelper.COLUMN_ID + "=" + id,
                null);
        loadLaps();
    }

    public void deleteAll() {
        mDatabase.delete(
                getTable(),
                null,
                null);
        mLaps.clear();
    }

    public List<Lap> getLaps() {
        return mLaps;
    }

    public String getPlayerName(int player) {
        switch (mGame) {
            default:
            case BELOTE_CLASSIC:
            case BELOTE_COINCHE:
                switch (player) {
                    default:
                        return mResources.getString(R.string.none);
                    case Lap.PLAYER_1:
                        return mResources.getString(R.string.them);
                    case Lap.PLAYER_2:
                        return mResources.getString(R.string.us);
                }

            case TAROT_3_PLAYERS:
            case TAROT_4_PLAYERS:
            case TAROT_5_PLAYERS:
                switch (player) {
                    default:
                        return mResources.getString(R.string.none);
                    case Lap.PLAYER_1:
                        return mPreferences
                                .getString(getNameKey(player), "Riri");
                    case Lap.PLAYER_2:
                        return mPreferences
                                .getString(getNameKey(player), "Fifi");
                    case Lap.PLAYER_3:
                        return mPreferences
                                .getString(getNameKey(player), "Loulou");
                    case Lap.PLAYER_4:
                        return mPreferences
                                .getString(getNameKey(player), "Toto");
                    case Lap.PLAYER_5:
                        return mPreferences
                                .getString(getNameKey(player), "Titi");
                }
        }
    }

    public void setPlayerName(int player, String name) {
        mPreferences.edit()
                .putString(getNameKey(player), name)
                .commit();
    }

    public int getPlayerColor(int player) {
        switch (player) {
            default:
                return mResources.getColor(R.color.darker_gray);
            case Lap.PLAYER_1:
                return mResources.getColor(R.color.color_player1);
            case Lap.PLAYER_2:
                return mResources.getColor(R.color.color_player2);
            case Lap.PLAYER_3:
                return mResources.getColor(R.color.color_player3);
            case Lap.PLAYER_4:
                return mResources.getColor(R.color.color_player4);
            case Lap.PLAYER_5:
                return mResources.getColor(R.color.color_player5);
        }
    }

    private String getNameKey(int player) {
        return "TAROT_" + (mGame + 1) + "_PLAYERS_NAME" + (player + 1);
    }

    private String getTable() {
        switch (mGame) {
            default:
            case BELOTE_CLASSIC:
                return GameSQLiteHelper.BELOTE_TABLE_CLASSIC;
            case BELOTE_COINCHE:
                return GameSQLiteHelper.BELOTE_TABLE_COINCHE;
            case TAROT_3_PLAYERS:
                return GameSQLiteHelper.TAROT_TABLE_3_PLAYERS;
            case TAROT_4_PLAYERS:
                return GameSQLiteHelper.TAROT_TABLE_4_PLAYERS;
            case TAROT_5_PLAYERS:
                return GameSQLiteHelper.TAROT_TABLE_5_PLAYERS;
        }
    }

    private String[] getColumns() {
        switch (mGame) {
            default:
            case BELOTE_CLASSIC:
            case BELOTE_COINCHE:
                return GameSQLiteHelper.BELOTE_ALL_COLUMNS;
            case TAROT_3_PLAYERS:
                return GameSQLiteHelper.TAROT_3_PLAYERS_ALL_COLUMNS;
            case TAROT_4_PLAYERS:
                return GameSQLiteHelper.TAROT_4_PLAYERS_ALL_COLUMNS;
            case TAROT_5_PLAYERS:
                return GameSQLiteHelper.TAROT_5_PLAYERS_ALL_COLUMNS;
        }
    }
}
