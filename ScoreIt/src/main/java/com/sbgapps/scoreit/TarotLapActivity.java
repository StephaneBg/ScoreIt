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

package com.sbgapps.scoreit;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.tarot.TarotBonus;
import com.sbgapps.scoreit.games.tarot.TarotFiveLap;
import com.sbgapps.scoreit.games.tarot.TarotLap;
import com.sbgapps.scoreit.widget.SeekbarInputPoints;

/**
 * Created by sbaiget on 07/12/13.
 */
public class TarotLapActivity extends LapActivity {

    private static final LapHolder HOLDER = new LapHolder();
    private int mGame;

    @Override
    public TarotLap getLap() {
        return (TarotLap) super.getLap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lap_tarot);

        HOLDER.taker = (Spinner) findViewById(R.id.spinner_taker);
        HOLDER.deal = (Spinner) findViewById(R.id.spinner_deal);
        HOLDER.petit = (CheckBox) findViewById(R.id.checkbox_petit);
        HOLDER.twenty_one = (CheckBox) findViewById(R.id.checkbox_twenty_one);
        HOLDER.fool = (CheckBox) findViewById(R.id.checkbox_fool);
        HOLDER.input_points = (SeekbarInputPoints) findViewById(R.id.input_points);
        HOLDER.ll_bonuses = (LinearLayout) findViewById(R.id.ll_bonuses);

        if (isDialog()) {
            findViewById(R.id.btn_cancel).setOnClickListener(this);
            findViewById(R.id.btn_confirm).setOnClickListener(this);
        }

        mGame = getGameHelper().getPlayedGame();
        HOLDER.taker.setAdapter(getPlayerArrayAdapter());

