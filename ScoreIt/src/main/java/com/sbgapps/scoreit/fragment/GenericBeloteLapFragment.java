package com.sbgapps.scoreit.fragment;

import android.widget.ArrayAdapter;

import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.GenericBeloteLap;

/**
 * Created by sbaiget on 29/10/2014.
 */
public class GenericBeloteLapFragment extends LapFragment {

    public GenericBeloteLap getLap() {
        return (GenericBeloteLap) super.getLap();
    }

    protected ArrayAdapter<PlayerItem> getPlayerArrayAdapter() {
        ArrayAdapter<PlayerItem> playerItemArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        playerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_1));
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_2));
        return playerItemArrayAdapter;
    }
}
