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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.games.tarot.TarotFiveLap;
import com.sbgapps.scoreit.games.tarot.TarotLap;
import com.sbgapps.scoreit.widget.SeekbarInputPoints;

/**
 * Created by sbaiget on 07/12/13.
 */
public class TarotLapActivity extends LapActivity {

    private static final LapHolder HOLDER = new LapHolder();

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
        HOLDER.mAnnounces = (LinearLayout) findViewById(R.id.ll_announces);

        if (isDialog()) {
            findViewById(R.id.btn_cancel).setOnClickListener(this);
            findViewById(R.id.btn_confirm).setOnClickListener(this);
        }

        final int game = getGameHelper().getPlayedGame();
        final ArrayAdapter<PlayerItem> playerItemArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        playerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_1));
        playerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_2));
        playerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_3));
        switch (game) {
            case GameHelper.TAROT_4_PLAYERS:
                playerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_4));
                break;
            case GameHelper.TAROT_5_PLAYERS:
                playerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_4));
                playerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_5));
                break;
        }
        HOLDER.taker.setAdapter(playerItemArrayAdapter);

        if (GameHelper.TAROT_5_PLAYERS == game) {
            ViewStub stub = (ViewStub) findViewById(R.id.viewstub_partner);
            View view = stub.inflate();
            final ArrayAdapter<PlayerItem> partnerItemArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item);
            partnerItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            partnerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_1));
            partnerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_2));
            partnerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_3));
            partnerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_4));
            partnerItemArrayAdapter.add(new PlayerItem(Lap.PLAYER_5));
            HOLDER.partner = (Spinner) view.findViewById(R.id.spinner_partner);
            HOLDER.partner.setAdapter(partnerItemArrayAdapter);
        }

        final ArrayAdapter<DealItem> dealItemArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        dealItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dealItemArrayAdapter.add(new DealItem(TarotLap.DEAL_TAKE));
        dealItemArrayAdapter.add(new DealItem(TarotLap.DEAL_GUARD));
        dealItemArrayAdapter.add(new DealItem(TarotLap.DEAL_WITHOUT));
        dealItemArrayAdapter.add(new DealItem(TarotLap.DEAL_AGAINST));
        HOLDER.deal.setAdapter(dealItemArrayAdapter);

        HOLDER.input_points.setMax(91);

        Button button = (Button) findViewById(R.id.btn_add_announce);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutInflater().inflate(R.layout.lap_include_announce, HOLDER.mAnnounces);
            }
        });

        if (isEdited()) {
            HOLDER.taker.setSelection(getLap().getTaker());
            if (GameHelper.TAROT_5_PLAYERS == game)
                HOLDER.partner.setSelection(((TarotFiveLap) getLap()).getPartner());
            HOLDER.deal.setSelection(getLap().getDeal());
            HOLDER.petit.setChecked((getLap().getOudlers() & TarotLap.OUDLER_PETIT_MSK)
                    == TarotLap.OUDLER_PETIT_MSK);
            HOLDER.fool.setChecked((getLap().getOudlers() & TarotLap.OUDLER_FOOL_MSK)
                    == TarotLap.OUDLER_FOOL_MSK);
            HOLDER.twenty_one.setChecked((getLap().getOudlers() & TarotLap.OUDLER_21_MSK)
                    == TarotLap.OUDLER_21_MSK);
            HOLDER.input_points.setPoints(getLap().getPoints());
        } else {
            HOLDER.input_points.setPoints(41);
        }
    }

    @Override
    public void updateLap() {
        TarotLap lap = getLap();
        lap.setTaker(((PlayerItem) HOLDER.taker.getSelectedItem()).getPlayer());
        lap.setDeal(((DealItem) HOLDER.deal.getSelectedItem()).getDeal());
        lap.setPoints(HOLDER.input_points.getPoints());
        lap.setOudlers(getOudlers());
        if (GameHelper.TAROT_5_PLAYERS == getGameHelper().getPlayedGame())
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
                | (HOLDER.fool.isChecked() ? TarotLap.OUDLER_FOOL_MSK : 0x00)
                | (HOLDER.twenty_one.isChecked() ? TarotLap.OUDLER_21_MSK : 0x00);
    }

    static class LapHolder {
        Spinner taker;
        Spinner deal;
        Spinner partner;
        CheckBox petit;
        CheckBox twenty_one;
        CheckBox fool;
        SeekbarInputPoints input_points;
        LinearLayout mAnnounces;
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
                case TarotLap.DEAL_TAKE:
                    return r.getString(R.string.take);
                case TarotLap.DEAL_GUARD:
                    return r.getString(R.string.guard);
                case TarotLap.DEAL_AGAINST:
                    return r.getString(R.string.guard_against);
                case TarotLap.DEAL_WITHOUT:
                    return r.getString(R.string.guard_without);
            }
            return null;
        }
    }

    public class PlayerItem {

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
}
