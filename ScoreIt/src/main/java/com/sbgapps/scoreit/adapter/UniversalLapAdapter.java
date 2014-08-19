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

package com.sbgapps.scoreit.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sbgapps.scoreit.games.universal.UniversalInput;

import java.util.List;

/**
 * Created by Stéphane on 19/08/2014.
 */
public class UniversalLapAdapter extends BaseAdapter {

    private final List<UniversalInput> mInputs;

    public UniversalLapAdapter(List<UniversalInput> inputs) {
        mInputs = inputs;
    }

    @Override
    public int getCount() {
        return mInputs.size();
    }

    @Override
    public UniversalInput getItem(int position) {
        return mInputs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position);
    }
}
