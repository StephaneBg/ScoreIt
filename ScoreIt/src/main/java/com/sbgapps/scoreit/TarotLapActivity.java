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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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
import com.sbgapps.scoreit.games.tarot.TarotAnnounce;
import com.sbgapps.scoreit.games.tarot.TarotFiveLap;
import com.sbgapps.scoreit.games.tarot.TarotLap;
import com.sbgapps.scoreit.util.Constants;
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
        HOLDER.ll_announces = (LinearLayout) findViewById(R.id.ll_announces);

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
                addAnnounce(null);
            }
        });

        if (isEdited()) {
            HOLDER.taker.setSelection(getLap().getTaker());
            if (Game.TAROT_5_PLAYERS == mGame)
                HOLDER.partner.setSelection(((TarotFiveLap) getLap()).getPartner());
            HOLDER.deal.setSelection(getLap().getDeal());
            HOLDER.petit.setChecked((getLap().getOudlers() & TarotLap.OUDLER_PETIT_MSK)
                    == TarotLap.OUDLER_PETIT_MSK);
            HOLDER.fool.setChecked((getLap().getOudlers() & TarotLap.OUDLER_FOOL_MSK)
                    == TarotLap.OUDLER_FOOL_MSK);
            HOLDER.twenty_one.setChecked((getLap().getOudlers() & TarotLap.OUDLER_21_MSK)
                    == TarotLap.OUDLER_21_MSK);
            HOLDER.input_points.setPoints(getLap().getPoints());
            for (TarotAnnounce ta : getLap().getAnnounces()) {
                addAnnounce(ta);
            }
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
                | (HOLDER.fool.isChecked() ? TarotLap.OUDLER_FOOL_MSK : 0x00)
                | (HOLDER.twenty_one.isChecked() ? TarotLap.OUDLER_21_MSK : 0x00);
    }

    private void addAnnounce(TarotAnnounce tarotAnnounce) {
        if (null == tarotAnnounce) {
            tarotAnnounce = new TarotAnnounce();
            getLap().getAnnounces().add(tarotAnnounce);
        }
        final TarotAnnounce ta = tarotAnnounce;

        final View view = getLayoutInflater()
                .inflate(R.layout.list_item_announce, HOLDER.ll_announces, false);
        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_remove_announce);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator =
                        ObjectAnimator.ofFloat(view, "alpha", Constants.ALPHA_MAX, Constants.ALPHA_MIN);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getLap().getAnnounces().remove(ta);
                        HOLDER.ll_announces.removeView(view);
                    }
                });
                animator.setDuration(Constants.ANIM_DURATION).start();
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_announce);
        spinner.setAdapter(getAnnounceArrayAdapter());
        spinner.setSelection(ta.getAnnounce());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AnnounceItem ai = (AnnounceItem) parent.getAdapter().getItem(position);
                ta.setAnnounce(ai.getAnnouce());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner = (Spinner) view.findViewById(R.id.spinner_player);
        ArrayAdapter<PlayerItem> aa = getPlayerArrayAdapter();
        spinner.setAdapter(aa);
        spinner.setSelection(ta.getPlayer());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PlayerItem pi = (PlayerItem) parent.getAdapter().getItem(position);
                ta.setPlayer(pi.getPlayer());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        HOLDER.ll_announces.addView(view);
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
        announceItemArrayAdapter.add(new AnnounceItem(TarotAnnounce.TYPE_PETIT_AU_BOUT));
        announceItemArrayAdapter.add(new AnnounceItem(TarotAnnounce.TYPE_POIGNEE_SIMPLE));
        announceItemArrayAdapter.add(new AnnounceItem(TarotAnnounce.TYPE_POIGNEE_DOUBLE));
        announceItemArrayAdapter.add(new AnnounceItem(TarotAnnounce.TYPE_MISERE_ATOUT));
        announceItemArrayAdapter.add(new AnnounceItem(TarotAnnounce.TYPE_MISERE_TETE));
        announceItemArrayAdapter.add(new AnnounceItem(TarotAnnounce.TYPE_PETIT_CHELEM));
        announceItemArrayAdapter.add(new AnnounceItem(TarotAnnounce.TYPE_CHELEM_NON_ANNONCE));
        announceItemArrayAdapter.add(new AnnounceItem(TarotAnnounce.TYPE_CHELEM_ANNONCE));
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
        LinearLayout ll_announces;
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
                case TarotAnnounce.TYPE_PETIT_AU_BOUT:
                    return r.getString(R.string.petit_au_bout);
                case TarotAnnounce.TYPE_POIGNEE_SIMPLE:
                    return r.getString(R.string.poignee_simple);
                case TarotAnnounce.TYPE_POIGNEE_DOUBLE:
                    return r.getString(R.string.poignee_double);
                case TarotAnnounce.TYPE_MISERE_ATOUT:
                    return r.getString(R.string.misere_atout);
                case TarotAnnounce.TYPE_MISERE_TETE:
                    return r.getString(R.string.misere_tete);
                case TarotAnnounce.TYPE_PETIT_CHELEM:
                    return r.getString(R.string.petit_chelem);
                case TarotAnnounce.TYPE_CHELEM_NON_ANNONCE:
                    return r.getString(R.string.chelem_non_annonce);
                case TarotAnnounce.TYPE_CHELEM_ANNONCE:
                    return r.getString(R.string.chelem_annonce);
            }
            return null;
        }
    }
}
