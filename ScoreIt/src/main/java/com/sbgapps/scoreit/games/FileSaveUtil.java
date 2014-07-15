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

package com.sbgapps.scoreit.games;

import android.content.SharedPreferences;

/**
 * Created by Stéphane on 09/07/2014.
 */
public class FileSaveUtil {

    public static final String KEY_LAST_UNIVERSAL_2 = "last_universal_2";
    public static final String KEY_LAST_UNIVERSAL_3 = "last_universal_3";
    public static final String KEY_LAST_UNIVERSAL_4 = "last_universal_4";
    public static final String KEY_LAST_UNIVERSAL_5 = "last_universal_5";
    public static final String KEY_LAST_BELOTE = "last_belote";
    public static final String KEY_LAST_COINCHE = "last_coinche";
    public static final String KEY_LAST_TAROT_3 = "last_tarot_3";
    public static final String KEY_LAST_TAROT_4 = "last_tarot_4";
    public static final String KEY_LAST_TAROT_5 = "last_tarot_5";

    static public String getLastSavedFile(GameHelper game) {
        String fileName;
        SharedPreferences preferences = game.getPreferences();
        switch (game.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                switch (game.getPlayerCount()) {
                    case 2:
                        fileName = preferences.getString(KEY_LAST_UNIVERSAL_2, "universal_2_default");
                        break;
                    case 3:
                        fileName = preferences.getString(KEY_LAST_UNIVERSAL_3, "universal_3_default");
                        break;
                    case 4:
                        fileName = preferences.getString(KEY_LAST_UNIVERSAL_4, "universal_4_default");
                        break;
                    default:
                    case 5:
                        fileName = preferences.getString(KEY_LAST_UNIVERSAL_5, "universal_5_default");
                        break;
                }
                break;
            case Game.BELOTE:
                fileName = preferences.getString(KEY_LAST_BELOTE, "belote_default");
                break;
            case Game.COINCHE:
                fileName = preferences.getString(KEY_LAST_COINCHE, "coinche_default");
                break;
            case Game.TAROT:
                switch (game.getPlayerCount()) {
                    case 3:
                        fileName = preferences.getString(KEY_LAST_TAROT_3, "tarot_3_default");
                        break;
                    case 4:
                        fileName = preferences.getString(KEY_LAST_TAROT_4, "tarot_4_default");
                        break;
                    default:
                    case 5:
                        fileName = preferences.getString(KEY_LAST_TAROT_5, "tarot_5_default");
                        break;
                }
                break;
        }
        return fileName;
    }
}
