package com.bx.scorecounter;

import com.bx.scorecounter.db.ScoreData;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;

public class ScoreUnitFragment extends Fragment {

    private static final String TAG = "ScoreUnitFragment";
    private static final String ARG_POSITION = "position";
    private static final String ARG_NO_BUTTON = "no_button";

    OnInteractionListener listener;
    private int position;
    // 2 different modes
    private boolean isNoButton;
    private ScoreData scoreData;

    private ScoreboardViewModel model;
    private View labelBackground;
    private TextView labelTextView;
    private TextView stepTextView;
    private MaterialButton settingsButton;
    private TextView scoreTextView;
    private MaterialButton incrementButton;
    private MaterialButton decrementButton;

    private final Observer<ScoreData> scoreDataObserver = input -> {
        final ScoreData oldScoreData = scoreData;
        scoreData = input;
        if (scoreData == null) {
            model.insertDefaultScoreData(position);
        } else {
            if (oldScoreData == null || scoreData.style != oldScoreData.style || scoreData.color != oldScoreData.color) {
                // commit now to get views from loading layout
                getFragmentManager().beginTransaction().detach(this).attach(this).commitNow();
            }
            scoreTextView.setText(String.valueOf(scoreData.score));
            labelTextView.setText(scoreData.label);
            stepTextView.setText(String.format("(±%s)", scoreData.step));
        }
    };

    private final View.OnDragListener onDragListener = (v, event) -> {
        final Object localState = event.getLocalState();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return localState instanceof ScoreData && ((ScoreData) localState).position != scoreData.position;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setForeground(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setForeground(null);
                return true;
            case DragEvent.ACTION_DROP:
                // just in case
                if (localState instanceof ScoreData && localState != scoreData) {
                    listener.onScoreUnitSwap((ScoreData) localState, scoreData);
                }
                v.setForeground(null);
                return true;
            default:
                // rest of the cases
                return false;
        }
    };

