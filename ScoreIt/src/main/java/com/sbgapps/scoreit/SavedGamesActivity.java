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

package com.sbgapps.scoreit;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbgapps.scoreit.models.GameManager;

import java.util.List;

/**
 * Created by Stéphane on 04/08/2014.
 */
public class SavedGamesActivity extends BaseActivity {

    private GameManager mGameManager;
    private List<String> mGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle(getString(R.string.title_activity_saved_games));
        }

        mGameManager = new GameManager(this);
        mGames = mGameManager.getFileUtils().getSavedFiles();

        RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SaveAdapter());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_saved_games;
    }

    private class SaveAdapter extends RecyclerView.Adapter<SaveViewHolder> {

        @Override
        public SaveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new SaveViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final SaveViewHolder holder, int position) {
            holder.game.setText(mGames.get(position));
            holder.game.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String game = mGames.get(holder.getAdapterPosition());
                    mGameManager.getFileUtils().setPlayedFile(game);
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mGames.size();
        }
    }

    private static class SaveViewHolder extends RecyclerView.ViewHolder {

        TextView game;

        public SaveViewHolder(View itemView) {
            super(itemView);
            game = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
