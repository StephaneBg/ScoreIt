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
