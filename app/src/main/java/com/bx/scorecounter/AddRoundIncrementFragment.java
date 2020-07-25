package com.bx.scorecounter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bx.scorecounter.db.ScoreData;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class AddRoundIncrementFragment extends Fragment {

    private static final String TAG = "AddRoundIncrementFragment";
    private static final String ARG_POSITION = "position";

    private int position;
    private ScoreData scoreData;

    private AddRoundActivity addRoundActivity;
    private AddRoundViewModel model;
    private TextView labelTextView;
    private EditText incrementEditText;
    private MaterialButton incrementButton;
    private MaterialButton decrementButton;

    private final Observer<ScoreData> scoreDataObserver = input -> {
        final ScoreData oldScoreData = scoreData;
        scoreData = input;
        if (scoreData != null) {
            if (oldScoreData == null || scoreData.color != oldScoreData.color) {
                // commit now to get views from loading layout
                getFragmentManager().beginTransaction().detach(this).attach(this).commitNow();
            }
            labelTextView.setText(scoreData.label + ": " + scoreData.score + " ± " + scoreData.step);
        }
    };

    public static AddRoundIncrementFragment newInstance(int position) {
        final AddRoundIncrementFragment fragment = new AddRoundIncrementFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        } else {
            Log.wtf(TAG, "No arguments");
        }
        addRoundActivity = (AddRoundActivity) getActivity();
        model = ViewModelProviders.of(this).get(AddRoundViewModel.class);
        model.getDefaultSessionScoreDataSync(position).observe(this, scoreDataObserver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (scoreData == null) {
            return inflater.inflate(R.layout.fragment_loading, container, false);
        }
        switch (scoreData.color) {
            case LIGHT:
                return inflater.inflate(R.layout.fragment_add_round_increment, container, false);
            case DARK:
                return inflater.inflate(R.layout.fragment_add_round_increment_dark, container, false);
            case RED:
                return inflater.inflate(R.layout.fragment_add_round_increment_red, container, false);
            case GREEN:
                return inflater.inflate(R.layout.fragment_add_round_increment_green, container, false);
            case BLUE:
                return inflater.inflate(R.layout.fragment_add_round_increment_blue, container, false);
            case YELLOW:
                return inflater.inflate(R.layout.fragment_add_round_increment_yellow, container, false);
            case PURPLE:
                return inflater.inflate(R.layout.fragment_add_round_increment_purple, container, false);
            case CYAN:
                return inflater.inflate(R.layout.fragment_add_round_increment_cyan, container, false);
            default:
                Log.wtf(TAG, "Unknown color: " + scoreData.color);
                return inflater.inflate(R.layout.fragment_add_round_increment, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        labelTextView = view.findViewById(R.id.label_text_view);
        incrementEditText = view.findViewById(R.id.increment_edit_text);
        incrementButton = view.findViewById(R.id.increment_button);
        decrementButton = view.findViewById(R.id.decrement_button);
        if (scoreData != null) {
            incrementEditText.setText("0");
            incrementButton.setOnClickListener(v -> {
                addRoundActivity.buttonResponseConditional();
                try {
                    incrementEditText.setText(String.valueOf(Long.parseLong(incrementEditText.getText().toString()) + scoreData.step));
                } catch (NumberFormatException e) {
                    incrementEditText.setText("0");
                }
                incrementEditText.setSelection(incrementEditText.length());
            });
            decrementButton.setOnClickListener(v -> {
                addRoundActivity.buttonResponseConditional();
                try {
                    incrementEditText.setText(String.valueOf(Long.parseLong(incrementEditText.getText().toString()) - scoreData.step));
                } catch (NumberFormatException e) {
                    incrementEditText.setText("0");
                }
                incrementEditText.setSelection(incrementEditText.length());
            });
        }
    }

    public int getPosition() {
        return position;
    }

    public long getIncrement() {
        if (incrementEditText == null) {
            return 0L;
        }
        try {
            return Long.parseLong(incrementEditText.getText().toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
