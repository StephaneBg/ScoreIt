package com.sbgapps.scoreit.core.model.utils;

import com.sbgapps.scoreit.core.model.Lap;
import com.sbgapps.scoreit.core.model.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 21/02/2017.
 */

public class GameHelper {

    public static int getScore(ArrayList<? extends Lap> laps, @Player.Players int player, boolean rounded) {
        int score = 0;
        for (Lap lap : laps) {
            score += lap.getScore(player, rounded);
        }
        return score;
    }
}
