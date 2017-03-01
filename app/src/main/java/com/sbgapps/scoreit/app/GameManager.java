package com.sbgapps.scoreit.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Player;
import com.sbgapps.scoreit.core.model.universal.UniversalGame;

import java.util.ArrayList;

/**
 * Created by sbaiget on 28/12/2016.
 */

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

    @SuppressWarnings("WrongConstant")
    @Game.Games
    public int getPlayedGame() {
        //return getPreferences().getInt(KEY_PLAYED_GAME, Game.UNIVERSAL);
        return Game.UNIVERSAL;
    }

    public void setPlayedGame(@Game.Games int game) {
        getPreferences().edit().putInt(KEY_PLAYED_GAME, game).apply();
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

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}
