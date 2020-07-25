package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;
import com.bx.scorecounter.SessionViewModel;
import com.bx.scorecounter.db.Session;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LoadSessionDialogFragment extends DialogFragment implements SessionAdapter.OnItemClickListener {

    private static final String TAG = "SaveSessionDialogFragment";

    private DialogListener listener;
    private SessionViewModel model;

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private SessionAdapter adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_load_session, null);
        recyclerView = view.findViewById(R.id.load_recycler_view);
        emptyTextView = view.findViewById(R.id.load_empty_text_view);

        adapter = new SessionAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.VERTICAL));

        model.getAllSessions().observe(this, sessions -> {
            final List<Session> sessionList = new ArrayList<>();
            for (final Session s: sessions) {
                if (!s.name.equals(Session.DEFAULT_SESSION_NAME)) {
                    sessionList.add(s);
                }
            }
            adapter.submitList(sessionList);
            if (sessionList.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                emptyTextView.setVisibility(View.GONE);
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle(R.string.load_session_dialog_title)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(LoadSessionDialogFragment.this));
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
        model.loadSession(session.name);
        dismiss();
    }

    @Override
    public void onItemDelete(int position, Session session) {
        model.deleteSession(session);
    }
}
