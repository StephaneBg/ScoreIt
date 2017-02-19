package com.sbgapps.scoreit.core.model.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Lap;
import com.sbgapps.scoreit.core.model.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 28/12/2016.
 */

public class GameHelper {

    public static final String KEY_PLAYED_GAME = "selected_game";
    public static final String KEY_UNIVERSAL_PLAYER_CNT = "universal_player_count";
    public static final String KEY_TAROT_PLAYER_CNT = "tarot_player_count";
    public static final String KEY_UNIVERSAL_TOTAL = "universal_show_total";
    public static final String KEY_BELOTE_ROUND = "belote_round_score";
    public static final String KEY_COINCHE_ROUND = "coinche_round_score";

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    @SuppressWarnings("WrongConstant")
    @Game.Games
    public static int getPlayedGame() {
        //return getPreferences().getInt(KEY_PLAYED_GAME, Game.UNIVERSAL);
        return Game.UNIVERSAL;
    }

    public static void setPlayedGame(@Game.Games int game) {
        getPreferences().edit().putInt(KEY_PLAYED_GAME, game).apply();
    }

    public static int getPlayerCount() {
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

    public static boolean isRounded() {
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

    public static boolean showTotal() {
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

    public static int getScore(ArrayList<? extends Lap> laps, @Player.Players int player) {
        int score = 0;
        for (Lap lap : laps) {
            score += lap.getScore(player);
        }
        return score;
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}
