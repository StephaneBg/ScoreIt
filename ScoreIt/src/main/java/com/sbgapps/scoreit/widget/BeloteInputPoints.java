package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.GenericBeloteLap;
import com.sbgapps.scoreit.fragment.GenericBeloteLapFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 28/10/2014.
 */
public class BeloteInputPoints extends FrameLayout {

    @InjectView(R.id.player1_name)
    TextView mPlayer1Name;
    @InjectView(R.id.player2_name)
    TextView mPlayer2Name;
    @InjectView(R.id.player1_points)
    TextView mPlayer1Points;
    @InjectView(R.id.player2_points)
    TextView mPlayer2Points;
    @InjectView(R.id.btn_switch)
    CircleButton mSwitchBtn;
    @InjectView(R.id.group_score)
    ToggleGroup mScoreGroup;
    @InjectView(R.id.seekbar_points)
    SeekPoints mSeekPoints;

    public BeloteInputPoints(Context context) {
        this(context, null);
    }

    public BeloteInputPoints(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeloteInputPoints(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.belote_input_points, this, true);
        ButterKnife.inject(this);
    }

    public void init(final GenericBeloteLapFragment beloteFrag) {
        final GameHelper gameHelper = beloteFrag.getGameHelper();
        final GenericBeloteLap lap = beloteFrag.getLap();

        mPlayer1Name.setText(gameHelper.getPlayer(Player.PLAYER_1).getName());
        mPlayer2Name.setText(gameHelper.getPlayer(Player.PLAYER_2).getName());


        mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Player.PLAYER_1 == lap.getScorer()) {
                    lap.setScorer(Player.PLAYER_2);
                } else {
                    lap.setScorer(Player.PLAYER_1);
                }
                setScores(lap);
            }
        });

        mScoreGroup.setOnCheckedChangeListener(new ToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_score:
                        mSeekPoints.setVisibility(View.VISIBLE);
                        lap.setPoints(110);
                        mSeekPoints.setPoints(110, "110");
                        break;
                    case R.id.btn_inside:
                        mSeekPoints.setVisibility(View.GONE);
                        lap.setPoints(160);
                        break;
                    case R.id.btn_capot:
                        mSeekPoints.setVisibility(View.GONE);
                        lap.setPoints(250);
                        break;
                }
                setScores(lap);
            }
        });

        int points = lap.getPoints();
        if (160 == points) {
            mScoreGroup.check(R.id.btn_inside);
            mSeekPoints.setVisibility(View.GONE);
        } else if (250 == points) {
            mScoreGroup.check(R.id.btn_capot);
            mSeekPoints.setVisibility(View.GONE);
        } else {
        }
        mSeekPoints.init(
                points,
                162,
                Integer.toString(points));
        mSeekPoints.setOnProgressChangedListener(beloteFrag, "points");

        setScores(lap);
    }

    public void setScores(GenericBeloteLap lap) {
        int scores[] = lap.getScores();
        if (Player.PLAYER_1 == lap.getScorer()) {
            mPlayer1Points.setText(Integer.toString(scores[0]));
            mPlayer2Points.setText(Integer.toString(scores[1]));
        } else {
            mPlayer1Points.setText(Integer.toString(scores[1]));
            mPlayer2Points.setText(Integer.toString(scores[0]));
        }
    }
}
