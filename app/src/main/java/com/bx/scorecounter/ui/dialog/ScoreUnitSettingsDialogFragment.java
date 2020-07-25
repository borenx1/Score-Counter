package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;
import com.bx.scorecounter.ScoreboardViewModel;
import com.bx.scorecounter.db.ScoreData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * Called by scoreboard to set settings. Position is set by {@link #ARG_POSITION} argument.
 */
public class ScoreUnitSettingsDialogFragment extends DialogFragment {

    private static final String TAG = "ScoreUnitSettingsDialogFragment";
    private static final String ARG_POSITION = "score_unit_dialog_fragment_position";

    private DialogListener listener;
    private ScoreboardViewModel model;

    // views of the dialog
    private EditText labelEditText;
    private Spinner colorSpinner;
    private EditText stepEditText;
    private CheckBox stepCheckBox;
    private Spinner styleSpinner;
    private CheckBox styleCheckBox;

    @NonNull
    public static ScoreUnitSettingsDialogFragment newInstance(int position) {
        final ScoreUnitSettingsDialogFragment fragment = new ScoreUnitSettingsDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(createContent())
                .setTitle(R.string.score_unit_settings_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, which) -> listener.onDialogPositiveClick(ScoreUnitSettingsDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(ScoreUnitSettingsDialogFragment.this));
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement DialogListener");
        }
        model = ViewModelProviders.of(this).get(ScoreboardViewModel.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    private View createContent() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_score_unit_settings, null);
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            final List<ScoreData> allScoreData = executor.submit(() -> model.getDefaultSessionScoreData()).get();
            final ScoreData scoreData = getPosition() < allScoreData.size() ? allScoreData.get(getPosition()) : null;
            if (scoreData != null) {
                labelEditText = view.findViewById(R.id.label_edit_text);
                labelEditText.setText(scoreData.label);

                colorSpinner = view.findViewById(R.id.color_spinner);
                colorSpinner.setAdapter(new TeamColorAdapter(getContext(), ScoreData.Color.values()));
                // enum color id depends on order declared
                colorSpinner.setSelection(scoreData.color.ordinal());

                stepEditText = view.findViewById(R.id.step_edit_text);
                stepEditText.setText(String.valueOf(scoreData.step));

                styleSpinner = view.findViewById(R.id.style_spinner);
                styleSpinner.setAdapter(new TeamStyleAdapter(getContext(), ScoreData.Style.values()));
                styleSpinner.setSelection(scoreData.style.ordinal());

                // if all settings the same, check checkbox
                stepCheckBox = view.findViewById(R.id.step_check_box);
                stepCheckBox.setChecked(isAllTeamsStepSame(allScoreData));
                styleCheckBox = view.findViewById(R.id.style_check_box);
                styleCheckBox.setChecked(isAllTeamsStyleSame(allScoreData));
            }
        } catch (Exception e) {
            Log.wtf(TAG, e);
        } finally {
            executor.shutdown();
        }
        return view;
    }

    /**
     * @return true if all teams have the same step.
     */
    private static boolean isAllTeamsStepSame(@NonNull List<ScoreData> scoreData) {
        if (scoreData.size() > 0) {
            final long firstStep = scoreData.get(0).step;
            for (int i = 1; i < scoreData.size(); i++) {
                if (scoreData.get(i).step != firstStep) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return true if all teams  have the same style
     */
    private static boolean isAllTeamsStyleSame(@NonNull List<ScoreData> scoreData) {
        if (scoreData.size() > 0) {
            final ScoreData.Style firstStyle = scoreData.get(0).style;
            for (int i = 1; i < scoreData.size(); i++) {
                if (scoreData.get(i).style != firstStyle) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getPosition() {
        if (getArguments() == null) {
            Log.wtf(TAG, "Position not set");
            return 0;
        } else {
            return getArguments().getInt(ARG_POSITION);
        }
    }

    @NonNull
    public String getUserLabel() {
        return labelEditText.getText().toString().trim();
    }

    @NonNull
    public ScoreData.Color getUserColor() {
        return (ScoreData.Color) colorSpinner.getSelectedItem();
    }

    public long getUserStep() throws NumberFormatException {
        final String step = stepEditText.getText().toString().trim();
        return Long.parseLong(step);
    }

    @NonNull
    public ScoreData.Style getUserStyle() {
        return (ScoreData.Style) styleSpinner.getSelectedItem();
    }

    public boolean isStepChecked() {
        return stepCheckBox.isChecked();
    }

    public boolean isStyleChecked() {
        return styleCheckBox.isChecked();
    }
}
