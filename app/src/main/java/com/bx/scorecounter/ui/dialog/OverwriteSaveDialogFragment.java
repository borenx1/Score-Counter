package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class OverwriteSaveDialogFragment extends DialogFragment {

    private static final String TAG = "OverwriteSaveDialogFragment";
    private static final String ARG_SESSION_NAME = "overwrite_save_dialog_fragment_session";
    private static final String ARG_RESET_AFTER_SAVE = "overwrite_save_dialog_fragment_reset_after_save";

    private DialogListener listener;

    @NonNull
    public static OverwriteSaveDialogFragment newInstance(@NonNull String session, boolean resetAfterSave) {
        final OverwriteSaveDialogFragment fragment = new OverwriteSaveDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_SESSION_NAME, session);
        args.putBoolean(ARG_RESET_AFTER_SAVE, resetAfterSave);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.save_session_overwrite_dialog_message, getArguments().getString(ARG_SESSION_NAME)))
                .setTitle(R.string.save_session_overwrite_dialog_title)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> listener.onDialogPositiveClick(OverwriteSaveDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(OverwriteSaveDialogFragment.this));
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

    @NonNull
    public String getSessionName() {
        if (getArguments() == null || getArguments().getString(ARG_SESSION_NAME) == null) {
            Log.wtf(TAG, "Session name not set");
            throw new AssertionError();
        }
        return getArguments().getString(ARG_SESSION_NAME);
    }

    public boolean isResetAfterSave() {
        if (getArguments() == null) {
            Log.wtf(TAG, "isResetAfterSave not set");
            return false;
        }
        return getArguments().getBoolean(ARG_RESET_AFTER_SAVE);
    }
}
