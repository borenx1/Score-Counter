package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;
import com.bx.scorecounter.SessionViewModel;
import com.bx.scorecounter.db.Session;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SaveSessionDialogFragment extends DialogFragment implements SessionAdapter.OnItemClickListener {

    private static final String TAG = "SaveSessionDialogFragment";
    private static final String ARG_RESET_AFTER_SAVE = "save_session_dialog_fragment_reset_after_save";

    private DialogListener listener;
    private SessionViewModel model;

    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private EditText editText;

    @NonNull
    public static SaveSessionDialogFragment newInstance(boolean resetAfterSave) {
        final SaveSessionDialogFragment fragment = new SaveSessionDialogFragment();
        final Bundle args = new Bundle();
        args.putBoolean(ARG_RESET_AFTER_SAVE, resetAfterSave);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(createContent())
                .setTitle(R.string.save_session_dialog_title)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> listener.onDialogPositiveClick(SaveSessionDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(SaveSessionDialogFragment.this));
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
        model = ViewModelProviders.of(this).get(SessionViewModel.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onItemClick(int position, Session session) {
        editText.setText(session.name);
        editText.setSelection(editText.length());
        editText.requestFocus();
    }

    @Override
    public void onItemDelete(int position, Session session) {
        model.deleteSession(session);
    }

    @NonNull
    private View createContent() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_save_session, null);

        recyclerView = view.findViewById(R.id.save_recycler_view);
        adapter = new SessionAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.VERTICAL));

        editText = view.findViewById(R.id.save_edit_text);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (listener != null) {
                    listener.onDialogPositiveClick(SaveSessionDialogFragment.this);
                    dismiss();
                }
                return true;
            }
            return false;
        });
        model.getAllSessions().observe(this, sessions -> {
            final List<Session> sessionList = new ArrayList<>();
            for (final Session s: sessions) {
                if (!s.name.equals(Session.DEFAULT_SESSION_NAME)) {
                    sessionList.add(s);
                }
            }
            adapter.submitList(sessionList);
        });
        return view;
    }

    public boolean isResetAfterSave() {
        if (getArguments() == null) {
            Log.wtf(TAG, "isResetAfterSave not set");
            return false;
        }
        return getArguments().getBoolean(ARG_RESET_AFTER_SAVE);
    }

    /**
     * @return Text in the editText, trimmed.
     */
    @NonNull
    public String getUserInput() {
        return editText.getText().toString().trim();
    }
}
