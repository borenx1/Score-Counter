package com.bx.scorecounter.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "score_data", primaryKeys = {"session", "position"},
        foreignKeys = @ForeignKey(entity = Session.class, parentColumns = "name", childColumns = "session",
                onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class ScoreData {

    public enum Style {
        BUTTON_SIDE, BUTTON_RIGHT, BUTTON_LEFT, BUTTON_BOTTOM, BUTTON_TOP, INTEGRATED_VERTICAL, INTEGRATED_HORIZONTAL
    }

    public enum Color {
        LIGHT, DARK, RED, GREEN, BLUE, YELLOW, PURPLE, CYAN
    }

    @NonNull
    public String session;
    /**
     * Position in a scoreboard, 0-n.
     */
    public int position;
    public long score;
    public long step;
    public String label;
    public Style style;
    public Color color;

    public ScoreData(@NonNull String session, int position, long score, long step, String label, Style style, Color color) {
        this.session = session;
        this.position = position;
        this.score = score;
        this.step = step;
        this.label = label;
        this.style = style;
        this.color = color;
    }

    @NonNull
    @Override
    public String toString() {
        return "ScoreData{" +
                "session=" + session +
                ", position=" + position +
                ", score=" + score +
                ", step=" + step +
                ", label='" + label + '\'' +
                ", style=" + style +
                ", color=" + color +
                '}';
    }

    public static ScoreData defaultScoreData(@NonNull String session, int position) {
        return new ScoreData(session, position, 0, 1, "Player " + (position + 1),
                Style.BUTTON_SIDE, Color.LIGHT);
    }
}