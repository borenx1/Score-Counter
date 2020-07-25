package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ResetScoresDialogFragment extends DialogFragment {

    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.reset_confirm_dialog_message)
                .setTitle(R.string.reset_confirm_dialog_title)
                .setPositiveButton(R.string.yes, (dialog, which) -> listener.onDialogPositiveClick(ResetScoresDialogFragment.this))
                .setNeutralButton(R.string.no, (dialog, which) -> listener.onDialogNeutralClick(ResetScoresDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(ResetScoresDialogFragment.this));
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
