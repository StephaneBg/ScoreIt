/*
 * Copyright (c) 2015 SBG Apps
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

package com.sbgapps.scoreit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.BeloteBonus;
import com.sbgapps.scoreit.games.belote.BeloteLap;
import com.sbgapps.scoreit.widget.SeekPoints;
import com.sbgapps.scoreit.widget.ToggleGroup;

import java.util.List;

/**
 * Created by sbaiget on 01/11/13.
 */
public class BeloteLapFragment extends GenericBeloteLapFragment
        implements SeekPoints.OnProgressChangedListener {

    LinearLayout mContainer;
    TextView mPlayer1Name;
    TextView mPlayer2Name;
    TextView mPlayer1Points;
    TextView mPlayer2Points;
    ImageButton mSwitchBtn;
    ToggleGroup mScoreGroup;
    SeekPoints mSeekPoints;
    Button mButtonBonus;

    @Override
    public BeloteLap getLap() {
        return (BeloteLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_belote, null);

        if (null == getLap()) return view;

        mContainer = (LinearLayout) view.findViewById(R.id.ll_container);
        mPlayer1Name = (TextView) view.findViewById(R.id.player1_name);
        mPlayer2Name = (TextView) view.findViewById(R.id.player2_name);
        mPlayer1Points = (TextView) view.findViewById(R.id.player1_points);
        mPlayer2Points = (TextView) view.findViewById(R.id.player2_points);
        mSwitchBtn = (ImageButton) view.findViewById(R.id.btn_switch);
        mScoreGroup = (ToggleGroup) view.findViewById(R.id.group_score);
        mSeekPoints = (SeekPoints) view.findViewById(R.id.seekbar_points);
        mButtonBonus = (Button) view.findViewById(R.id.btn_add_bonus);

        mPlayer1Name.setText(getGameHelper().getPlayer(Player.PLAYER_1).getName());
        mPlayer2Name.setText(getGameHelper().getPlayer(Player.PLAYER_2).getName());

        mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Player.PLAYER_1 == getLap().getScorer()) {
                    getLap().setScorer(Player.PLAYER_2);
                } else {
                    getLap().setScorer(Player.PLAYER_1);
                }
                displayScores();
            }
        });

        int p = getLap().getPoints();

        mScoreGroup.setOnCheckedChangeListener(new ToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_score:
                        mSeekPoints.setVisibility(View.VISIBLE);
                        getLap().setPoints(110);
                        mSeekPoints.setProgress(110, true);
                        mSeekPoints.setPoints("110");
                        break;
                    case R.id.btn_inside:
                        mSeekPoints.setVisibility(View.GONE);
                        getLap().setPoints(160);
                        break;
                    case R.id.btn_capot:
                        mSeekPoints.setVisibility(View.GONE);
                        getLap().setPoints(250);
                        break;
                }
                displayScores();
            }
        });

        mSeekPoints.setMax(pointsToProgress(157))
                .setProgress(pointsToProgress(p))
                .setPoints(Integer.toString(p))
                .setOnProgressChangedListener(this);
        displayScores();

        mButtonBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBonus(null);
            }
        });
        for (BeloteBonus bonus : getLap().getBonuses()) {
            addBonus(bonus);
        }

        return view;
    }

    private int progressToPoints(int progress) {
        return progress + 5;
    }

    private int pointsToProgress(int points) {
        return points - 5;
    }

    private void addBonus(BeloteBonus beloteBonus) {
        List<BeloteBonus> bonuses = getLap().getBonuses();
        if (null == beloteBonus) {
            beloteBonus = new BeloteBonus();
            bonuses.add(beloteBonus);
        }
        final BeloteBonus bonus = beloteBonus;

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.list_item_bonus, mContainer, false);
        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_remove_announce);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().getBonuses().remove(bonus);
                mButtonBonus.setEnabled(getLap().getBonuses().size() < 3);
                mContainer.removeView(view);
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_announce);
        spinner.setAdapter(getBonusArrayAdapter());
        spinner.setSelection(bonus.get());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bonus.set(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner = (Spinner) view.findViewById(R.id.spinner_player);
        ArrayAdapter<PlayerItem> aa = getPlayerArrayAdapter();
        spinner.setAdapter(aa);
        spinner.setSelection(bonus.getPlayer());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bonus.setPlayer(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int pos = mContainer.getChildCount() - 1;
        mContainer.addView(view, pos);
        mButtonBonus.setEnabled(bonuses.size() < 3);
    }

    @Override
    public String onProgressChanged(SeekPoints seekPoints, int progress) {
        int points = progressToPoints(progress);
        getLap().setPoints(points);
        displayScores();
        return Integer.toString(points);
    }

    private ArrayAdapter<BeloteBonusItem> getBonusArrayAdapter() {
        final ArrayAdapter<BeloteBonusItem> announceItemArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        announceItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        announceItemArrayAdapter.add(new BeloteBonusItem(BeloteBonus.BONUS_BELOTE));
        announceItemArrayAdapter.add(new BeloteBonusItem(BeloteBonus.BONUS_RUN_3));
        announceItemArrayAdapter.add(new BeloteBonusItem(BeloteBonus.BONUS_RUN_4));
        announceItemArrayAdapter.add(new BeloteBonusItem(BeloteBonus.BONUS_RUN_5));
        announceItemArrayAdapter.add(new BeloteBonusItem(BeloteBonus.BONUS_FOUR_NORMAL));
        announceItemArrayAdapter.add(new BeloteBonusItem(BeloteBonus.BONUS_FOUR_NINE));
        announceItemArrayAdapter.add(new BeloteBonusItem(BeloteBonus.BONUS_FOUR_JACK));
        return announceItemArrayAdapter;
    }

    private void displayScores() {
        getLap().computePoints();
        mPlayer1Points.setText(Integer.toString(getLap().getScore(Player.PLAYER_1)));
        mPlayer2Points.setText(Integer.toString(getLap().getScore(Player.PLAYER_2)));
    }

    class BeloteBonusItem {

        final int mBonus;

        BeloteBonusItem(int bonus) {
            mBonus = bonus;
        }

        @Override
        public String toString() {
            return BeloteBonus.getLitteralBonus(getActivity(), mBonus);
        }
    }
}
