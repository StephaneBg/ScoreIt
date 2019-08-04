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

package com.sbgapps.scoreit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.sbgapps.scoreit.data.interactor.GameManager;
import com.sbgapps.scoreit.data.model.Game;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stéphane on 09/07/2014.
 */
public class StorageManager {

    final private Storage mStorage;
    final private GameManager mGameManager;

    public StorageManager(Context context, GameManager gameManager) {
        mStorage = SimpleStorage.getInternalStorage(context);
        mGameManager = gameManager;
    }

    public File getPlayedFile() {
        String fileName = mGameManager.getPreferences().getString(getKey(), "default");
        return mStorage.getFile(getDirectory(), fileName);
    }

    public boolean isDefaultFile() {
        return mGameManager.getPreferences().getString(getKey(), "default").equals("default");
    }

    public void setPlayedFile(String fileName) {
        SharedPreferences.Editor editor = mGameManager.getPreferences().edit();
        editor.putString(getKey(), fileName);
        editor.apply();
    }

    public File createFile(String fileName) {
        String dir = getDirectory();
        mStorage.createFile(dir, fileName, "");
        return mStorage.getFile(dir, fileName);
    }

    public List<String> getSavedFiles() {
        List<String> names = new ArrayList<>();
        for (File f : mStorage.getFiles(getDirectory(), OrderType.DATE)) {
            String name = f.getName();
            if (!name.equals("default")) {
                names.add(name);
            }
        }
        return names;
    }

    private String getDirectory() {
        String dir;
        switch (mGameManager.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                dir = "universal_" + mGameManager.getPlayerCount();
                break;
            case Game.BELOTE:
                dir = "belote";
                break;
            case Game.COINCHE:
                dir = "coinche_2";
                break;
            case Game.TAROT:
                dir = "tarot_" + mGameManager.getPlayerCount();
                break;
        }
        if (!mStorage.isDirectoryExists(dir)) {
            mStorage.createDirectory(dir);
        }
        return dir;
    }

    private String getKey() {
        String key;
        switch (mGameManager.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                key = "universal_" + mGameManager.getPlayerCount() + "_file";
                break;
            case Game.BELOTE:
                key = "belote_file";
                break;
            case Game.COINCHE:
                key = "coinche_file";
                break;
            case Game.TAROT:
                key = "tarot_" + mGameManager.getPlayerCount() + "_file";
                break;
        }
        return key;
    }
}
