package com.sbgapps.scoreit.app.header;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbgapps.scoreit.app.R;
import com.sbgapps.scoreit.app.base.BaseFragment;
import com.sbgapps.scoreit.app.utils.LinearListHelper;
import com.sbgapps.scoreit.core.model.Player;
import com.sbgapps.scoreit.core.model.utils.GameHelper;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by sbaiget on 21/12/2016.
 */

public class HeaderFragment extends BaseFragment<HeaderViewActions, HeaderPresenter>
        implements HeaderViewActions {

    @BindView(R.id.ll_header)
    LinearLayout mHeaderContainer;
    ArrayList<HeaderItemHolder> mItems;

    public static HeaderFragment newInstance() {
        return new HeaderFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HeaderLinearListHelper helper = new HeaderLinearListHelper();
        mItems = helper.populateItems(
                mHeaderContainer,
                R.layout.item_header,
                GameHelper.getPlayerCount());

        getPresenter().setup();
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

    @Override
    public void showColorSelectorDialog(@Player.Players int player) {

    }

    private class HeaderLinearListHelper extends LinearListHelper<HeaderItemHolder> {

        @Override
        public HeaderItemHolder onCreateItem(View view) {
            return new HeaderItemHolder(view);
        }

        @Override
        public void onBindItem(HeaderItemHolder item, int player) {
            item.mName.setOnClickListener(l -> getPresenter().onNameChanged(player, ""));
            item.mScore.setOnClickListener(l -> getPresenter().onColorChanged(player, 0));
        }
    }

    class HeaderItemHolder {

        final TextView mName;
        final TextView mScore;
        final View mIndicator;

        HeaderItemHolder(View item) {
            mName = (TextView) item.findViewById(R.id.name);
            mScore = (TextView) item.findViewById(R.id.score);
            mIndicator = item.findViewById(R.id.marker);
        }
    }
}