    @NonNull
    public static ScoreUnitFragment newInstance(int position, boolean noButton) {
        final ScoreUnitFragment fragment = new ScoreUnitFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putBoolean(ARG_NO_BUTTON, noButton);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            isNoButton = getArguments().getBoolean(ARG_NO_BUTTON);
        } else {
            Log.wtf(TAG, "No arguments");
        }
        // view model of scoreboard fragment
        model = ViewModelProviders.of(getParentFragment()).get(ScoreboardViewModel.class);
        model.getDefaultSessionScoreDataSync(position).observe(this, scoreDataObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (scoreData == null) {
            return inflater.inflate(R.layout.fragment_loading, container, false);
        }
        if (isNoButton) {
            switch (scoreData.color) {
                case LIGHT:
                    return inflater.inflate(R.layout.fragment_score_unit_no_button, container, false);
                case DARK:
                    return inflater.inflate(R.layout.fragment_score_unit_no_button_dark, container, false);
                case RED:
                    return inflater.inflate(R.layout.fragment_score_unit_no_button_red, container, false);
                case GREEN:
                    return inflater.inflate(R.layout.fragment_score_unit_no_button_green, container, false);
                case BLUE:
                    return inflater.inflate(R.layout.fragment_score_unit_no_button_blue, container, false);
                case YELLOW:
                    return inflater.inflate(R.layout.fragment_score_unit_no_button_yellow, container, false);
                case PURPLE:
                    return inflater.inflate(R.layout.fragment_score_unit_no_button_purple, container, false);
                case CYAN:
                    return inflater.inflate(R.layout.fragment_score_unit_no_button_cyan, container, false);
                default:
                    Log.wtf(TAG, "Unknown color: " + scoreData.color);
                    return inflater.inflate(R.layout.fragment_score_unit_no_button, container, false);
            }
        } else {
            switch (scoreData.color) {
                case LIGHT:
                    switch (scoreData.style) {
                        case BUTTON_LEFT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_left, container, false);
                        case BUTTON_RIGHT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_right, container, false);
                        case BUTTON_TOP:
                            return inflater.inflate(R.layout.fragment_score_unit_button_top, container, false);
                        case BUTTON_BOTTOM:
                            return inflater.inflate(R.layout.fragment_score_unit_button_bottom, container, false);
                        case BUTTON_SIDE:
                            return inflater.inflate(R.layout.fragment_score_unit_button_side, container, false);
                        case INTEGRATED_HORIZONTAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_horizontal, container, false);
                        case INTEGRATED_VERTICAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_vertical, container, false);
                        default:
                            Log.wtf(TAG, "Unknown style: " + scoreData.style);
                            return inflater.inflate(R.layout.fragment_score_unit_button_right, container, false);
                    }
                case DARK:
                    switch (scoreData.style) {
                        case BUTTON_LEFT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_left_dark, container, false);
                        case BUTTON_RIGHT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_dark, container, false);
                        case BUTTON_TOP:
                            return inflater.inflate(R.layout.fragment_score_unit_button_top_dark, container, false);
                        case BUTTON_BOTTOM:
                            return inflater.inflate(R.layout.fragment_score_unit_button_bottom_dark, container, false);
                        case BUTTON_SIDE:
                            return inflater.inflate(R.layout.fragment_score_unit_button_side_dark, container, false);
                        case INTEGRATED_HORIZONTAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_horizontal_dark, container, false);
                        case INTEGRATED_VERTICAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_vertical_dark, container, false);
                        default:
                            Log.wtf(TAG, "Unknown style: " + scoreData.style);
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_dark, container, false);
                    }
                case RED:
                    switch (scoreData.style) {
                        case BUTTON_LEFT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_left_red, container, false);
                        case BUTTON_RIGHT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_red, container, false);
                        case BUTTON_TOP:
                            return inflater.inflate(R.layout.fragment_score_unit_button_top_red, container, false);
                        case BUTTON_BOTTOM:
                            return inflater.inflate(R.layout.fragment_score_unit_button_bottom_red, container, false);
                        case BUTTON_SIDE:
                            return inflater.inflate(R.layout.fragment_score_unit_button_side_red, container, false);
                        case INTEGRATED_HORIZONTAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_horizontal_red, container, false);
                        case INTEGRATED_VERTICAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_vertical_red, container, false);
                        default:
                            Log.wtf(TAG, "Unknown style: " + scoreData.style);
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_red, container, false);
                    }
                case GREEN:
                    switch (scoreData.style) {
                        case BUTTON_LEFT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_left_green, container, false);
                        case BUTTON_RIGHT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_green, container, false);
                        case BUTTON_TOP:
                            return inflater.inflate(R.layout.fragment_score_unit_button_top_green, container, false);
                        case BUTTON_BOTTOM:
                            return inflater.inflate(R.layout.fragment_score_unit_button_bottom_green, container, false);
                        case BUTTON_SIDE:
                            return inflater.inflate(R.layout.fragment_score_unit_button_side_green, container, false);
                        case INTEGRATED_HORIZONTAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_horizontal_green, container, false);
                        case INTEGRATED_VERTICAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_vertical_green, container, false);
                        default:
                            Log.wtf(TAG, "Unknown style: " + scoreData.style);
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_green, container, false);
                    }
                case BLUE:
                    switch (scoreData.style) {
                        case BUTTON_LEFT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_left_blue, container, false);
                        case BUTTON_RIGHT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_blue, container, false);
                        case BUTTON_TOP:
                            return inflater.inflate(R.layout.fragment_score_unit_button_top_blue, container, false);
                        case BUTTON_BOTTOM:
                            return inflater.inflate(R.layout.fragment_score_unit_button_bottom_blue, container, false);
                        case BUTTON_SIDE:
                            return inflater.inflate(R.layout.fragment_score_unit_button_side_blue, container, false);
                        case INTEGRATED_HORIZONTAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_horizontal_blue, container, false);
                        case INTEGRATED_VERTICAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_vertical_blue, container, false);
                        default:
                            Log.wtf(TAG, "Unknown style: " + scoreData.style);
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_blue, container, false);
                    }
                case YELLOW:
                    switch (scoreData.style) {
                        case BUTTON_LEFT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_left_yellow, container, false);
                        case BUTTON_RIGHT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_yellow, container, false);
                        case BUTTON_TOP:
                            return inflater.inflate(R.layout.fragment_score_unit_button_top_yellow, container, false);
                        case BUTTON_BOTTOM:
                            return inflater.inflate(R.layout.fragment_score_unit_button_bottom_yellow, container, false);
                        case BUTTON_SIDE:
                            return inflater.inflate(R.layout.fragment_score_unit_button_side_yellow, container, false);
                        case INTEGRATED_HORIZONTAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_horizontal_yellow, container, false);
                        case INTEGRATED_VERTICAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_vertical_yellow, container, false);
                        default:
                            Log.wtf(TAG, "Unknown style: " + scoreData.style);
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_yellow, container, false);
                    }
                case PURPLE:
                    switch (scoreData.style) {
                        case BUTTON_LEFT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_left_purple, container, false);
                        case BUTTON_RIGHT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_purple, container, false);
                        case BUTTON_TOP:
                            return inflater.inflate(R.layout.fragment_score_unit_button_top_purple, container, false);
                        case BUTTON_BOTTOM:
                            return inflater.inflate(R.layout.fragment_score_unit_button_bottom_purple, container, false);
                        case BUTTON_SIDE:
                            return inflater.inflate(R.layout.fragment_score_unit_button_side_purple, container, false);
                        case INTEGRATED_HORIZONTAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_horizontal_purple, container, false);
                        case INTEGRATED_VERTICAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_vertical_purple, container, false);
                        default:
                            Log.wtf(TAG, "Unknown style: " + scoreData.style);
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_purple, container, false);
                    }
                case CYAN:
                    switch (scoreData.style) {
                        case BUTTON_LEFT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_left_cyan, container, false);
                        case BUTTON_RIGHT:
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_cyan, container, false);
                        case BUTTON_TOP:
                            return inflater.inflate(R.layout.fragment_score_unit_button_top_cyan, container, false);
                        case BUTTON_BOTTOM:
                            return inflater.inflate(R.layout.fragment_score_unit_button_bottom_cyan, container, false);
                        case BUTTON_SIDE:
                            return inflater.inflate(R.layout.fragment_score_unit_button_side_cyan, container, false);
                        case INTEGRATED_HORIZONTAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_horizontal_cyan, container, false);
                        case INTEGRATED_VERTICAL:
                            return inflater.inflate(R.layout.fragment_score_unit_integrated_vertical_cyan, container, false);
                        default:
                            Log.wtf(TAG, "Unknown style: " + scoreData.style);
                            return inflater.inflate(R.layout.fragment_score_unit_button_right_cyan, container, false);
                    }
                default:
                    Log.wtf(TAG, "Unknown color: " + scoreData.color);
                    return inflater.inflate(R.layout.fragment_score_unit_button_right, container, false);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        labelBackground = view.findViewById(R.id.label_background);
        labelTextView = view.findViewById(R.id.score_unit_label_text_view);
        stepTextView = view.findViewById(R.id.score_unit_step_text_view);
        settingsButton = view.findViewById(R.id.score_unit_settings_button);
        scoreTextView = view.findViewById(R.id.score_text_view);
        incrementButton = view.findViewById(R.id.increment_button); // null if no button
        decrementButton = view.findViewById(R.id.decrement_button); // null if no button
        // listeners
        view.setOnDragListener(onDragListener);
        if (scoreData != null) {
            // start drag event
            labelBackground.setOnLongClickListener(v -> {
                final View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                v.startDragAndDrop(null, shadowBuilder, scoreData, 0);
                return true;
            });
            settingsButton.setOnClickListener(v -> listener.onScoreUnitSettingsPressed(scoreData));
            if (isNoButton) {
                view.setOnClickListener(v -> model.toggleSelectedScoreUnit(position));
                labelBackground.setOnClickListener(v -> model.toggleSelectedScoreUnit(position));
            } else {
                incrementButton.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        listener.onButtonTouch();
                    }
                    return false;
                });
                incrementButton.setOnClickListener(v -> listener.onIncrement(scoreData));
                incrementButton.setOnLongClickListener(v -> {
                    listener.onLongIncrement(scoreData);
                    return true;
                });
                decrementButton.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        listener.onButtonTouch();
                    }
                    return false;
                });
                decrementButton.setOnClickListener(v -> listener.onDecrement(scoreData));
                decrementButton.setOnLongClickListener(v -> {
                    listener.onLongDecrement(scoreData);
                    return true;
                });
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public ScoreData getScoreData() {
        return scoreData;
    }

    public interface OnInteractionListener {
        void onButtonTouch();
        void onIncrement(ScoreData scoreData);
        void onDecrement(ScoreData scoreData);
        void onLongIncrement(ScoreData scoreData);
        void onLongDecrement(ScoreData scoreData);
        void onScoreUnitSettingsPressed(ScoreData scoreData);
        void onScoreUnitSwap(ScoreData from, ScoreData to);
    }
}
