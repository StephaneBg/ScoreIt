package com.sbgapps.scoreit.game;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by sbaiget on 06/02/14.
 */
public class UniversalLap implements Lap {

    private long mId;
    private final int[] mScores = new int[PLAYER_COUNT_MAX];

    public UniversalLap(Cursor cursor) {
        mId = cursor.getLong(GameSQLiteHelper.COLUMN_IDX_ID);
        mScores[PLAYER_1] = cursor.getInt(GameSQLiteHelper.UNIVERSAL_COLUMN_IDX_SCORE_1);
        mScores[PLAYER_2] = cursor.getInt(GameSQLiteHelper.UNIVERSAL_COLUMN_IDX_SCORE_2);
        mScores[PLAYER_3] = cursor.getInt(GameSQLiteHelper.UNIVERSAL_COLUMN_IDX_SCORE_3);
        mScores[PLAYER_4] = cursor.getInt(GameSQLiteHelper.UNIVERSAL_COLUMN_IDX_SCORE_4);
        mScores[PLAYER_5] = cursor.getInt(GameSQLiteHelper.UNIVERSAL_COLUMN_IDX_SCORE_5);
    }

    public UniversalLap() {
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public void setId(long id) {
        mId = id;
    }

    @Override
    public void setScores() {
        // Nothing to do!
    }

    public void setScore(int player, int score) {
        mScores[player] = score;
    }

    @Override
    public int getScore(int player) {
        return mScores[player];
    }

    @Override
    public ContentValues getValues() {
        ContentValues val = new ContentValues();
        val.put(GameSQLiteHelper.UNIVERSAL_COLUMN_SCORE_1, mScores[PLAYER_1]);
        val.put(GameSQLiteHelper.UNIVERSAL_COLUMN_SCORE_2, mScores[PLAYER_2]);
        val.put(GameSQLiteHelper.UNIVERSAL_COLUMN_SCORE_3, mScores[PLAYER_3]);
        val.put(GameSQLiteHelper.UNIVERSAL_COLUMN_SCORE_4, mScores[PLAYER_4]);
        val.put(GameSQLiteHelper.UNIVERSAL_COLUMN_SCORE_5, mScores[PLAYER_5]);
        return val;
    }
}
