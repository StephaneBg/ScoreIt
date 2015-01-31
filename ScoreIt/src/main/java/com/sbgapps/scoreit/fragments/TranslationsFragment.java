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

package com.sbgapps.scoreit.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sbgapps.scoreit.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stéphane on 15/11/2014.
 */
public class TranslationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translations, container, false);

        Button iv = (Button) view.findViewById(R.id.btn_translate);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://crowdin.net/project/score-it"));
                startActivity(intent);
            }
        });

        ListView listView = (ListView) view.findViewById(android.R.id.list);

        String[] from = new String[]{"language", "translator"};
        int[] to = new int[]{R.id.language, R.id.translator};

        List<HashMap<String, String>> data = new ArrayList<>();
        HashMap<String, String> map;
        String[] l = getResources().getStringArray(R.array.languages);
        String[] t = getResources().getStringArray(R.array.translators);
        for (int i = 0; i < l.length; i++) {
            map = new HashMap<>();
            map.put("language", l[i]);
            map.put("translator", t[i]);
            data.add(map);
        }

        listView.setAdapter(new SimpleAdapter(getActivity(),
                data, R.layout.list_item_translation, from, to));

        return view;
    }
}
