package com.sbgapps.scoreit.app.universal;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sbgapps.scoreit.app.R;
import com.sbgapps.scoreit.app.base.BaseFragment;
import com.sbgapps.scoreit.app.utils.LinearListHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sbaiget on 03/03/2017.
 */

public class UniversalFragment extends BaseFragment<UniversalViewActions, UniversalPresenter>
        implements UniversalViewActions {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_universal;
    }

    @Override
    @NonNull
    public UniversalPresenter createPresenter() {
        return new UniversalPresenter();
    }

    @Override
    public void setupPlayerCount(int count) {

    }

    private class UniversalLinearListHelper extends LinearListHelper<UniversalItemHolder> {

        @Override
        public UniversalItemHolder onCreateItem(View view, int player) {
            UniversalItemHolder item = new UniversalItemHolder(view);
            return item;
        }
    }

    private static class UniversalItemHolder {

        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.score)
        TextView mScore;
        @BindView(R.id.indicator)
        View mIndicator;

        UniversalItemHolder(View item) {
            ButterKnife.bind(this, item);
        }
    }
}
