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
import com.sbgapps.scoreit.models.Player;
import com.sbgapps.scoreit.models.coinche.CoincheBonus;
import com.sbgapps.scoreit.models.coinche.CoincheLap;
import com.sbgapps.scoreit.views.widgets.SeekPoints;

import java.util.List;

/**
 * Created by sbaiget on 29/01/14.
 */
public class CoincheLapFragment extends GenericBeloteLapFragment
        implements SeekPoints.OnProgressChangedListener {

    LinearLayout mContainer;
    Spinner mBidderSpinner;
    SeekPoints mSeekBid;
    Spinner mCoincheSpinner;
    TextView mPlayer1Name;
    TextView mPlayer2Name;
    TextView mPlayer1Points;
    TextView mPlayer2Points;
    ImageButton mSwitchBtn;
    SeekPoints mSeekPoints;
    Button mButtonBonus;

    @Override
    public CoincheLap getLap() {
        return (CoincheLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_coinche, null);

        mContainer = (LinearLayout) view.findViewById(R.id.ll_container);
        mBidderSpinner = (Spinner) view.findViewById(R.id.spinner_bidder);
        mSeekBid = (SeekPoints) view.findViewById(R.id.seekbar_bid);
        mCoincheSpinner = (Spinner) view.findViewById(R.id.spinner_coinche);
        mPlayer1Name = (TextView) view.findViewById(R.id.player1_name);
        mPlayer2Name = (TextView) view.findViewById(R.id.player2_name);
        mPlayer1Points = (TextView) view.findViewById(R.id.player1_points);
        mPlayer2Points = (TextView) view.findViewById(R.id.player2_points);
        mSwitchBtn = (ImageButton) view.findViewById(R.id.btn_switch);
        mSeekPoints = (SeekPoints) view.findViewById(R.id.seekbar_points);
        mButtonBonus = (Button) view.findViewById(R.id.btn_add_bonus);

        if (null == getLap()) return view;

        ArrayAdapter<PlayerItem> bidAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        bidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bidAdapter.add(new PlayerItem(Player.PLAYER_1));
        bidAdapter.add(new PlayerItem(Player.PLAYER_2));
        mBidderSpinner.setAdapter(bidAdapter);
        mBidderSpinner.setSelection(getLap().getBidder());
        mBidderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLap().setBidder(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int b = getLap().getBid();
        mSeekBid.setMax(bidToProgress(1000))
                .setProgress(bidToProgress(b))
                .setPoints(Integer.toString(b))
                .setOnProgressChangedListener(this);

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
        mSeekPoints.setMax(pointsToProgress(500))
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
        for (CoincheBonus bonus : getLap().getBonuses()) {
            addBonus(bonus);
        }

        return view;
    }

    @Override
    public String onProgressChanged(SeekPoints seekPoints, int progress) {
        if (seekPoints.equals(mSeekPoints)) {
            int points = progressToPoints(progress);
            getLap().setPoints(points);
            displayScores();
            return Integer.toString(points);
        } else {
            int bid = progressToBid(progress);
            getLap().setBid(bid);
            return Integer.toString(bid);
        }
    }

    private int progressToPoints(int progress) {
        switch (progress) {
            default:
                return progress + 5;
            case 153:
                return 160;
            case 154:
                return 250;
            case 155:
                return 500;
        }
    }

    private int pointsToProgress(int points) {
        switch (points) {
            default:
                return points - 5;
            case 160:
                return 153;
            case 250:
                return 154;
            case 500:
                return 155;
        }
    }

    private int progressToBid(int progress) {
        return progress * 10 + 80;
    }

    private int bidToProgress(int bid) {
        return (bid - 80) / 10;
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