        if (Game.TAROT_5_PLAYERS == mGame) {
            ViewStub stub = (ViewStub) findViewById(R.id.viewstub_partner);
            View view = stub.inflate();
            final ArrayAdapter<PlayerItem> partnerItemArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item);
            partnerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_1));
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_2));
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_3));
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_4));
            partnerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_5));
            HOLDER.partner = (Spinner) view.findViewById(R.id.spinner_partner);
            HOLDER.partner.setAdapter(partnerItemArrayAdapter);
        }

        final ArrayAdapter<DealItem> dealItemArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        dealItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dealItemArrayAdapter.add(new DealItem(TarotLap.BID_PRISE));
        dealItemArrayAdapter.add(new DealItem(TarotLap.BID_GARDE));
        dealItemArrayAdapter.add(new DealItem(TarotLap.BID_GARDE_SANS));
        dealItemArrayAdapter.add(new DealItem(TarotLap.BID_GARDE_CONTRE));
        HOLDER.deal.setAdapter(dealItemArrayAdapter);

        HOLDER.input_points.setMax(91);

        Button button = (Button) findViewById(R.id.btn_add_bonus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBonus(null);
            }
        });

        if (isEdited()) {
            HOLDER.taker.setSelection(getLap().getTaker());
            if (Game.TAROT_5_PLAYERS == mGame)
                HOLDER.partner.setSelection(((TarotFiveLap) getLap()).getPartner());
            HOLDER.deal.setSelection(getLap().getBid());
            HOLDER.petit.setChecked((getLap().getOudlers() & TarotLap.OUDLER_PETIT_MSK)
                    == TarotLap.OUDLER_PETIT_MSK);
            HOLDER.fool.setChecked((getLap().getOudlers() & TarotLap.OUDLER_EXCUSE_MSK)
                    == TarotLap.OUDLER_EXCUSE_MSK);
            HOLDER.twenty_one.setChecked((getLap().getOudlers() & TarotLap.OUDLER_21_MSK)
                    == TarotLap.OUDLER_21_MSK);
            HOLDER.input_points.setPoints(getLap().getPoints());
            for (TarotBonus bonus : getLap().getBonuses()) {
                addBonus(bonus);
            }
        } else {
            HOLDER.input_points.setPoints(41);
        }
    }

    @Override
    public void updateLap() {
        TarotLap lap = getLap();
        lap.setTaker(((PlayerItem) HOLDER.taker.getSelectedItem()).getPlayer());
        lap.setBid(((DealItem) HOLDER.deal.getSelectedItem()).getDeal());
        lap.setPoints(HOLDER.input_points.getPoints());
        lap.setOudlers(getOudlers());
        if (Game.TAROT_5_PLAYERS == getGameHelper().getPlayedGame())
            ((TarotFiveLap) lap).setPartner(
                    ((PlayerItem) HOLDER.partner.getSelectedItem()).getPlayer());
        lap.setScores();
    }

    @Override
    public int progressToPoints(int progress) {
        return progress;
    }

    @Override
    public int pointsToProgress(int points) {
        return points;
    }

    private int getOudlers() {
        return (HOLDER.petit.isChecked() ? TarotLap.OUDLER_PETIT_MSK : 0x00)
                | (HOLDER.fool.isChecked() ? TarotLap.OUDLER_EXCUSE_MSK : 0x00)
                | (HOLDER.twenty_one.isChecked() ? TarotLap.OUDLER_21_MSK : 0x00);
    }

    private void addBonus(TarotBonus tarotBonus) {
        if (null == tarotBonus) {
            tarotBonus = new TarotBonus();
            getLap().getBonuses().add(tarotBonus);
        }
        final TarotBonus bonus = tarotBonus;

        final View view = getLayoutInflater()
                .inflate(R.layout.list_item_bonus, HOLDER.ll_bonuses, false);
        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_remove_announce);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().getBonuses().remove(bonus);
                HOLDER.ll_bonuses.removeView(view);
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_announce);
        spinner.setAdapter(getAnnounceArrayAdapter());
        spinner.setSelection(bonus.getBonus());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AnnounceItem ai = (AnnounceItem) parent.getAdapter().getItem(position);
                bonus.setBonus(ai.getAnnouce());
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
                PlayerItem pi = (PlayerItem) parent.getAdapter().getItem(position);
                bonus.setPlayer(pi.getPlayer());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int pos = HOLDER.ll_bonuses.getChildCount() - 1;
        HOLDER.ll_bonuses.addView(view, pos);
    }

    private ArrayAdapter<PlayerItem> getPlayerArrayAdapter() {
        ArrayAdapter<PlayerItem> playerItemArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        playerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_1));
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_2));
        playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_3));
        switch (mGame) {
            case Game.TAROT_4_PLAYERS:
                playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_4));
                break;
            case Game.TAROT_5_PLAYERS:
                playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_4));
                playerItemArrayAdapter.add(new PlayerItem(Player.PLAYER_5));
                break;
        }
        return playerItemArrayAdapter;
    }

    private ArrayAdapter<AnnounceItem> getAnnounceArrayAdapter() {
        final ArrayAdapter<AnnounceItem> announceItemArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        announceItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        announceItemArrayAdapter.add(new AnnounceItem(TarotBonus.BONUS_PETIT_AU_BOUT));
        announceItemArrayAdapter.add(new AnnounceItem(TarotBonus.BONUS_POIGNEE_SIMPLE));
        announceItemArrayAdapter.add(new AnnounceItem(TarotBonus.BONUS_POIGNEE_DOUBLE));
        announceItemArrayAdapter.add(new AnnounceItem(TarotBonus.BONUS_POIGNEE_TRIPLE));
        announceItemArrayAdapter.add(new AnnounceItem(TarotBonus.BONUS_CHELEM_NON_ANNONCE));
        announceItemArrayAdapter.add(new AnnounceItem(TarotBonus.BONUS_CHELEM_ANNONCE));
        return announceItemArrayAdapter;
    }

    static class LapHolder {
        Spinner taker;
        Spinner deal;
        Spinner partner;
        CheckBox petit;
        CheckBox twenty_one;
        CheckBox fool;
        SeekbarInputPoints input_points;
        LinearLayout ll_bonuses;
    }

    class DealItem {

        final int mDeal;

        DealItem(int deal) {
            mDeal = deal;
        }

        public int getDeal() {
            return mDeal;
        }

        @Override
        public String toString() {
            Resources r = getResources();
            switch (mDeal) {
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

        public int getPlayer() {
            return mPlayer;
        }

        @Override
        public String toString() {
            return getGameHelper().getPlayerName(mPlayer);
        }
    }

    class AnnounceItem {

        final int mAnnouce;

        AnnounceItem(int annouce) {
            mAnnouce = annouce;
        }

        public int getAnnouce() {
            return mAnnouce;
        }

        @Override
        public String toString() {
            Resources r = getResources();
            switch (mAnnouce) {
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
                case TarotBonus.BONUS_CHELEM_ANNONCE:
                    return r.getString(R.string.chelem_annonce);
            }
            return null;
        }
    }
}
