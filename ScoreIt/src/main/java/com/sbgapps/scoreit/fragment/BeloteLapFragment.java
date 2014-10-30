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

package com.sbgapps.scoreit.fragment;

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

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.BeloteBonus;
import com.sbgapps.scoreit.games.belote.BeloteLap;
import com.sbgapps.scoreit.widget.BeloteInputPoints;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 01/11/13.
 */
public class BeloteLapFragment extends GenericBeloteLapFragment {

    @InjectView(R.id.input_points)
    BeloteInputPoints mInputPoints;
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

        mInputPoints.init(this);

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
        mButtonBonus.setEnabled(bonuses.size() < 3);
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
