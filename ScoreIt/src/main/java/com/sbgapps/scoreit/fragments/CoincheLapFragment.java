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
import android.widget.ToggleButton;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.coinche.CoincheBonus;
import com.sbgapps.scoreit.games.coinche.CoincheLap;
import com.sbgapps.scoreit.views.SeekPoints;
import com.sbgapps.scoreit.views.ToggleGroup;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 29/01/14.
 */
public class CoincheLapFragment extends GenericBeloteLapFragment
        implements SeekPoints.OnProgressChangedListener {

    @InjectView(R.id.bidder_group)
    ToggleGroup mBidderGroup;
    @InjectView(R.id.tv_bid)
    TextView mBid;
    @InjectView(R.id.btn_bid_minus_10)
    Button mButtonBidMinusTen;
    @InjectView(R.id.btn_bid_minus_100)
    Button mButtonBidMinusHundred;
    @InjectView(R.id.btn_bid_plus_10)
    Button mButtonBidPlusTen;
    @InjectView(R.id.btn_bid_plus_100)
    Button mButtonBidPlusHundred;

    @InjectView(R.id.spinner_coinche)
    Spinner mCoincheSpinner;

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
    @InjectView(R.id.seekbar_points)
    SeekPoints mSeekPoints;

    @InjectView(R.id.ll_bonuses)
    LinearLayout mBonuses;
    @InjectView(R.id.btn_add_bonus)
    Button mButtonBonus;

    @Override
    public CoincheLap getLap() {
        return (CoincheLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_coinche, null);
        ButterKnife.inject(this, view);

        if (null == getLap()) return view;

        switch (getLap().getBidder()) {
            case Player.PLAYER_1:
                mBidderGroup.check(R.id.btn_player_1);
                break;
            case Player.PLAYER_2:
                mBidderGroup.check(R.id.btn_player_2);
                break;
        }

        mBidderGroup.setOnCheckedChangeListener(new ToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_player_1:
                        getLap().setBidder(Player.PLAYER_1);
                        break;
                    case R.id.btn_player_2:
                        getLap().setBidder(Player.PLAYER_2);
                        break;
                }
            }
        });

        ToggleButton btn = (ToggleButton) view.findViewById(R.id.btn_player_1);
        String name = getGameHelper().getPlayer(Player.PLAYER_1).getName();
        btn.setText(name);
        btn.setTextOn(name);
        btn.setTextOff(name);
        btn = (ToggleButton) view.findViewById(R.id.btn_player_2);
        name = getGameHelper().getPlayer(Player.PLAYER_2).getName();
        btn.setText(name);
        btn.setTextOn(name);
        btn.setTextOff(name);

        int bid = getLap().getBid();
        mBid.setText(Integer.toString(bid));

        stepBid(0);
        mButtonBidMinusTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepBid(-10);
            }
        });
        mButtonBidMinusHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepBid(-100);
            }
        });
        mButtonBidPlusTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepBid(10);
            }
        });
        mButtonBidPlusHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepBid(100);
            }
        });

        ArrayAdapter<CoincheItem> coincheAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        coincheAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coincheAdapter.add(new CoincheItem(CoincheLap.COINCHE_NONE));
        coincheAdapter.add(new CoincheItem(CoincheLap.COINCHE_COINCHE));
        coincheAdapter.add(new CoincheItem(CoincheLap.COINCHE_SURCOINCHE));
        mCoincheSpinner.setAdapter(coincheAdapter);
        mCoincheSpinner.setSelection(getLap().getCoinche());
        mCoincheSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLap().setCoinche(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        p = pointsToProgress(p);
        mSeekPoints.init(
                p,
                160,
                Integer.toString(p));
        mSeekPoints.setOnProgressChangedListener(this);
        displayScores();

        mButtonBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBonus(null);
            }
        });
        for (CoincheBonus bonus : getLap().getBonuses()) {
            addBonus(bonus);
        }

        return view;
    }

    private void stepBid(int increment) {
        int bid = getLap().stepBid(increment);
        mBid.setText(Integer.toString(bid));

        mButtonBidMinusTen.setEnabled(bid >= 90);
        mButtonBidMinusHundred.setEnabled(bid >= 180);
        mButtonBidPlusTen.setEnabled(bid <= 990);
        mButtonBidPlusHundred.setEnabled(bid <= 900);
    }

    @Override
    public String onProgressChanged(SeekPoints seekPoints, int progress) {
        int points = progressToPoints(progress);
        getLap().setPoints(points);
        displayScores();
        return Integer.toString(points);
    }

    private int progressToPoints(int progress) {
        switch (progress) {
            default:
                return progress;
            case 158:
                return 160;
            case 159:
                return 250;
            case 160:
                return 500;
        }
    }

    private int pointsToProgress(int points) {
        switch (points) {
            default:
                return points;
            case 160:
                return 158;
            case 250:
                return 159;
            case 500:
                return 160;
        }
    }

    private void displayScores() {
        getLap().computePoints();
        mPlayer1Points.setText(Integer.toString(getLap().getScore(Player.PLAYER_1)));
        mPlayer2Points.setText(Integer.toString(getLap().getScore(Player.PLAYER_2)));
    }

    private void addBonus(CoincheBonus coincheBonus) {
        List<CoincheBonus> bonuses = getLap().getBonuses();
        if (null == coincheBonus) {
            coincheBonus = new CoincheBonus();
            bonuses.add(coincheBonus);
        }
        final CoincheBonus bonus = coincheBonus;

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
        mButtonBonus.setEnabled(bonuses.size() < 3);
    }

    private ArrayAdapter<CoincheBonusItem> getBonusArrayAdapter() {
        ArrayAdapter<CoincheBonusItem> bonusArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        bonusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bonusArrayAdapter.add(new CoincheBonusItem(CoincheBonus.BONUS_BELOTE));
        bonusArrayAdapter.add(new CoincheBonusItem(CoincheBonus.BONUS_RUN_3));
        bonusArrayAdapter.add(new CoincheBonusItem(CoincheBonus.BONUS_RUN_4));
        bonusArrayAdapter.add(new CoincheBonusItem(CoincheBonus.BONUS_RUN_5));
        bonusArrayAdapter.add(new CoincheBonusItem(CoincheBonus.BONUS_FOUR_NORMAL));
        bonusArrayAdapter.add(new CoincheBonusItem(CoincheBonus.BONUS_FOUR_NINE));
        bonusArrayAdapter.add(new CoincheBonusItem(CoincheBonus.BONUS_FOUR_JACK));
        return bonusArrayAdapter;
    }

    class CoincheBonusItem {

        final int mBonus;

        CoincheBonusItem(int bonus) {
            mBonus = bonus;
        }

        @Override
        public String toString() {
            return CoincheBonus.getLitteralBonus(getActivity(), mBonus);
        }
    }

    class CoincheItem {

        final int mCoinche;

        CoincheItem(int coinche) {
            mCoinche = coinche;
        }

        @Override
        public String toString() {
            switch (mCoinche) {
                default:
                case CoincheLap.COINCHE_NONE:
                    return getActivity().getString(R.string.none);
                case CoincheLap.COINCHE_COINCHE:
                    return getActivity().getString(R.string.coinche);
                case CoincheLap.COINCHE_SURCOINCHE:
                    return getActivity().getString(R.string.surcoinche);
            }
        }
    }
}
