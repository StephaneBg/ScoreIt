package com.sbgapps.scoreit.app.universal;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sbgapps.scoreit.app.GameManager;
import com.sbgapps.scoreit.app.ScoreItApp;

/**
 * Created by sbaiget on 03/03/2017.
 */

class UniversalPresenter extends MvpBasePresenter<UniversalViewActions> {

    final private GameManager mGameManager;

    public UniversalPresenter() {
        mGameManager = ScoreItApp.getGameManager();
    }


}
