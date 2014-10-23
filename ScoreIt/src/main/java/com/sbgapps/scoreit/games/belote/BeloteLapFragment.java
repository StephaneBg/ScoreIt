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

package com.sbgapps.scoreit.games.belote;

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
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.LapFragment;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.widget.SeekPoints;
import com.sbgapps.scoreit.widget.ToggleGroup;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 01/11/13.
 */
public class BeloteLapFragment extends LapFragment
        implements SeekPoints.OnPointsChangedListener {

    @InjectView(R.id.player1_name)
    TextView mPlayer1Name;
    @InjectView(R.id.player2_name)
    TextView mPlayer2Name;
    @InjectView(R.id.player1_points)
    TextView mPlayer1Points;
    @InjectView(R.id.player2_points)
    TextView mPlayer2Points;
    @InjectView(R.id.btn_switch)
    ImageButton mSwitchBtn;
    @InjectView(R.id.group_score)
    ToggleGroup mScoreGroup;
    @InjectView(R.id.input_points)
    SeekPoints mSeekPoints;
    @InjectView(R.id.ll_bonuses)
    LinearLayout mBonuses;
    @InjectView(R.id.btn_add_bonus)
    Button mButtonBonus;

    @Override
    public BeloteLap getLap() {
        return (BeloteLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_belote, null);
        ButterKnife.inject(this, view);

        final GameHelper gameHelper = getGameHelper();
        mPlayer1Name.setText(gameHelper.getPlayer(Player.PLAYER_1).getName());
        mPlayer2Name.setText(gameHelper.getPlayer(Player.PLAYER_2).getName());

        setScores();

        mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Player.PLAYER_1 == getLap().getScorer()) {
                    getLap().setScorer(Player.PLAYER_2);
                } else {
                    getLap().setScorer(Player.PLAYER_1);
                }
                setScores();
            }
        });

        mScoreGroup.setOnCheckedChangeListener(new ToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_score:
                        mSeekPoints.setVisibility(View.VISIBLE);
                        getLap().setPoints(110);
                        mSeekPoints.setPoints(110);
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
                setScores();
            }
        });

        int points = getLap().getPoints();
        if (160 == points) {
            mScoreGroup.check(R.id.btn_inside);
            mSeekPoints.setVisibility(View.GONE);
        } else if (250 == points) {
            mScoreGroup.check(R.id.btn_capot);
            mSeekPoints.setVisibility(View.GONE);
        } else {
        }
        mSeekPoints.init(
                points,
                162,
                getLap().getPoints());
        mSeekPoints.setOnPointsChangedListener(this, "points");

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

    private void setScores() {
        int scores[] = getLap().getScores();
        if (Player.PLAYER_1 == getLap().getScorer()) {
            mPlayer1Points.setText(Integer.toString(scores[0]));
            mPlayer2Points.setText(Integer.toString(scores[1]));
        } else {
            mPlayer1Points.setText(Integer.toString(scores[1]));
            mPlayer2Points.setText(Integer.toString(scores[0]));
        }
    }

    @Override
    public void onPointsChanged(int progress, String tag) {
        getLap().setPoints(progress);
        setScores();
    }

    private void addBonus(BeloteBonus beloteBonus) {
        List<BeloteBonus> bonuses = getLap().getBonuses();
        if (null == beloteBonus) {
            beloteBonus = new BeloteBonus();
            bonuses.add(beloteBonus);
        }
        final BeloteBonus bonus = beloteBonus;

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.list_item_bonus, mBonuses, false);
        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_remove_announce);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().getBonuses().remove(bonus);
                mButtonBonus.setEnabled(getLap().getBonuses().size() < 3);
                mBonuses.removeView(view);
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

        int pos = mBonuses.getChildCount() - 1;
        mBonuses.addView(view, pos);
        mButtonBonus.setEnabled(bonuses.size() < 4);
    }

    private ArrayAdapter<PlayerItem> getPlayerArrayAdapter() {
        ArrayAdapter<PlayerItem> playerItemArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        playerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_1));
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_2));
        return playerItemArrayAdapter;
    }

    private ArrayAdapter<BonusItem> getBonusArrayAdapter() {
        final ArrayAdapter<BonusItem> announceItemArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        announceItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        announceItemArrayAdapter.add(new BonusItem(BeloteBonus.BONUS_BELOTE));
        announceItemArrayAdapter.add(new BonusItem(BeloteBonus.BONUS_RUN_3));
        announceItemArrayAdapter.add(new BonusItem(BeloteBonus.BONUS_RUN_4));
        announceItemArrayAdapter.add(new BonusItem(BeloteBonus.BONUS_RUN_5));
        announceItemArrayAdapter.add(new BonusItem(BeloteBonus.BONUS_FOUR_NORMAL));
        announceItemArrayAdapter.add(new BonusItem(BeloteBonus.BONUS_FOUR_NINE));
        announceItemArrayAdapter.add(new BonusItem(BeloteBonus.BONUS_FOUR_JACK));
        return announceItemArrayAdapter;
    }

    class PlayerItem {

        final int mPlayer;

        public PlayerItem(int player) {
            mPlayer = player;
        }

        @Override
        public String toString() {
            return getGameHelper().getPlayer(mPlayer).getName();
        }
    }

    class BonusItem {

        final int mBonus;

        BonusItem(int bonus) {
            mBonus = bonus;
        }

        @Override
        public String toString() {
            return BeloteBonus.getLitteralBonus(getActivity(), mBonus);
        }
    }
}
