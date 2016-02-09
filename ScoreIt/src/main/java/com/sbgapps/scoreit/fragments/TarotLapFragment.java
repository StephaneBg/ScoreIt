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
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ToggleButton;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.models.Player;
import com.sbgapps.scoreit.models.tarot.TarotBid;
import com.sbgapps.scoreit.models.tarot.TarotBonus;
import com.sbgapps.scoreit.models.tarot.TarotFiveLap;
import com.sbgapps.scoreit.models.tarot.TarotLap;
import com.sbgapps.scoreit.views.widgets.SeekPoints;

/**
 * Created by sbaiget on 07/12/13.
 */
public class TarotLapFragment extends LapFragment
        implements SeekPoints.OnProgressChangedListener {

    LinearLayout mContainer;
    Spinner mTaker;
    Spinner mBid;
    ToggleButton mPetit;
    ToggleButton mTwentyOne;
    ToggleButton mExcuse;
    SeekPoints mSeekPoints;
    Button mButtonBonus;
    Spinner mPartner;

    @Override
    public TarotLap getLap() {
        return (TarotLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_tarot, null);

        mContainer = (LinearLayout) view.findViewById(R.id.ll_container);
        mTaker = (Spinner) view.findViewById(R.id.spinner_taker);
        mBid = (Spinner) view.findViewById(R.id.spinner_bid);
        mPetit = (ToggleButton) view.findViewById(R.id.checkbox_petit);
        mTwentyOne = (ToggleButton) view.findViewById(R.id.checkbox_twenty_one);
        mExcuse = (ToggleButton) view.findViewById(R.id.checkbox_excuse);
        mSeekPoints = (SeekPoints) view.findViewById(R.id.seekbar_points);
        mButtonBonus = (Button) view.findViewById(R.id.btn_add_bonus);

        if (null == getLap()) return view;

        mTaker.setAdapter(getPlayerAdapter());
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

        if (5 == getGameHelper().getPlayerCount()) {
            ViewStub stub = (ViewStub) view.findViewById(R.id.viewstub_partner);
            View v = stub.inflate();
            mPartner = (Spinner) v.findViewById(R.id.spinner_partner);
            mPartner.setAdapter(getPartnerAdapter());
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

        mBid.setAdapter(getBidAdapter());
        mBid.setSelection(getLap().getBid().get());
        mBid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLap().setBid(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int points = getLap().getPoints();
        mSeekPoints.setMax(91)
                .setProgress(points)
                .setPoints(Integer.toString(points))
                .setOnProgressChangedListener(this);

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

        mButtonBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBonus(null);
            }
        });
        for (TarotBonus bonus : getLap().getBonuses()) {
            addBonus(bonus);
        }
        return view;
    }

    private void addBonus(TarotBonus tarotBonus) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.list_item_bonus, mContainer, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_bonus);
        SpinnerAdapter sa = getBonusAdapter();
        if (null == tarotBonus) {
            tarotBonus = new TarotBonus(((BonusItem) sa.getItem(0)).mBonus);
            getLap().getBonuses().add(tarotBonus);
        }
        final TarotBonus bonus = tarotBonus;

        spinner.setAdapter(sa);
        for (int i = 0; i < sa.getCount(); i++) {
            if (bonus.get() == ((BonusItem) sa.getItem(i)).mBonus) {
                spinner.setSelection(i);
                break;
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BonusItem item = (BonusItem) parent.getAdapter().getItem(position);
                bonus.set(item.mBonus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner = (Spinner) view.findViewById(R.id.spinner_player);
        spinner.setAdapter(getPlayerAdapter());
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
        mButtonBonus.setEnabled(getLap().getBonuses().size() < 3);

        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_remove_announce);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().getBonuses().remove(bonus);
                mButtonBonus.setEnabled(getLap().getBonuses().size() < 3);
                mContainer.removeView(view);
            }
        });

    }

    private SpinnerAdapter getPlayerAdapter() {
        ArrayAdapter<PlayerItem> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(new PlayerItem(Player.PLAYER_1));
        adapter.add(new PlayerItem(Player.PLAYER_2));
        adapter.add(new PlayerItem(Player.PLAYER_3));
        switch (getGameHelper().getPlayerCount()) {
            case 4:
                adapter.add(new PlayerItem(Player.PLAYER_4));
                break;
            case 5:
                adapter.add(new PlayerItem(Player.PLAYER_4));
                adapter.add(new PlayerItem(Player.PLAYER_5));
                break;
        }
        return adapter;
    }

    private SpinnerAdapter getBidAdapter() {
        ArrayAdapter<BidItem> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(new BidItem(TarotBid.BID_PRISE));
        adapter.add(new BidItem(TarotBid.BID_GARDE));
        adapter.add(new BidItem(TarotBid.BID_GARDE_SANS));
        adapter.add(new BidItem(TarotBid.BID_GARDE_CONTRE));
        return adapter;
    }

    private SpinnerAdapter getPartnerAdapter() {
        ArrayAdapter<PlayerItem> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(new PlayerItem(Player.PLAYER_1));
        adapter.add(new PlayerItem(Player.PLAYER_2));
        adapter.add(new PlayerItem(Player.PLAYER_3));
        adapter.add(new PlayerItem(Player.PLAYER_4));
        adapter.add(new PlayerItem(Player.PLAYER_5));
        return adapter;
    }

    private SpinnerAdapter getBonusAdapter() {
        ArrayAdapter<BonusItem> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (!getLap().hasBonus(TarotBonus.BONUS_PETIT_AU_BOUT))
            adapter.add(new BonusItem(TarotBonus.BONUS_PETIT_AU_BOUT));

        if (!getLap().hasBonus(TarotBonus.BONUS_POIGNEE_DOUBLE)
                && !getLap().hasBonus(TarotBonus.BONUS_POIGNEE_TRIPLE)) {
            adapter.add(new BonusItem(TarotBonus.BONUS_POIGNEE_SIMPLE));
            if (!getLap().hasBonus(TarotBonus.BONUS_POIGNEE_SIMPLE)) {
                adapter.add(new BonusItem(TarotBonus.BONUS_POIGNEE_DOUBLE));
                adapter.add(new BonusItem(TarotBonus.BONUS_POIGNEE_TRIPLE));
            }
        }

        if (!getLap().hasBonus(TarotBonus.BONUS_CHELEM_NON_ANNONCE)
                && !getLap().hasBonus(TarotBonus.BONUS_CHELEM_ANNONCE_REALISE)
                && !getLap().hasBonus(TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE)) {
            adapter.add(new BonusItem(TarotBonus.BONUS_CHELEM_NON_ANNONCE));
            adapter.add(new BonusItem(TarotBonus.BONUS_CHELEM_ANNONCE_REALISE));
            adapter.add(new BonusItem(TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE));
        }
        return adapter;
    }

    @Override
    public String onProgressChanged(SeekPoints seekPoints, int progress) {
        getLap().setPoints(progress);
        return Integer.toString(progress);
    }

    class BidItem {

        @TarotBid.TarotBidValues
        final int mBid;

        BidItem(@TarotBid.TarotBidValues int bid) {
            mBid = bid;
        }

        @Override
        public String toString() {
            return TarotBid.getLiteralBid(getActivity(), mBid);
        }
    }

    class BonusItem {

        @TarotBonus.TarotBonusValues
        final int mBonus;

        BonusItem(@TarotBonus.TarotBonusValues int bonus) {
            mBonus = bonus;
        }

        @Override
        public String toString() {
            return TarotBonus.getLiteralBonus(getActivity(), mBonus);
        }
    }
}
