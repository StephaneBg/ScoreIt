package com.sbgapps.scoreit.app;

import android.app.Application;

/**
 * Created by sbaiget on 20/02/2017.
 */

public class ScoreItApp extends Application {

    private static GameManager sGameManager;

    public static GameManager getGameManager() {
        return sGameManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        if (null == sGameManager) sGameManager = new GameManager(getApplicationContext());
    }
}
