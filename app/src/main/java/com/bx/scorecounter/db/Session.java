package com.bx.scorecounter.db;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Session {

    public static final String DEFAULT_SESSION_NAME = "default_current_session_name420";

    @NonNull
    @PrimaryKey
    public String name;
    /**
     * Position in the change stack, for undo/redo. -1 for no changes
     */
    @ColumnInfo(name = "current_state")
    public long currentState;
    @ColumnInfo(name = "date_time")
    public ZonedDateTime dateTime;

    public Session(@NonNull String name, long currentState, ZonedDateTime dateTime) {
        this.name = name;
        this.currentState = currentState;
        this.dateTime = dateTime;
    }

    public Session(@NonNull String name, long currentState, long epochSecond) {
        this(name, currentState, ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault()));
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Session[name=%s, dateTime=%s, currentState=%s]", name, dateTime, currentState);
    }
}
