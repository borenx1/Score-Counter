package com.bx.scorecounter.db;

import java.util.Objects;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"session", "position", "score_position"},
        foreignKeys = @ForeignKey(entity = Session.class, parentColumns = "name", childColumns = "session",
                onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE))
public class Change {

    private static final String TAG = "change";

    /**
     * From old to new.
     */
    public long position;
    /**
     * Session that this state belongs to.
     */
    @NonNull
    public String session;
    @ColumnInfo(name = "score_position")
    public int scorePosition;
    /**
     * Amount incremented, minus to reverse. Set to 0 for no change.
     */
    @ColumnInfo(name = "score_change")
    public long scoreChange;
    @ColumnInfo(name = "step_change")
    public long stepChange;
    /**
     * Set to null to ignore label changes.
     */
    @ColumnInfo(name = "old_label")
    public String oldLabel;
    /**
     * Set to null to ignore label changes.
     */
    @ColumnInfo(name = "new_label")
    public String newLabel;
    @ColumnInfo(name = "style_change")
    public int styleChange;
    @ColumnInfo(name = "color_change")
    public int colorChange;

    public Change(long position, @NonNull String session, int scorePosition) {
        this.position = position;
        this.session = session;
        this.scorePosition = scorePosition;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Change[");
        builder.append("scorePosition=").append(position);
        builder.append(", session=").append(session);
        builder.append(", scorePosition=").append(scorePosition);
        if (scoreChange != 0) {
            builder.append(", scoreChange=").append(scoreChange);
        }
        if (stepChange != 0) {
            builder.append(", stepChange=").append(stepChange);
        }
        if (!Objects.equals(oldLabel, newLabel)) {
            builder.append(", oldLabel=").append(oldLabel).append(", newLabel=").append(newLabel);
        }
        if (styleChange != 0) {
            builder.append(", styleChange=").append(styleChange);
        }
        if (colorChange != 0) {
            builder.append(", colorChange=").append(colorChange);
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * @param scoreData {@link ScoreData} to apply changes, the instance has its values changed.
     * @return {@code true} if changed, or false if nothing changed.
     */
    public boolean applyChange(ScoreData scoreData) {
        boolean changed = false;
        if (scoreChange != 0) {
            scoreData.score += scoreChange;
            changed = true;
        }
        if (stepChange != 0) {
            scoreData.step += stepChange;
            changed = true;
        }
        if (newLabel != null && !Objects.equals(scoreData.label, newLabel)) {
            scoreData.label = newLabel;
            changed = true;
        }
        if (styleChange != 0) {
            try {
                scoreData.style = ScoreData.Style.values()[scoreData.style.ordinal() + styleChange];
                changed = true;
            } catch (IndexOutOfBoundsException e) {
                Log.wtf(TAG, "Invalid style change: " + scoreData.style + " + " + styleChange);
            }
        }
        if (colorChange != 0) {
            try {
                scoreData.color = ScoreData.Color.values()[scoreData.color.ordinal() + colorChange];
                changed = true;
            } catch (IndexOutOfBoundsException e) {
                Log.wtf(TAG, "Invalid color change: " + scoreData.color + " + " + colorChange);
            }
        }
        return changed;
    }

    public boolean reverseChange(ScoreData scoreData) {
        boolean changed = false;
        if (scoreChange != 0) {
            scoreData.score -= scoreChange;
            changed = true;
        }
        if (stepChange != 0) {
            scoreData.step -= stepChange;
            changed = true;
        }
        if (oldLabel != null && !Objects.equals(scoreData.label, oldLabel)) {
            scoreData.label = oldLabel;
            changed = true;
        }
        if (styleChange != 0) {
            try {
                scoreData.style = ScoreData.Style.values()[scoreData.style.ordinal() - styleChange];
                changed = true;
            } catch (IndexOutOfBoundsException e) {
                Log.wtf(TAG, "Invalid style change: " + scoreData.style + " - " + styleChange);
            }
        }
        if (colorChange != 0) {
            try {
                scoreData.color = ScoreData.Color.values()[scoreData.color.ordinal() - colorChange];
                changed = true;
            } catch (IndexOutOfBoundsException e) {
                Log.wtf(TAG, "Invalid color change: " + scoreData.color + " - " + colorChange);
            }
        }
        return changed;
    }

    /**
     * Class representing a target {@link ScoreData} that a {@link ScoreData} wants to change into. Use this class to generate a
     * {@link Change} instance that represents a change in values from one {@link ScoreData} to another.
     */
    public static class Target {

        private long increment;
        private long step;
        private String label;
        private ScoreData.Style style;
        private ScoreData.Color color;

        /**
         * Constructor for a {@link Target}.
         *
         * @param increment Change in scores. Set to 0 for no change.
         * @param step Target step value. Set to 0 or a negative number for no change.
         * @param label Target label value. Set to {@code null} for no change.
         * @param style Target style value. Set to {@code null} for no change.
         * @param color Target color value. Set to {@code null} for no change.
         */
        public Target(long increment, long step, @Nullable String label, @Nullable ScoreData.Style style, @Nullable ScoreData.Color color) {
            this.increment = increment;
            this.step = step;
            this.label = label;
            this.style = style;
            this.color = color;
        }

        public Target(long increment) {
            this(increment, 0, null, null, null);
        }

        public Target(long increment, @NonNull ScoreData scoreData) {
            this(increment, scoreData.step, scoreData.label, scoreData.style, scoreData.color);
        }

        public long getIncrement() {
            return increment;
        }

        public long getStep() {
                return step;
        }

        public String getLabel() {
            return label;
        }

        public ScoreData.Style getStyle() {
            return style;
        }

        public ScoreData.Color getColor() {
            return color;
        }

        public void setIncrement(long increment) {
            this.increment = increment;
        }

        public void setStep(long step) {
            this.step = step;
        }

        public void setLabel(@Nullable String label) {
            this.label = label;
        }

        public void setStyle(@Nullable ScoreData.Style style) {
            this.style = style;
        }

        public void setColor(@Nullable ScoreData.Color color) {
            this.color = color;
        }

        @NonNull
        public Change getChange(long changePosition, @NonNull String session, @NonNull ScoreData original) {
            final Change change = new Change(changePosition, session, original.position);
            change.scoreChange = increment;
            if (step <= 0) {
                change.stepChange = 0;
            } else {
                change.stepChange = step - original.step;
            }
            if (label == null || label.equals(original.label)) {
                change.oldLabel = null;
                change.newLabel = null;
            } else {
                change.oldLabel = original.label;
                change.newLabel = label;
            }
            if (style == null) {
                change.styleChange = 0;
            } else {
                change.styleChange = style.ordinal() - original.style.ordinal();
            }
            if (color == null) {
                change.colorChange = 0;
            } else {
                change.colorChange = color.ordinal() - original.color.ordinal();
            }
            return change;
        }
    }
}
