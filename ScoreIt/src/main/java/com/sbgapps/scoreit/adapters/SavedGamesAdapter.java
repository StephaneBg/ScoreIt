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

package com.sbgapps.scoreit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ui.SavedGamesActivity;
import com.sbgapps.scoreit.views.RevealView;

/**
 * Created by sbaiget on 06/02/2015.
 */
public class SavedGamesAdapter extends RecyclerView.Adapter<SavedGamesAdapter.ViewHolder> {

    private final SavedGamesActivity mActivity;

    public SavedGamesAdapter(SavedGamesActivity activity) {
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_saved_game, parent, false);
        return new SavedGamesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.mRevealView.hide();
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
        //return mActivity.getFileNames().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RevealView mRevealView;
        public TextView mTextView;
        public ImageButton mDeleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mRevealView = (RevealView) itemView.findViewById(R.id.reveal);
            mTextView = (TextView) itemView.findViewById(R.id.filename);
            mDeleteBtn = (ImageButton) itemView.findViewById(R.id.action_discard);
        }
    }
}
