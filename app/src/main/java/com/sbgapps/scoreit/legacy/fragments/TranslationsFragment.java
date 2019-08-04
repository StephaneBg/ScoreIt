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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sbgapps.scoreit.R;

/**
 * Created by Stéphane on 15/11/2014.
 */
public class TranslationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translations, container, false);

        RecyclerView recyclerView = view.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new TranslationsAdapter());

        return view;
    }

    private class TranslationsAdapter extends RecyclerView.Adapter<TranslationViewHolder> {

        @Override
        public TranslationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_translation, parent, false);
            return new TranslationViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TranslationViewHolder holder, int position) {
            holder.translator.setText(getResources().getStringArray(R.array.translators)[position]);
            holder.language.setText(getResources().getStringArray(R.array.languages)[position]);
        }

        @Override
        public int getItemCount() {
            return getResources().getStringArray(R.array.translators).length;
        }
    }

    private static class TranslationViewHolder extends RecyclerView.ViewHolder {

        TextView translator;
        TextView language;

        public TranslationViewHolder(View itemView) {
            super(itemView);
            translator = itemView.findViewById(R.id.translator);
            language = itemView.findViewById(R.id.language);
        }
    }
}
