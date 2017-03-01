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

import com.sbgapps.scoreit.app.GameManager;
import com.sbgapps.scoreit.app.R;
import com.sbgapps.scoreit.app.ScoreItApp;
import com.sbgapps.scoreit.app.base.BaseFragment;
import com.sbgapps.scoreit.app.utils.LinearListHelper;
import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Player;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;

/**
 * Created by sbaiget on 21/12/2016.
 */

public class HeaderFragment extends BaseFragment<HeaderViewActions, HeaderPresenter>
        implements HeaderViewActions {

    private static final int REQ_CODE_PERMISSION_CONTACTS = 100;
    private static final int REQ_CODE_RESULT_CONTACT = 101;
    private static final String EXTRA_EDITED_PLAYER = "EditedPlayer";

    @BindView(R.id.ll_header)
    LinearLayout mHeaderContainer;
    ArrayList<HeaderItemHolder> mItems;

    GameManager mGameManager;

    public static HeaderFragment newInstance() {
        return new HeaderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameManager = ScoreItApp.getGameManager();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HeaderLinearListHelper helper = new HeaderLinearListHelper();
        mItems = helper.populateItems(
                mHeaderContainer,
                R.layout.item_header,
                mGameManager.getPlayerCount());

        getPresenter().setup();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_header;
    }

    @Override
    @NonNull
    public HeaderPresenter createPresenter() {
        return new HeaderPresenter(mGameManager);
    }

    @Override
    public void setName(@Player.Players int player, String name) {
        mItems.get(player).mName.setText(name);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setScore(@Player.Players int player, int score) {
        mItems.get(player).mScore.setText(Integer.toString(score));
    }

    @Override
    public void setColor(@Player.Players int player, @ColorInt int color) {
        mItems.get(player).mScore.setTextColor(color);
    }

    @SuppressWarnings("WrongConstant")
    @Override
    public void setIndicator(@Player.Players int player) {
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
                int player = data.getIntExtra(EXTRA_EDITED_PLAYER, Player.PLAYER_1);
                name = name.split(" ")[0];
                getGame().getPlayer(player).setName(name);
                setName(player, name);
            }
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQ_CODE_PERMISSION_CONTACTS == requestCode &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickContact(Player.PLAYER_1);
        }
    }

    private void showColorSelectorDialog(@Player.Players int player) {
        new ChromaDialog.Builder()
                .initialColor(getGame().getPlayer(player).getColor())
                .colorMode(ColorMode.RGB)
                .onColorSelected(color -> {
                    getGame().getPlayer(player).setColor(color);
                    setColor(player, color);
                })
                .create()
                .show(getChildFragmentManager(), "ColorDialog");
    }

    private void showNameActionsDialog(@Player.Players int player) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_title_edit_name)
                .setItems(R.array.dialog_edit_name_actions, (dialog, which) -> {
                    switch (which) {
                        default:
                        case 0:
                            if (ContextCompat.checkSelfPermission(getContext(),
                                    Manifest.permission.READ_CONTACTS)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        REQ_CODE_PERMISSION_CONTACTS);
                            } else {
                                pickContact(player);
                            }
                            break;
                        case 1:
                            showEditNameDialog(player);
                            break;
                    }
                })
                .create()
                .show();
    }

    private void pickContact(@Player.Players int player) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.putExtra(EXTRA_EDITED_PLAYER, player);
        startActivityForResult(intent, REQ_CODE_RESULT_CONTACT);
    }

    private void showEditNameDialog(@Player.Players int player) {
//        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_input_text, null);
//        final EditText editText = (EditText) view.findViewById(R.id.edit_text);
//
//        new AlertDialog.Builder(getContext())
//                .setTitle(R.string.dialog_title_edit_name)
//                .setView(view)
//                .setPositiveButton(R.string.button_action_ok, (dialog, which) -> {
//                    String name = editText.getText().toString();
//                    if (!name.isEmpty()) {
//                        getGame().getPlayer(which).setName(name);
//                        getView().setName(player, name);
//                        mEditedPlayer = Player.PLAYER_NONE;
//                        mHeaderFragment.update();
//                        if (mScoreListFragment.isVisible()) mScoreListFragment.update();
//                    }
//                })
//                .setNegativeButton(R.string.button_action_cancel, null)
//                .create()
//                .show();
    }

    private class HeaderLinearListHelper extends LinearListHelper<HeaderItemHolder> {

        @Override
        public HeaderItemHolder onCreateItem(View view) {
            return new HeaderItemHolder(view);
        }

        @Override
        public void onBindItem(HeaderItemHolder item, int player) {
            item.mName.setOnClickListener(l -> showNameActionsDialog(player));
            item.mScore.setOnClickListener(l -> showColorSelectorDialog(player));
        }
    }

    private Game getGame() {
        return mGameManager.getGame();
    }

    class HeaderItemHolder {

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
