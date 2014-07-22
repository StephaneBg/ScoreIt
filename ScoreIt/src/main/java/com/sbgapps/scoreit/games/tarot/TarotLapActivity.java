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

package com.sbgapps.scoreit.games.tarot;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.LapActivity;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.widget.SeekbarPoints;

/**
 * Created by sbaiget on 07/12/13.
 */
public class TarotLapActivity extends LapActivity
        implements SeekbarPoints.OnPointsChangedListener {

    private Spinner mTaker;
    private Spinner mBid;
    private Spinner mPartner;
    private CheckBox mPetit;
    private CheckBox mTwentyOne;
    private CheckBox mExcuse;
    private SeekbarPoints mPoints;
    private LinearLayout mBonuses;
    private Button mButtonBonus;

    @Override
    public TarotLap getLap() {
        return (TarotLap) super.getLap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == savedInstanceState) {
            if (-1 != mPosition) {
                mLap = getGameHelper().getLaps().get(mPosition);
            } else {
                switch (getGameHelper().getPlayerCount()) {
                    case 3:
                        mLap = new TarotThreeLap();
                        break;
                    case 4:
                        mLap = new TarotFourLap();
                        break;
                    case 5:
                        mLap = new TarotFiveLap();
                        break;
                }
            }
        }

        setContentView(R.layout.activity_lap_tarot);

        mTaker = (Spinner) findViewById(R.id.spinner_taker);
        mBid = (Spinner) findViewById(R.id.spinner_deal);
        mPetit = (CheckBox) findViewById(R.id.checkbox_petit);
        mTwentyOne = (CheckBox) findViewById(R.id.checkbox_twenty_one);
        mExcuse = (CheckBox) findViewById(R.id.checkbox_fool);
        mPoints = (SeekbarPoints) findViewById(R.id.sb_points);
        mBonuses = (LinearLayout) findViewById(R.id.ll_bonuses);

        mTaker.setAdapter(getPlayerArrayAdapter());
        mTaker.setSelection(getLap().getTaker());
        mTaker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLap().setTaker(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getGameHelper().getPlayerCount() == 5) {
            ViewStub stub = (ViewStub) findViewById(R.id.viewstub_partner);
            View v = stub.inflate();
            final ArrayAdapter<PlayerItem> partnerItemArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item);
            partnerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_1));
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_2));
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_3));
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_4));
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_5));
            mPartner = (Spinner) v.findViewById(R.id.spinner_partner);
            mPartner.setAdapter(partnerItemArrayAdapter);
            mPartner.setSelection(((TarotFiveLap) getLap()).getPartner());
            mPartner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TarotFiveLap) getLap()).setPartner(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        final ArrayAdapter<BidItem> dealItemArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        dealItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dealItemArrayAdapter.add(new BidItem(TarotLap.BID_PRISE));
        dealItemArrayAdapter.add(new BidItem(TarotLap.BID_GARDE));
        dealItemArrayAdapter.add(new BidItem(TarotLap.BID_GARDE_SANS));
        dealItemArrayAdapter.add(new BidItem(TarotLap.BID_GARDE_CONTRE));
        mBid.setAdapter(dealItemArrayAdapter);
        mBid.setSelection(getLap().getBid());
        mBid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLap().setBid(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPoints.init(getLap().getPoints(), 91, getLap().getPoints());
        mPoints.setOnPointsChangedListener(this, "points");

        mPetit.setChecked((getLap().getOudlers() & TarotLap.OUDLER_PETIT_MSK)
                == TarotLap.OUDLER_PETIT_MSK);
        mPetit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int oudlers = getLap().getOudlers();
                if (isChecked) {
                    getLap().setOudlers(oudlers | TarotLap.OUDLER_PETIT_MSK);
                } else {
                    getLap().setOudlers(oudlers & ~TarotLap.OUDLER_PETIT_MSK);
                }
            }
        });
        mExcuse.setChecked((getLap().getOudlers() & TarotLap.OUDLER_EXCUSE_MSK)
                == TarotLap.OUDLER_EXCUSE_MSK);
        mExcuse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int oudlers = getLap().getOudlers();
                if (isChecked) {
                    getLap().setOudlers(oudlers | TarotLap.OUDLER_EXCUSE_MSK);
                } else {
                    getLap().setOudlers(oudlers & ~TarotLap.OUDLER_EXCUSE_MSK);
                }
            }
        });
        mTwentyOne.setChecked((getLap().getOudlers() & TarotLap.OUDLER_21_MSK)
                == TarotLap.OUDLER_21_MSK);
        mTwentyOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int oudlers = getLap().getOudlers();
                if (isChecked) {
                    getLap().setOudlers(oudlers | TarotLap.OUDLER_21_MSK);
                } else {
                    getLap().setOudlers(oudlers & ~TarotLap.OUDLER_21_MSK);
                }
            }
        });

        mButtonBonus = (Button) findViewById(R.id.btn_add_bonus);
        mButtonBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBonus(null);
            }
        });
        for (TarotBonus bonus : getLap().getBonuses()) {
            addBonus(bonus);
        }
    }

    private void addBonus(TarotBonus tarotBonus) {
        ArrayAdapter<BonusItem> adapter = getBonusArrayAdapter();

        if (null == tarotBonus) {
            tarotBonus = new TarotBonus();
            getLap().getBonuses().add(tarotBonus);
            mButtonBonus.setEnabled(getLap().getBonuses().size() < 3);
        }
        final TarotBonus bonus = tarotBonus;

        final View view = getLayoutInflater()
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
        spinner.setAdapter(adapter);
        spinner.setSelection(bonus.getBonus());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bonus.setBonus(position);
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
    }

    private ArrayAdapter<PlayerItem> getPlayerArrayAdapter() {
        ArrayAdapter<PlayerItem> playerItemArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        playerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_1));
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_2));
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_3));
        switch (getGameHelper().getPlayerCount()) {
            case 4:
                playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_4));
                break;
            case 5:
                playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_4));
                playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_5));
                break;
        }
        return playerItemArrayAdapter;
    }

    private ArrayAdapter<BonusItem> getBonusArrayAdapter() {
        final ArrayAdapter<BonusItem> announceItemArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        announceItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (!isBonusAlreadySet(TarotBonus.BONUS_PETIT_AU_BOUT)) {
            announceItemArrayAdapter.add(new BonusItem(TarotBonus.BONUS_PETIT_AU_BOUT));
        }

        if (!isBonusAlreadySet(TarotBonus.BONUS_POIGNEE_SIMPLE) &&
                !isBonusAlreadySet(TarotBonus.BONUS_POIGNEE_DOUBLE) &&
                !isBonusAlreadySet(TarotBonus.BONUS_POIGNEE_TRIPLE)) {
            announceItemArrayAdapter.add(new BonusItem(TarotBonus.BONUS_POIGNEE_SIMPLE));
            announceItemArrayAdapter.add(new BonusItem(TarotBonus.BONUS_POIGNEE_DOUBLE));
            announceItemArrayAdapter.add(new BonusItem(TarotBonus.BONUS_POIGNEE_TRIPLE));
        }

        if (!isBonusAlreadySet(TarotBonus.BONUS_CHELEM_NON_ANNONCE) &&
                !isBonusAlreadySet(TarotBonus.BONUS_CHELEM_ANNONCE_REALISE) &&
                !isBonusAlreadySet(TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE)) {
            announceItemArrayAdapter.add(new BonusItem(TarotBonus.BONUS_CHELEM_NON_ANNONCE));
            announceItemArrayAdapter.add(new BonusItem(TarotBonus.BONUS_CHELEM_ANNONCE_REALISE));
            announceItemArrayAdapter.add(new BonusItem(TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE));
        }
        return announceItemArrayAdapter;
    }

    private boolean isBonusAlreadySet(int bonus) {
        for (TarotBonus b : getLap().getBonuses()) {
            if (b.getBonus() == bonus) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int onPointsChanged(int progress, String tag) {
        getLap().setPoints(progress);
        return progress;
    }

    class BidItem {

        final int mBid;

        BidItem(int bid) {
            mBid = bid;
        }

        @Override
        public String toString() {
            Resources r = getResources();
            switch (mBid) {
                case TarotLap.BID_PRISE:
                    return r.getString(R.string.take);
                case TarotLap.BID_GARDE:
                    return r.getString(R.string.guard);
                case TarotLap.BID_GARDE_CONTRE:
                    return r.getString(R.string.guard_against);
                case TarotLap.BID_GARDE_SANS:
                    return r.getString(R.string.guard_without);
            }
            return null;
        }
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
            Resources r = getResources();
            switch (mBonus) {
                case TarotBonus.BONUS_PETIT_AU_BOUT:
                    return r.getString(R.string.petit_au_bout);
                case TarotBonus.BONUS_POIGNEE_SIMPLE:
                    return r.getString(R.string.poignee_simple);
                case TarotBonus.BONUS_POIGNEE_DOUBLE:
                    return r.getString(R.string.poignee_double);
                case TarotBonus.BONUS_POIGNEE_TRIPLE:
                    return r.getString(R.string.poignee_double);
                case TarotBonus.BONUS_CHELEM_NON_ANNONCE:
                    return r.getString(R.string.chelem_non_annonce);
                case TarotBonus.BONUS_CHELEM_ANNONCE_REALISE:
                    return r.getString(R.string.chelem_annonce_realise);
                case TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE:
                    return r.getString(R.string.chelem_annonce_non_realise);
            }
            return null;
        }
    }
}
