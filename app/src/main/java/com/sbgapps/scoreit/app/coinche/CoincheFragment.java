/*
 * Copyright 2017 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.app.coinche;

import android.support.annotation.NonNull;

import com.sbgapps.scoreit.app.R;
import com.sbgapps.scoreit.app.base.BaseFragment;

public class CoincheFragment extends BaseFragment<CoincheView, CoinchePresenter>
        implements CoincheView {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_coinche;
    }

    @Override
    @NonNull
    public CoinchePresenter createPresenter() {
        return new CoinchePresenter();
    }
}
