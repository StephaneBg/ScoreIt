package com.sbgapps.scoreit.fragment;

import com.sbgapps.scoreit.games.belote.GenericBeloteLap;
import com.sbgapps.scoreit.widget.SeekPoints;

/**
 * Created by sbaiget on 29/10/2014.
 */
abstract public class GenericBeloteLapFragment extends LapFragment
        implements SeekPoints.OnProgressChangedListener {

    public GenericBeloteLap getLap() {
        return (GenericBeloteLap) super.getLap();
    }
}
