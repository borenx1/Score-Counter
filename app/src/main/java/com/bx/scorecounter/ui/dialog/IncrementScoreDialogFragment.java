package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;
import com.bx.scorecounter.db.ScoreData;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class IncrementScoreDialogFragment extends DialogFragment {

    private static final String TAG = "IncrementScoreDialogFragment";
    private static final String ARG_POSITION = "position";
    private static final String ARG_STEP = "increment";
    private static final String ARG_LABEL = "label";

    private DialogListener listener;

    private NumberPicker numberPicker;

    /**
     * @param scoreData ScoreData to increment.
     * @return Dialog instance.
     */
    @NonNull
    public static IncrementScoreDialogFragment newInstance(@NonNull ScoreData scoreData) {
        final IncrementScoreDialogFragment fragment = new IncrementScoreDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION, scoreData.position);
        args.putLong(ARG_STEP, scoreData.step);
        args.putString(ARG_LABEL, scoreData.label);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        numberPicker = new NumberPicker(getContext());
        final long step = Math.abs(getStep());
        numberPicker.setMinValue((int) Math.max(1, step/10));
        numberPicker.setMaxValue((int) step * 50);
        numberPicker.setValue((int) step);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(numberPicker)
                .setTitle(getString(R.string.dialog_increment_title, getLabel()))
                .setPositiveButton(R.string.yes, (dialog, which) -> listener.onDialogPositiveClick(IncrementScoreDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(IncrementScoreDialogFragment.this));
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public int getNumber() {
        return numberPicker.getValue();
    }

    public int getPosition() {
        if (getArguments() == null) {
            Log.wtf(TAG, "Position not set");
            return 0;
        } else {
            return getArguments().getInt(ARG_POSITION);
        }
    }

    private long getStep() {
        if (getArguments() == null) {
            Log.wtf(TAG, "Increment not set");
            return 0;
        } else {
            return getArguments().getLong(ARG_STEP);
        }
    }

    private String getLabel() {
        if (getArguments() == null) {
            Log.wtf(TAG, "Label not set");
            return null;
        } else {
            return getArguments().getString(ARG_LABEL);
        }
    }
}
