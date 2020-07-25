package com.bx.scorecounter;

import com.bx.scorecounter.ui.dialog.DialogListener;
import com.bx.scorecounter.ui.dialog.ScoreboardResetDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class SettingsActivity extends AppCompatActivity implements DialogListener {

    private SettingsViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        model = ViewModelProviders.of(this).get(SettingsViewModel.class);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof ScoreboardResetDialogFragment) {
            model.deleteDefaultScoreDataAndChanges();
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    protected void vibrate(int milliseconds) {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, 50));
        }
    }
}
