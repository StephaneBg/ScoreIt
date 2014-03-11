/*
 * Copyright 2013 SBG Apps
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.sbgapps.scoreit;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by sbaiget on 08/02/14.
 */
public class EditNameActivity extends BaseActivity
        implements TextView.OnEditorActionListener {

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAccentHelper().prepareDialog(this, getWindow());

        setContentView(R.layout.activity_edit_name);
        mEditText = (EditText) findViewById(R.id.edit_name);
        setTitle(R.string.edit_name);
        mEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            Intent intent = new Intent();
            intent.putExtra(ScoreItActivity.EXTRA_NAME, mEditText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return false;
    }
}
