package com.bx.scorecounter;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bx.scorecounter.db.ScoreData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreboardFragment extends Fragment {

    public static final int LAYOUT_GRID = 0;
    public static final int LAYOUT_ROWS = 1;
    public static final int LAYOUT_DISPLAY = 2;

    private static final String TAG = "ScoreboardFragment";

    private static final int[] gridContainerIds = new int[] {R.id.score_unit_container_0, R.id.score_unit_container_1, R.id.score_unit_container_2,
            R.id.score_unit_container_3, R.id.score_unit_container_4, R.id.score_unit_container_5, R.id.score_unit_container_6, R.id.score_unit_container_7};

    private int scoreboardNumber;
    private int scoreboardLayout;
    private ScoreboardViewModel model;
    private final List<ScoreUnitFragment> scoreUnitFragments = new ArrayList<>();

    // Views for display layout
    private View bottomSheet;
    private TextView labelTextView;
    private TextView scoreTextView;
    private MaterialButton incrementButton;
    private MaterialButton decrementButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        scoreboardNumber = Integer.valueOf(preferences.getString("scoreboard_number", "2"));
        if (scoreboardNumber <= 0) {
            Log.wtf(TAG, "Scoreboard number less than 1: " + scoreboardNumber);
        }
        scoreboardLayout = Integer.valueOf(preferences.getString("scoreboard_layout", "0"));
        model = ViewModelProviders.of(this).get(ScoreboardViewModel.class);
        if (scoreboardLayout == LAYOUT_DISPLAY) {
            model.getSelectedScoreData().observe(this, selected -> {
                if (selected == null) {
                    // hide buttons, set animations
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                    for (final ScoreUnitFragment f: scoreUnitFragments) {
                        f.getView().animate().alpha(1f);
                    }

                    labelTextView.setText(null);
                    scoreTextView.setText(null);
                    incrementButton.setText("+");
                    decrementButton.setText("-");
                    incrementButton.setOnClickListener(null);
                    decrementButton.setOnClickListener(null);
                    incrementButton.setOnLongClickListener(null);
                    decrementButton.setOnLongClickListener(null);
                } else {
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                    for (int i = 0; i < scoreUnitFragments.size(); i++) {
                        if (i == selected.position) {
                            scoreUnitFragments.get(i).getView().animate().alpha(1f);
                        } else {
                            scoreUnitFragments.get(i).getView().animate().alpha(0.4f);
                        }
                    }

                    labelTextView.setText(selected.label);
                    scoreTextView.setText(String.valueOf(selected.score));
                    incrementButton.setText("+" + selected.step);
                    decrementButton.setText("-" + selected.step);
                    final ScoreUnitFragment.OnInteractionListener listener = scoreUnitFragments.get(selected.position).listener;
                    incrementButton.setOnTouchListener((v, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            listener.onButtonTouch();
                        }
                        return false;
                    });
                    decrementButton.setOnTouchListener((v, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            listener.onButtonTouch();
                        }
                        return false;
                    });
                    incrementButton.setOnClickListener(v -> listener.onIncrement(selected));
                    decrementButton.setOnClickListener(v -> listener.onDecrement(selected));
                    incrementButton.setOnLongClickListener(v -> {
                        listener.onLongIncrement(selected);
                        return true;
                    });
                    decrementButton.setOnLongClickListener(v -> {
                        listener.onLongDecrement(selected);
                        return true;
                    });
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        switch (scoreboardLayout) {
            case LAYOUT_GRID:
                switch (scoreboardNumber) {
                    case 1:
                        return inflater.inflate(R.layout.fragment_scoreboard_1, container, false);
                    case 2:
                        return inflater.inflate(R.layout.fragment_scoreboard_2, container, false);
                    case 3:
                        return inflater.inflate(R.layout.fragment_scoreboard_3, container, false);
                    case 4:
                        return inflater.inflate(R.layout.fragment_scoreboard_4, container, false);
                    case 5:
                        return inflater.inflate(R.layout.fragment_scoreboard_5, container, false);
                    case 6:
                        return inflater.inflate(R.layout.fragment_scoreboard_6, container, false);
                    case 7:
                        return inflater.inflate(R.layout.fragment_scoreboard_7, container, false);
                    case 8:
                        return inflater.inflate(R.layout.fragment_scoreboard_8, container, false);
                    default:
                        Log.wtf(TAG, "Unknown scoreboard number: " + scoreboardNumber);
                        return inflater.inflate(R.layout.fragment_scoreboard_2, container, false);
                }
            case LAYOUT_ROWS:
                final LinearLayout rowLayout = new LinearLayout(getContext());
                rowLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                rowLayout.setOrientation(LinearLayout.VERTICAL);
                final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                for (int i = 0; i < scoreboardNumber; i++) {
                    final FrameLayout scoreUnitContainer = new FrameLayout(getContext());
                    scoreUnitContainer.setId(gridContainerIds[i]);
                    rowLayout.addView(scoreUnitContainer, lp);
                }
                return rowLayout;
            case LAYOUT_DISPLAY:
                final ViewGroup displayLayout = (ViewGroup) inflater.inflate(R.layout.fragment_scoreboard_display, container, false);
                final View scoreboardContainer;
                switch (scoreboardNumber) {
                    case 1:
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_1, displayLayout, false);
                        break;
                    case 2:
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_2, displayLayout, false);
                        break;
                    case 3:
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_3, displayLayout, false);
                        break;
                    case 4:
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_4, displayLayout, false);
                        break;
                    case 5:
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_5, displayLayout, false);
                        break;
                    case 6:
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_6, displayLayout, false);
                        break;
                    case 7:
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_7, displayLayout, false);
                        break;
                    case 8:
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_8, displayLayout, false);
                        break;
                    default:
                        Log.wtf(TAG, "Unknown scoreboard number: " + scoreboardNumber);
                        scoreboardContainer = inflater.inflate(R.layout.fragment_scoreboard_2, displayLayout, false);
                }
                displayLayout.addView(scoreboardContainer, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return displayLayout;
            default:
                Log.wtf(TAG, "Unknown scoreboard layout: " + scoreboardLayout);
                return inflater.inflate(R.layout.fragment_scoreboard_2, container, false);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // no button mode
        final boolean isNoButton = scoreboardLayout == LAYOUT_DISPLAY;
        if (isNoButton) {
            bottomSheet = view.findViewById(R.id.scoreboard_buttons);
            labelTextView = bottomSheet.findViewById(R.id.scoreboard_label_text_view);
            scoreTextView = bottomSheet.findViewById(R.id.scoreboard_score_text_view);
            incrementButton = bottomSheet.findViewById(R.id.scoreboard_increment_button);
            decrementButton = bottomSheet.findViewById(R.id.scoreboard_decrement_button);
        }

        // attach fragments
        scoreUnitFragments.clear();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < scoreboardNumber; i++) {
            final ScoreUnitFragment fragment = ScoreUnitFragment.newInstance(i, isNoButton);
            transaction.replace(gridContainerIds[i], fragment);
            scoreUnitFragments.add(fragment);
        }
        transaction.commit();
    }

    /**
     * Performs action on back press.
     *
     * @return {@code true} if back press handled in fragment, or {@code false} to allow back press
     * to pass through to activity.
     */
    public boolean onBackPressed() {
        if (scoreboardLayout != LAYOUT_DISPLAY || model.getSelectedScoreData().getValue() == null) {
            return false;
        } else {
            model.setSelectedScoreUnit(-1);
            return true;
        }
    }
}
