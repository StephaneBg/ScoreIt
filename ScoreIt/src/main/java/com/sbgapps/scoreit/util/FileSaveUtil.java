/*
 * Copyright (c) 2014 SBG Apps
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

package com.sbgapps.scoreit.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stéphane on 09/07/2014.
 */
public class FileSaveUtil {

    static public void setLastSavedFile(String file, GameHelper game) {
        String key;
        SharedPreferences.Editor editor = game.getPreferences().edit();
        switch (game.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                key = "last_universal_" + game.getPlayerCount();
                editor.putString(key, file);
                break;
            case Game.BELOTE:
                editor.putString("last_belote", file);
                break;
            case Game.COINCHE:
                editor.putString("last_coinche", file);
                break;
            case Game.TAROT:
                key = "last_tarot_" + game.getPlayerCount();
                editor.putString(key, file);
                break;
        }
        editor.apply();
    }

    static public String getLastSavedFile(GameHelper game) {
        String fileName, key, def;
        SharedPreferences preferences = game.getPreferences();
        switch (game.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                key = "last_universal_" + game.getPlayerCount();
                def = key + "_default";
                fileName = preferences.getString(key, def);
                break;
            case Game.BELOTE:
                fileName = preferences.getString("last_belote", "belote_default");
                break;
            case Game.COINCHE:
                fileName = preferences.getString("last_coinche", "coinche_default");
                break;
            case Game.TAROT:
                key = "last_tarot_" + game.getPlayerCount();
                def = key + "_default";
                fileName = preferences.getString(key, def);
                break;
        }
        return fileName;
    }

    static public List<String> getSavedFiles(Context context, String prefix) {
        List<String> list = new ArrayList<>();
        if (null == prefix) {
            return list;
        }

        for (String s : context.fileList()) {
            if (s.startsWith(prefix) && !s.contains("default")) {
                list.add(s.replace(prefix, ""));
            }
        }
        return list;
    }
}
