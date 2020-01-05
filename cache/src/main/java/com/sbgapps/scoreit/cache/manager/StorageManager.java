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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sbgapps.scoreit.data.repository.PreferencesRepo;
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
    final private SharedPreferences mPreferences;
    final private PreferencesRepo mPrefsRepo;

    public StorageManager(Context context, PreferencesRepo prefsRepo) {
        mStorage = SimpleStorage.getInternalStorage(context);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mPrefsRepo = prefsRepo;
    }

    public File getPlayedFile() {
        String fileName = mPreferences.getString(getKey(), "default");
        return mStorage.getFile(getDirectory(), fileName);
    }

    public boolean isDefaultFile() {
        return mPreferences.getString(getKey(), "default").equals("default");
    }

    public void setPlayedFile(String fileName) {
        SharedPreferences.Editor editor = mPreferences.edit();
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
        switch (mPrefsRepo.getCurrentGame()) {
            default:
            case UNIVERSAL:
                dir = "universal_" + mPrefsRepo.getPlayerCount();
                break;
            case BELOTE:
                dir = "belote_2";
                break;
            case COINCHE:
                dir = "coinche_2";
                break;
            case TAROT:
                dir = "tarot_" + mPrefsRepo.getPlayerCount();
                break;
        }
        if (!mStorage.isDirectoryExists(dir)) {
            mStorage.createDirectory(dir);
        }
        return dir;
    }

    private String getKey() {
        String key;
        switch (mPrefsRepo.getCurrentGame()) {
            default:
            case UNIVERSAL:
                key = "universal_" + mPrefsRepo.getPlayerCount() + "_file";
                break;
            case BELOTE:
                key = "belote_file";
                break;
            case COINCHE:
                key = "coinche_file";
                break;
            case TAROT:
                key = "tarot_" + mPrefsRepo.getPlayerCount() + "_file";
                break;
        }
        return key;
    }
}
