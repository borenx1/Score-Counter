package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;
import com.bx.scorecounter.db.Session;
import com.google.android.material.button.MaterialButton;

import java.time.format.DateTimeFormatter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class SessionAdapter extends ListAdapter<Session, SessionAdapter.SessionViewHolder> {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DiffUtil.ItemCallback<Session> DIFF_CALLBACK = new DiffUtil.ItemCallback<Session>() {
        @Override
        public boolean areItemsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
            return oldItem.name.equals(newItem.name);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
            return oldItem.currentState == newItem.currentState
                    && oldItem.dateTime.equals(newItem.dateTime);
        }
    };
    /**
     * Listener for click item view.
     */
    private final OnItemClickListener listener;

    public SessionAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, parent, false);
        return new SessionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        final Session session = getItem(position);

        holder.sessionNameTextView.setText(session.name);
        holder.sessionDateTimeTextView.setText(session.dateTime.format(dateTimeFormatter));
    }

    public class SessionViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final TextView sessionNameTextView;
        private final TextView sessionDateTimeTextView;
        private final MaterialButton button;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionNameTextView = itemView.findViewById(R.id.session_name_text_view);
            sessionDateTimeTextView = itemView.findViewById(R.id.session_date_time_text_view);
            button = itemView.findViewById(R.id.button);
            button.setOnClickListener(v -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onItemClick(position, getItem(position));
                }
            });
            button.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(R.string.delete).setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    final int position = getAdapterPosition();
                    listener.onItemDelete(position, getItem(position));
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemClickListener {
        /**
         * Called when an item (viewHolder) in the {@link RecyclerView} is clicked.
         *
         * @param position The position of the item within the adapter's data set.
         * @param session The session that item is referring to.
         */
        void onItemClick(int position, Session session);

        /**
         * Called when the delete menu item in the context menu is clicked.
         *
         * @param position The position of the item within the adapter's data set.
         * @param session The session that item is referring to.
         */
        void onItemDelete(int position, Session session);
    }
}
