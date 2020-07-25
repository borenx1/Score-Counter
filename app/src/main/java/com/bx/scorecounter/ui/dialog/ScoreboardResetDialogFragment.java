package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Dialog shown in settings to confirm scoreboard reset
 */
public class ScoreboardResetDialogFragment extends DialogFragment {

    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.settings_scoreboard_reset_dialog_message)
                .setTitle(R.string.settings_scoreboard_reset_dialog_title)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> listener.onDialogPositiveClick(ScoreboardResetDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(ScoreboardResetDialogFragment.this));
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
}
