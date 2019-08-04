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

package com.sbgapps.scoreit.legacy.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.data.model.Player;
import com.sbgapps.scoreit.data.model.tarot.TarotBid;
import com.sbgapps.scoreit.data.model.tarot.TarotBonus;
import com.sbgapps.scoreit.data.model.tarot.TarotFiveLap;
import com.sbgapps.scoreit.data.model.tarot.TarotLap;
import com.sbgapps.scoreit.legacy.views.widgets.SeekPoints;

/**
 * Created by sbaiget on 07/12/13.
 */
public class TarotLapFragment extends LapFragment
        implements SeekPoints.OnProgressChangedListener {

    LinearLayout mContainer;
    Spinner mTaker;
    Spinner mBid;
    CheckBox mPetit;
    CheckBox mTwentyOne;
    CheckBox mExcuse;
    SeekPoints mSeekPoints;
    Button mButtonBonus;
    Spinner mPartner;

    @Override
    public TarotLap getLap() {
        return (TarotLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_tarot, container, false);

        mContainer = view.findViewById(R.id.bonus_container);
        mTaker = view.findViewById(R.id.spinner_taker);
        mBid = view.findViewById(R.id.spinner_bid);
        mPetit = view.findViewById(R.id.checkbox_petit);
        mTwentyOne = view.findViewById(R.id.checkbox_twenty_one);
        mExcuse = view.findViewById(R.id.checkbox_excuse);
        mSeekPoints = view.findViewById(R.id.seekbar_points);
        mButtonBonus = view.findViewById(R.id.btn_add_bonus);

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
            ViewStub stub = view.findViewById(R.id.viewstub_partner);
            View v = stub.inflate();
            mPartner = v.findViewById(R.id.spinner_partner);
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
                showBonusDialog();
            }
        });
        for (TarotBonus bonus : getLap().getBonuses()) {
            addBonusView(bonus);
        }
        manageBonusButton();

        return view;
    }

    @SuppressLint("InflateParams")
    private void showBonusDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_bonus, null);
        final Spinner spinnerBonus = view.findViewById(R.id.spinner_bonus);
        spinnerBonus.setAdapter(getBonusAdapter());
        final Spinner spinnerPlayer = view.findViewById(R.id.spinner_player);
        spinnerPlayer.setAdapter(getPlayerAdapter());

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tarot_header_bonuses)
                .setView(view)
                .setPositiveButton(R.string.button_action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TarotBonus bonus = new TarotBonus(
                                ((TarotBonusItem) spinnerBonus.getSelectedItem()).mBonus,
                                spinnerPlayer.getSelectedItemPosition());
                        getLap().getBonuses().add(bonus);
                        addBonusView(bonus);
                        manageBonusButton();
                    }
                })
                .setNegativeButton(R.string.button_action_cancel, null)
                .create()
                .show();
    }

    private void addBonusView(final TarotBonus bonus) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.list_item_bonus, mContainer, false);

        TextView tv = view.findViewById(R.id.tv_bonus);
        tv.setText(TarotBonus.getLiteralBonus(getContext(), bonus.get()));

        tv = view.findViewById(R.id.tv_player);
        tv.setText(getGameHelper().getPlayer(bonus.getPlayer()).getName());

        ImageButton btn = view.findViewById(R.id.btn_remove_bonus);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().getBonuses().remove(bonus);
                mContainer.removeView(view);
                manageBonusButton();
            }
        });

        int pos = mContainer.getChildCount() - 1;
        mContainer.addView(view, pos);
    }

    private void manageBonusButton() {
        mButtonBonus.setEnabled(getLap().getBonuses().size() < 3);
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
        ArrayAdapter<TarotBidItem> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(new TarotBidItem(TarotBid.BID_PRISE));
        adapter.add(new TarotBidItem(TarotBid.BID_GARDE));
        adapter.add(new TarotBidItem(TarotBid.BID_GARDE_SANS));
        adapter.add(new TarotBidItem(TarotBid.BID_GARDE_CONTRE));
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
        ArrayAdapter<TarotBonusItem> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (!getLap().hasBonus(TarotBonus.BONUS_PETIT_AU_BOUT))
            adapter.add(new TarotBonusItem(TarotBonus.BONUS_PETIT_AU_BOUT));

        if (!getLap().hasBonus(TarotBonus.BONUS_POIGNEE_DOUBLE)
                && !getLap().hasBonus(TarotBonus.BONUS_POIGNEE_TRIPLE)) {
            adapter.add(new TarotBonusItem(TarotBonus.BONUS_POIGNEE_SIMPLE));
            if (!getLap().hasBonus(TarotBonus.BONUS_POIGNEE_SIMPLE)) {
                adapter.add(new TarotBonusItem(TarotBonus.BONUS_POIGNEE_DOUBLE));
                adapter.add(new TarotBonusItem(TarotBonus.BONUS_POIGNEE_TRIPLE));
            }
        }

        if (!getLap().hasBonus(TarotBonus.BONUS_CHELEM_NON_ANNONCE)
                && !getLap().hasBonus(TarotBonus.BONUS_CHELEM_ANNONCE_REALISE)
                && !getLap().hasBonus(TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE)) {
            adapter.add(new TarotBonusItem(TarotBonus.BONUS_CHELEM_NON_ANNONCE));
            adapter.add(new TarotBonusItem(TarotBonus.BONUS_CHELEM_ANNONCE_REALISE));
            adapter.add(new TarotBonusItem(TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE));
        }
        return adapter;
    }

    @Override
    public String onProgressChanged(SeekPoints seekPoints, int progress) {
        getLap().setPoints(progress);
        return Integer.toString(progress);
    }

    class TarotBidItem {

        @TarotBid.TarotBidValues
        final int mBid;

        TarotBidItem(@TarotBid.TarotBidValues int bid) {
            mBid = bid;
        }

        @Override
        public String toString() {
            return TarotBid.getLiteralBid(getActivity(), mBid);
        }
    }

    class TarotBonusItem {

        @TarotBonus.TarotBonusValues
        final int mBonus;

        TarotBonusItem(@TarotBonus.TarotBonusValues int bonus) {
            mBonus = bonus;
        }

        @Override
        public String toString() {
            return TarotBonus.getLiteralBonus(getActivity(), mBonus);
        }
    }
}
