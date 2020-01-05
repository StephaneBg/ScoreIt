/*
 * Copyright 2019 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.cache.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.sbgapps.scoreit.cache.model.Game;
import com.sbgapps.scoreit.cache.model.belote.BeloteGame;
import com.sbgapps.scoreit.cache.model.coinche.CoincheGame;
import com.sbgapps.scoreit.cache.model.tarot.TarotFiveGame;
import com.sbgapps.scoreit.cache.model.tarot.TarotFourGame;
import com.sbgapps.scoreit.cache.model.tarot.TarotThreeGame;
import com.sbgapps.scoreit.cache.model.universal.UniversalGame;
import com.sbgapps.scoreit.data.repository.PreferencesRepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by sbaiget on 01/11/13.
 */
public class GameManager {

    final private Context mContext;
    final private PreferencesRepo mPrefsRepo;
    final private StorageManager mStorageManager;

    public GameManager(Context context, StorageManager storageManager, PreferencesRepo prefsRepo) {
        mContext = context;
        mPrefsRepo = prefsRepo;
        mStorageManager = storageManager;
    }

    public void saveGame(Game game) {
        final File file = mStorageManager.getPlayedFile();
        save(game, file);
    }

    public Game loadGame() {
        Game game = null;
        switch (mPrefsRepo.getCurrentGame()) {
            default:
            case UNIVERSAL:
                game = load(UniversalGame.class);
                break;
            case BELOTE:
                game = load(BeloteGame.class);
                break;
            case COINCHE:
                game = load(CoincheGame.class);
                break;
            case TAROT:
                switch (mPrefsRepo.getPlayerCount()) {
                    case 3:
                        game = load(TarotThreeGame.class);
                        break;
                    case 4:
                        game = load(TarotFourGame.class);
                        break;
                    case 5:
                        game = load(TarotFiveGame.class);
                        break;
                }
                break;
        }
        if (null == game) game = createGame();
        return game;
    }

    public Game createGame(String fileName) {
        File file = mStorageManager.createFile(fileName);
        Game game = createGame();
        save(game, file);
        return game;
    }

    private Game createGame() {
        Game game = null;
        switch (mPrefsRepo.getCurrentGame()) {
            default:
            case UNIVERSAL:
                game = new UniversalGame(mContext, mPrefsRepo.getPlayerCount());
                break;
            case BELOTE:
                game = new BeloteGame(mContext);
                break;
            case COINCHE:
                game = new CoincheGame(mContext);
                break;
            case TAROT:
                switch (mPrefsRepo.getPlayerCount()) {
                    case 3:
                        game = new TarotThreeGame(mContext);
                        break;
                    case 4:
                        game = new TarotFourGame(mContext);
                        break;
                    case 5:
                        game = new TarotFiveGame(mContext);
                        break;
                }
                break;
        }
        mStorageManager.setPlayedFile("default");
        return game;
    }

    private <T> T load(final Class<T> clazz) {
        try {
            final Gson g = new Gson();
            final File file = mStorageManager.getPlayedFile();
            final FileInputStream is = new FileInputStream(file);
            final BufferedReader r = new BufferedReader(new InputStreamReader(is));
            return g.fromJson(r, clazz);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void save(Game game, final File file) {
        try {
            final Gson g = new Gson();
            final FileOutputStream os = new FileOutputStream(file);
            os.write(g.toJson(game).getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
