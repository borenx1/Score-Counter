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

public class TeamStyleAdapter extends ArrayAdapter<ScoreData.Style> {

    private final ScoreData.Style[] styles;

    public TeamStyleAdapter(@NonNull Context context, ScoreData.Style[] styles) {
        super(context, 0, styles);
        this.styles = styles;
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

        final ScoreData.Style style = styles[position];
        if (style == ScoreData.Style.BUTTON_RIGHT) {
            itemView.setText(R.string.score_unit_style_button_right);
        } else if (style == ScoreData.Style.BUTTON_LEFT) {
            itemView.setText(R.string.score_unit_style_button_left);
        } else if (style == ScoreData.Style.BUTTON_BOTTOM) {
            itemView.setText(R.string.score_unit_style_button_bottom);
        } else if (style == ScoreData.Style.BUTTON_TOP) {
            itemView.setText(R.string.score_unit_style_button_top);
        } else if (style == ScoreData.Style.BUTTON_SIDE) {
            itemView.setText(R.string.score_unit_style_button_side);
        } else if (style == ScoreData.Style.INTEGRATED_VERTICAL) {
            itemView.setText(R.string.score_unit_style_integrated_vertical);
        } else if (style == ScoreData.Style.INTEGRATED_HORIZONTAL) {
            itemView.setText(R.string.score_unit_style_integrated_horizontal);
        } else {
            throw new AssertionError(String.format("Style not recognized: %s", style));
        }

        return itemView;
    }
}
