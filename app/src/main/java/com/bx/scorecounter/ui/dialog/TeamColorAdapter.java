package com.bx.scorecounter.ui.dialog;

import com.bx.scorecounter.R;
import com.bx.scorecounter.db.ScoreData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TeamColorAdapter extends ArrayAdapter<ScoreData.Color> {

    private final ScoreData.Color[] colors;

    public TeamColorAdapter(@NonNull Context context, ScoreData.Color[] colors) {
        super(context, 0, colors);
        this.colors = colors;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, true);
    }

    private View createViewFromResource(int position, @Nullable View convertView, @NonNull ViewGroup parent, boolean isDropDown) {
        final Context context = getContext();
        TextView itemView = (TextView) convertView;
        if (itemView == null) {
            if (isDropDown) {
                itemView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            } else {
                itemView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
            }
        }

        ScoreData.Color color = colors[position];
        if (color == ScoreData.Color.LIGHT) {
            itemView.setText(R.string.score_unit_color_light);
            itemView.setTextColor(context.getColor(R.color.onLabelLight));
            itemView.setBackgroundColor(context.getColor(R.color.backgroundLight));
        } else if (color == ScoreData.Color.DARK) {
            itemView.setText(R.string.score_unit_color_dark);
            itemView.setTextColor(context.getColor(R.color.onLabelDark));
            itemView.setBackgroundColor(context.getColor(R.color.backgroundDark));
        } else if (color == ScoreData.Color.RED) {
            itemView.setText(R.string.score_unit_color_red);
            itemView.setTextColor(context.getColor(R.color.onLabelRed));
            itemView.setBackgroundColor(context.getColor(R.color.backgroundRed));
        } else if (color == ScoreData.Color.GREEN) {
            itemView.setText(R.string.score_unit_color_green);
            itemView.setTextColor(context.getColor(R.color.onLabelGreen));
            itemView.setBackgroundColor(context.getColor(R.color.backgroundGreen));
        } else if (color == ScoreData.Color.BLUE) {
            itemView.setText(R.string.score_unit_color_blue);
            itemView.setTextColor(context.getColor(R.color.onLabelBlue));
            itemView.setBackgroundColor(context.getColor(R.color.backgroundBlue));
        } else if (color == ScoreData.Color.CYAN) {
            itemView.setText(R.string.score_unit_color_cyan);
            itemView.setTextColor(context.getColor(R.color.onLabelCyan));
            itemView.setBackgroundColor(context.getColor(R.color.backgroundCyan));
        } else if (color == ScoreData.Color.PURPLE) {
            itemView.setText(R.string.score_unit_color_purple);
            itemView.setTextColor(context.getColor(R.color.onLabelPurple));
            itemView.setBackgroundColor(context.getColor(R.color.backgroundPurple));
        } else if (color == ScoreData.Color.YELLOW) {
            itemView.setText(R.string.score_unit_color_yellow);
            itemView.setTextColor(context.getColor(R.color.onLabelYellow));
            itemView.setBackgroundColor(context.getColor(R.color.backgroundYellow));
        } else {
            throw new AssertionError(String.format("Color not recognized: %s", color));
        }
        return itemView;
    }
}
