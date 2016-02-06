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

package com.sbgapps.scoreit.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.fragments.ScoreListFragment;
import com.sbgapps.scoreit.models.Lap;
import com.sbgapps.scoreit.utils.GameHelper;
import com.sbgapps.scoreit.views.widgets.LinearListView;
import com.sbgapps.scoreit.views.widgets.RevealView;

/**
 * Created by sbaiget on 23/11/13.
 */
public abstract class ScoreListAdapter<E extends ScoreListAdapter.ViewHolder> extends RecyclerView.Adapter<E> {

    private final ScoreListFragment mScoreListFragment;

    public ScoreListAdapter(ScoreListFragment fragment) {
        mScoreListFragment = fragment;
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getActivity());
    }

    public ScoreItActivity getActivity() {
        return (ScoreItActivity) mScoreListFragment.getActivity();
    }

    public ScoreListFragment getFragment() {
        return mScoreListFragment;
    }

    @Override
    public void onBindViewHolder(final E viewHolder, int i) {
        final Lap lap = getGameHelper().getLaps().get(viewHolder.getAdapterPosition());
        viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().removeLap(lap);
                viewHolder.mRevealView.hide();
            }
        });

        viewHolder.mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().editLap(lap);
                viewHolder.mRevealView.hide();
            }
        });

        viewHolder.mRevealView.setRevealViewListener(new RevealView.RevealViewListener() {
            @Override
            public void onViewRevealed(RevealView revealView) {
                mScoreListFragment.closeOthers(revealView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getGameHelper().getLaps().size();
    }

    public GameHelper getGameHelper() {
        return getActivity().getGameHelper();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RevealView mRevealView;
        public LinearListView mLinearListView;
        public ImageButton mDeleteBtn;
        public ImageButton mEditBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mRevealView = (RevealView) itemView.findViewById(R.id.reveal);
            mLinearListView = (LinearListView) itemView.findViewById(R.id.list_score);
            mDeleteBtn = (ImageButton) itemView.findViewById(R.id.action_discard);
            mEditBtn = (ImageButton) itemView.findViewById(R.id.action_edit);
        }
    }
}