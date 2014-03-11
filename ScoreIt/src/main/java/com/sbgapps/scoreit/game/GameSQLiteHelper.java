/*
 * Copyright 2013 SBG Apps
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.sbgapps.scoreit.game;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sbaiget on 25/10/13.
 */
public class GameSQLiteHelper extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "_id";
    public static final int COLUMN_IDX_ID = 0;
    /*
     * Universal
     */
    public static final String UNIVERSAL_TABLE = "UNIVERSAL_TABLE";
    public static final String UNIVERSAL_COLUMN_SCORE_1 = "score_player1";
    public static final int UNIVERSAL_COLUMN_IDX_SCORE_1 = 1;
    public static final String UNIVERSAL_COLUMN_SCORE_2 = "score_player2";
    public static final int UNIVERSAL_COLUMN_IDX_SCORE_2 = 2;
    public static final String UNIVERSAL_COLUMN_SCORE_3 = "score_player3";
    public static final int UNIVERSAL_COLUMN_IDX_SCORE_3 = 3;
    public static final String UNIVERSAL_COLUMN_SCORE_4 = "score_player4";
    public static final int UNIVERSAL_COLUMN_IDX_SCORE_4 = 4;
    public static final String UNIVERSAL_COLUMN_SCORE_5 = "score_player5";
    public static final String[] UNIVERSAL_ALL_COLUMNS = {
            COLUMN_ID, UNIVERSAL_COLUMN_SCORE_1,
            UNIVERSAL_COLUMN_SCORE_2, UNIVERSAL_COLUMN_SCORE_3,
            UNIVERSAL_COLUMN_SCORE_4, UNIVERSAL_COLUMN_SCORE_5};
    public static final int UNIVERSAL_COLUMN_IDX_SCORE_5 = 5;
    /*
     * Belote
     */
    public static final String BELOTE_TABLE_CLASSIC = "BELOTE_TABLE_CLASSIC";
    public static final String BELOTE_TABLE_COINCHE = "BELOTE_TABLE_COINCHE";
    public static final String BELOTE_COLUMN_TAKER = "taker";
    public static final int BELOTE_COLUMN_IDX_TAKER = 1;
    public static final String BELOTE_COLUMN_POINTS = "points";
    public static final int BELOTE_COLUMN_IDX_POINTS = 2;
    public static final String BELOTE_COLUMN_BELOTE = "belote";
    public static final int BELOTE_COLUMN_IDX_BELOTE = 3;
    public static final String BELOTE_COLUMN_SCORE_1 = "score_player1";
    public static final int BELOTE_COLUMN_IDX_SCORE_1 = 4;
    public static final String BELOTE_COLUMN_SCORE_2 = "score_player2";
    public static final String[] BELOTE_CLASSIC_ALL_COLUMNS = {
            COLUMN_ID, BELOTE_COLUMN_TAKER,
            BELOTE_COLUMN_POINTS, BELOTE_COLUMN_BELOTE,
            BELOTE_COLUMN_SCORE_1, BELOTE_COLUMN_SCORE_2};
    public static final int BELOTE_COLUMN_IDX_SCORE_2 = 5;
    public static final String BELOTE_COLUMN_DEAL = "deal";
    public static final int BELOTE_COLUMN_IDX_DEAL = 6;
    public static final String BELOTE_COLUMN_COINCHE = "coinche";
    public static final String[] BELOTE_COINCHE_ALL_COLUMNS = {
            COLUMN_ID, BELOTE_COLUMN_TAKER,
            BELOTE_COLUMN_POINTS, BELOTE_COLUMN_BELOTE,
            BELOTE_COLUMN_SCORE_1, BELOTE_COLUMN_SCORE_2,
            BELOTE_COLUMN_DEAL, BELOTE_COLUMN_COINCHE};
    public static final int BELOTE_COLUMN_IDX_COINCHE = 7;
    /*
     * Tarot
     */
    public static final String TAROT_TABLE_3_PLAYERS = "TAROT_TABLE_3_PLAYERS";
    public static final String TAROT_TABLE_4_PLAYERS = "TAROT_TABLE_4_PLAYERS";
    public static final String TAROT_TABLE_5_PLAYERS = "TAROT_TABLE_5_PLAYERS";
    public static final String TAROT_COLUMN_TAKER = "taker";
    public static final int TAROT_COLUMN_IDX_TAKER = 1;
    public static final String TAROT_COLUMN_DEAL = "deal";
    public static final int TAROT_COLUMN_IDX_DEAL = 2;
    public static final String TAROT_COLUMN_POINTS = "points";
    public static final int TAROT_COLUMN_IDX_POINTS = 3;
    public static final String TAROT_COLUMN_OUDLER = "oudler";
    public static final int TAROT_COLUMN_IDX_OUDLER = 4;
    public static final String TAROT_COLUMN_SCORE_1 = "score_player1";
    public static final int TAROT_COLUMN_IDX_SCORE_1 = 5;
    public static final String TAROT_COLUMN_SCORE_2 = "score_player2";
    public static final int TAROT_COLUMN_IDX_SCORE_2 = 6;
    public static final String TAROT_COLUMN_SCORE_3 = "score_player3";
    public static final String[] TAROT_3_PLAYERS_ALL_COLUMNS = {
            COLUMN_ID, TAROT_COLUMN_TAKER, TAROT_COLUMN_DEAL,
            TAROT_COLUMN_POINTS, TAROT_COLUMN_OUDLER,
            TAROT_COLUMN_SCORE_1, TAROT_COLUMN_SCORE_2,
            TAROT_COLUMN_SCORE_3};
    public static final int TAROT_COLUMN_IDX_SCORE_3 = 7;
    public static final String TAROT_COLUMN_SCORE_4 = "score_player4";
    public static final String[] TAROT_4_PLAYERS_ALL_COLUMNS = {
            COLUMN_ID, TAROT_COLUMN_TAKER, TAROT_COLUMN_DEAL,
            TAROT_COLUMN_POINTS, TAROT_COLUMN_OUDLER,
            TAROT_COLUMN_SCORE_1, TAROT_COLUMN_SCORE_2,
            TAROT_COLUMN_SCORE_3, TAROT_COLUMN_SCORE_4};
    public static final int TAROT_COLUMN_IDX_SCORE_4 = 8;
    public static final String TAROT_COLUMN_SCORE_5 = "score_player5";
    public static final int TAROT_COLUMN_IDX_SCORE_5 = 9;
    public static final String TAROT_COLUMN_PARTNER = "partner";
    public static final String[] TAROT_5_PLAYERS_ALL_COLUMNS = {
            COLUMN_ID, TAROT_COLUMN_TAKER, TAROT_COLUMN_DEAL,
            TAROT_COLUMN_POINTS, TAROT_COLUMN_OUDLER,
            TAROT_COLUMN_SCORE_1, TAROT_COLUMN_SCORE_2,
            TAROT_COLUMN_SCORE_3, TAROT_COLUMN_SCORE_4,
            TAROT_COLUMN_SCORE_5, TAROT_COLUMN_PARTNER};
    public static final int TAROT_COLUMN_IDX_PARTNER = 10;
    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION_1 = 1;
    private static final int DATABASE_VERSION_2 = 2;
    private static final int DATABASE_VERSION_3 = 3;
    private static final int DATABASE_VERSION = DATABASE_VERSION_3;

    public GameSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUniversalTable(db);
        createClassicBeloteTable(db);
        createCoincheBeloteTable(db);
        createTarotThreePlayerTable(db);
        createTarotFourPlayerTable(db);
        createTarotFivePlayerTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DATABASE_VERSION_1 == oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + BELOTE_TABLE_COINCHE);
            createCoincheBeloteTable(db);
        }
        if (oldVersion < DATABASE_VERSION_3) {
            createUniversalTable(db);
        }
    }

    private void createUniversalTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UNIVERSAL_TABLE
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UNIVERSAL_COLUMN_SCORE_1 + " INTEGER NOT NULL, "
                + UNIVERSAL_COLUMN_SCORE_2 + " INTEGER NOT NULL, "
                + UNIVERSAL_COLUMN_SCORE_3 + " INTEGER NOT NULL, "
                + UNIVERSAL_COLUMN_SCORE_4 + " INTEGER NOT NULL, "
                + UNIVERSAL_COLUMN_SCORE_5 + " INTEGER NOT NULL"
                + ");");
    }

    private void createClassicBeloteTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BELOTE_TABLE_CLASSIC
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BELOTE_COLUMN_TAKER + " INTEGER NOT NULL, "
                + BELOTE_COLUMN_POINTS + " INTEGER NOT NULL, "
                + BELOTE_COLUMN_BELOTE + " INTEGER NOT NULL, "
                + BELOTE_COLUMN_SCORE_1 + " INTEGER NOT NULL, "
                + BELOTE_COLUMN_SCORE_2 + " INTEGER NOT NULL"
                + ");");
    }

    private void createCoincheBeloteTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BELOTE_TABLE_COINCHE
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BELOTE_COLUMN_TAKER + " INTEGER NOT NULL, "
                + BELOTE_COLUMN_POINTS + " INTEGER NOT NULL, "
                + BELOTE_COLUMN_BELOTE + " INTEGER NOT NULL, "
                + BELOTE_COLUMN_SCORE_1 + " INTEGER NOT NULL, "
                + BELOTE_COLUMN_SCORE_2 + " INTEGER NOT NULL,"
                + BELOTE_COLUMN_DEAL + " INTEGER NOT NULL,"
                + BELOTE_COLUMN_COINCHE + " INTEGER NOT NULL"
                + ");");
    }

    private void createTarotThreePlayerTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TAROT_TABLE_3_PLAYERS
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TAROT_COLUMN_TAKER + " INTEGER NOT NULL, "
                + TAROT_COLUMN_DEAL + " INTEGER NOT NULL, "
                + TAROT_COLUMN_POINTS + " INTEGER NOT NULL, "
                + TAROT_COLUMN_OUDLER + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_1 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_2 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_3 + " INTEGER NOT NULL"
                + ");");
    }

    private void createTarotFourPlayerTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TAROT_TABLE_4_PLAYERS
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TAROT_COLUMN_TAKER + " INTEGER NOT NULL, "
                + TAROT_COLUMN_DEAL + " INTEGER NOT NULL, "
                + TAROT_COLUMN_POINTS + " INTEGER NOT NULL, "
                + TAROT_COLUMN_OUDLER + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_1 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_2 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_3 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_4 + " INTEGER NOT NULL"
                + ");");
    }

    private void createTarotFivePlayerTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TAROT_TABLE_5_PLAYERS
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TAROT_COLUMN_TAKER + " INTEGER NOT NULL, "
                + TAROT_COLUMN_DEAL + " INTEGER NOT NULL, "
                + TAROT_COLUMN_POINTS + " INTEGER NOT NULL, "
                + TAROT_COLUMN_OUDLER + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_1 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_2 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_3 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_4 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_SCORE_5 + " INTEGER NOT NULL, "
                + TAROT_COLUMN_PARTNER + " INTEGER NOT NULL"
                + ");");
    }
}
