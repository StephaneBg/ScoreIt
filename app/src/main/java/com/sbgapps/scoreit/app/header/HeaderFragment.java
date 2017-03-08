package com.sbgapps.scoreit.app.header;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kunzisoft.androidclearchroma.ChromaDialog;
import com.kunzisoft.androidclearchroma.colormode.ColorMode;
import com.kunzisoft.androidclearchroma.listener.OnColorSelectedListener;
import com.sbgapps.scoreit.app.R;
import com.sbgapps.scoreit.app.base.BaseFragment;
import com.sbgapps.scoreit.app.utils.LinearListHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sbaiget on 21/12/2016.
 */

public class HeaderFragment extends BaseFragment<HeaderViewActions, HeaderPresenter>
        implements HeaderViewActions, OnColorSelectedListener {

    private static final String TAG_FRAGMENT_COLOR = "ColorDialog";
    private static final int REQ_CODE_PERMISSION_CONTACTS = 100;
    private static final int REQ_CODE_RESULT_CONTACT = 101;

    @BindView(R.id.ll_header)
    LinearLayout mHeaderContainer;
    ArrayList<HeaderItemHolder> mItems;

    public static HeaderFragment newInstance() {
        return new HeaderFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().start();
    }

    @Override
    public void setupPlayerCount(int count) {
        HeaderLinearListHelper helper = new HeaderLinearListHelper();
        mItems = helper.populateItems(
                mHeaderContainer,
                R.layout.item_header,
                count);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_header;
    }

    @Override
    @NonNull
    public HeaderPresenter createPresenter() {
        return new HeaderPresenter();
    }

    @Override
    public void setName(int player, String name) {
        mItems.get(player).mName.setText(name);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setScore(int player, int score) {
        mItems.get(player).mScore.setText(Integer.toString(score));
    }

    @Override
    public void setColor(int player, @ColorInt int color) {
        mItems.get(player).mScore.setTextColor(color);
    }

    @SuppressWarnings("WrongConstant")
    @Override
    public void setIndicator(int player) {
        for (int i = 0; i < mItems.size(); i++) {
            mItems.get(i).mIndicator.setVisibility(i == player ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @SuppressWarnings("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK != resultCode && REQ_CODE_RESULT_CONTACT == requestCode) {
            Cursor cursor = getContext().getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
            if (null == cursor) return;

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String name = cursor.getString(columnIndex);
                name = name.split(" ")[0];
                getPresenter().onNameSelected(name);
            }
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQ_CODE_PERMISSION_CONTACTS == requestCode
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickContact();
        }
    }

    @Override
    public void showColorSelectorDialog(@ColorInt int initialColor) {
        new ChromaDialog.Builder()
                .initialColor(initialColor)
                .colorMode(ColorMode.RGB)
                .setOnColorSelectedListener(this)
                .create()
                .show(getChildFragmentManager(), TAG_FRAGMENT_COLOR);
    }

    @Override
    public void showNameActionsDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_title_edit_name)
                .setItems(R.array.dialog_edit_name_actions, (dialog, which) -> {
                    switch (which) {
                        default:
                        case 0:
                            if (isContactPermissionGranted()) {
                                pickContact();
                            } else {
                                requestContactPermission();
                            }
                            break;
                        case 1:
                            showEditNameDialog();
                            break;
                    }
                })
                .create()
                .show();
    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_RESULT_CONTACT);
    }

    private boolean isContactPermissionGranted() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                REQ_CODE_PERMISSION_CONTACTS);
    }

    private void showEditNameDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        final EditText editText = ButterKnife.findById(view, R.id.edit_text);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_title_edit_name)
                .setView(view)
                .setPositiveButton(R.string.button_action_ok, (dialog, which) -> {
                    String name = editText.getText().toString();
                    if (!name.isEmpty()) getPresenter().onNameSelected(name);
                })
                .setNegativeButton(R.string.button_action_cancel, null)
                .create()
                .show();
    }

    @Override
    public void onPositiveButtonClick(@ColorInt int color) {
        getPresenter().onColorSelected(color);
    }

    @Override
    public void onNegativeButtonClick(@ColorInt int color) {
    }

    private class HeaderLinearListHelper extends LinearListHelper<HeaderItemHolder> {

        @Override
        public HeaderFragment.HeaderItemHolder onCreateItem(View view, int player) {
            HeaderFragment.HeaderItemHolder item = new HeaderFragment.HeaderItemHolder(view);
            item.mName.setOnClickListener(l -> getPresenter().onNameSelectionStarted(player));
            item.mScore.setOnClickListener(l -> getPresenter().onColorSelectionStarted(player));
            return item;
        }
    }

    private static class HeaderItemHolder {

        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.score)
        TextView mScore;
        @BindView(R.id.indicator)
        View mIndicator;

        HeaderItemHolder(View item) {
            ButterKnife.bind(this, item);
        }
    }
}
