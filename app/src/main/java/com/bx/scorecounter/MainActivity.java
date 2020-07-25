package com.bx.scorecounter;

import com.bx.scorecounter.db.Change;
import com.bx.scorecounter.db.ScoreData;
import com.bx.scorecounter.db.Session;
import com.bx.scorecounter.ui.dialog.DecrementScoreDialogFragment;
import com.bx.scorecounter.ui.dialog.DialogListener;
import com.bx.scorecounter.ui.dialog.IncrementScoreDialogFragment;
import com.bx.scorecounter.ui.dialog.ResetScoresDialogFragment;
import com.bx.scorecounter.ui.dialog.LoadSessionDialogFragment;
import com.bx.scorecounter.ui.dialog.OverwriteSaveDialogFragment;
import com.bx.scorecounter.ui.dialog.SaveSessionDialogFragment;
import com.bx.scorecounter.ui.dialog.ScoreUnitSettingsDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity implements DialogListener, ScoreUnitFragment.OnInteractionListener {

    private static final String TAG = "MainActivity";

    private MainViewModel model;
    private MediaPlayer buttonSoundPlayer;

    /**
     * The enabled states of the undo/redo actions in the action bar.
     */
    private boolean undoEnabled = false, redoEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = ViewModelProviders.of(this).get(MainViewModel.class);
        buttonSoundPlayer = MediaPlayer.create(this, R.raw.button_click);

        model.canUndo().observe(this, canUndo -> {
            undoEnabled = canUndo;
            invalidateOptionsMenu();
        });
        model.canRedo().observe(this, canRedo -> {
            redoEnabled = canRedo;
            invalidateOptionsMenu();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_undo).setEnabled(undoEnabled);
        menu.findItem(R.id.menu_redo).setEnabled(redoEnabled);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_redo:
                model.redoDefaultSession();
                return true;
            case R.id.menu_undo:
                model.undoDefaultSession();
                return true;
            case R.id.menu_add_round:
                startActivity(new Intent(this, AddRoundActivity.class));
                return true;
            case R.id.menu_reset:
                if (undoEnabled || redoEnabled) {
                    new ResetScoresDialogFragment().show(getSupportFragmentManager(), null);
                } else {
                    model.resetScores();
                }
                return true;
            case R.id.menu_save:
                SaveSessionDialogFragment.newInstance(false).show(getSupportFragmentManager(), null);
                return true;
            case R.id.menu_load:
                new LoadSessionDialogFragment().show(getSupportFragmentManager(), null);
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                Log.wtf(TAG, "Unknown menu id: " + item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("other_keep_screen_on", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onBackPressed() {
        final ScoreboardFragment scoreboardFragment = (ScoreboardFragment) getSupportFragmentManager().findFragmentById(R.id.scoreboard_fragment);
        if (scoreboardFragment == null || !scoreboardFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onButtonTouch() {
        buttonResponseConditional();
    }

    @Override
    public void onIncrement(ScoreData scoreData) {
        if (scoreData != null) {
            model.addChange(scoreData.position, new Change.Target(scoreData.step));
        }
    }

    @Override
    public void onDecrement(ScoreData scoreData) {
        if (scoreData != null) {
            model.addChange(scoreData.position, new Change.Target(-scoreData.step));
        }
    }

    @Override
    public void onLongIncrement(ScoreData scoreData) {
        if (scoreData != null) {
            IncrementScoreDialogFragment.newInstance(scoreData).show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onLongDecrement(ScoreData scoreData) {
        if (scoreData != null) {
            DecrementScoreDialogFragment.newInstance(scoreData).show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onScoreUnitSettingsPressed(ScoreData scoreData) {
        if (scoreData != null) {
            ScoreUnitSettingsDialogFragment.newInstance(scoreData.position).show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onScoreUnitSwap(ScoreData from, ScoreData to) {
        if (from != null && to != null && from.position != to.position && from.session.equals(to.session)) {
            final Map<Integer, Change.Target> changes = new HashMap<>();
            changes.put(from.position, new Change.Target(to.score - from.score, to));
            changes.put(to.position, new Change.Target(from.score - to.score, from));
            model.addChanges(changes);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof SaveSessionDialogFragment) {
            Log.i(TAG, "Save dialog");
            // menu save
            final String input = ((SaveSessionDialogFragment) dialog).getUserInput();
            if (input.isEmpty()) {
                Snackbar.make(findViewById(R.id.coordinator_layout), R.string.save_session_dialog_error_empty_name, Snackbar.LENGTH_LONG).show();
                return;
            }
            if (input.equals(Session.DEFAULT_SESSION_NAME)) {
                Snackbar.make(findViewById(R.id.coordinator_layout), R.string.save_session_dialog_error_invalid_name, Snackbar.LENGTH_LONG).show();
                return;
            }
            // true if the save dialog is called from reset score menu
            final boolean resetAfterSave = ((SaveSessionDialogFragment) dialog).isResetAfterSave();
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                final List<Session> sessions = executor.submit(() -> model.getAllSessions()).get();
                for (final Session s: sessions) {
                    if (input.equals(s.name)) {
                        OverwriteSaveDialogFragment.newInstance(input, resetAfterSave).show(getSupportFragmentManager(), null);
                        return;
                    }
                }
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinator_layout), R.string.save_session_dialog_error, Snackbar.LENGTH_LONG).show();
                return;
            } finally {
                executor.shutdown();
            }
            model.saveSession(input);
            if (resetAfterSave) {
                model.resetScores();
            }
        } else if (dialog instanceof OverwriteSaveDialogFragment) {
            Log.i(TAG, "Overwrite save dialog");
            final String sessionName = ((OverwriteSaveDialogFragment) dialog).getSessionName();
            boolean resetAfterSave = ((OverwriteSaveDialogFragment) dialog).isResetAfterSave();
            model.saveSession(sessionName);
            if (resetAfterSave) {
                model.resetScores();
            }
        } else if (dialog instanceof LoadSessionDialogFragment) {
            Log.i(TAG, "Load dialog");
            // menu load
        } else if (dialog instanceof ResetScoresDialogFragment) {
            // menu reset scores - YES
            SaveSessionDialogFragment.newInstance(true).show(getSupportFragmentManager(), null);
        } else if (dialog instanceof ScoreUnitSettingsDialogFragment) {
            Log.i(TAG, "ScoreUnit settings dialog");
            final ScoreUnitSettingsDialogFragment d = (ScoreUnitSettingsDialogFragment) dialog;
            final int position = d.getPosition();
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                final List<ScoreData> scoreData = executor.submit(() -> model.getDefaultSessionScoreData()).get();
                final Map<Integer, Change.Target> changes = new HashMap<>();
                for (final ScoreData sd: scoreData) {
                    changes.put(sd.position, new Change.Target(0, 0, null, null, null));
                }
                changes.get(position).setLabel(d.getUserLabel());
                changes.get(position).setColor(d.getUserColor());
                try {
                    final long step = d.getUserStep();
                    if (step <= 0) {
                        throw new NumberFormatException();
                    } else {
                        if (d.isStepChecked()) {
                            for (final Integer i: changes.keySet()) {
                                changes.get(i).setStep(step);
                            }
                        } else {
                            changes.get(position).setStep(step);
                        }
                    }
                } catch (NumberFormatException e) {
                    Snackbar.make(findViewById(R.id.coordinator_layout), R.string.score_unit_settings_dialog_error_step, Snackbar.LENGTH_LONG).show();
                }
                if (d.isStyleChecked()) {
                    for (final Integer i: changes.keySet()) {
                        changes.get(i).setStyle(d.getUserStyle());
                    }
                } else {
                    changes.get(position).setStyle(d.getUserStyle());
                }
                model.addChanges(changes);
            } catch (Exception e) {
                Log.wtf(TAG, "ScoreUnitSettingsDialogFragment problem getting scoreData");
            } finally {
                executor.shutdown();
            }
        } else if (dialog instanceof IncrementScoreDialogFragment) {
            final IncrementScoreDialogFragment d = (IncrementScoreDialogFragment) dialog;
            model.addChange(d.getPosition(), new Change.Target(d.getNumber()));
        } else if (dialog instanceof DecrementScoreDialogFragment) {
            final DecrementScoreDialogFragment d = (DecrementScoreDialogFragment) dialog;
            model.addChange(d.getPosition(), new Change.Target(-d.getNumber()));
        }
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        if (dialog instanceof ResetScoresDialogFragment) {
            // "No" in confirm reset
            model.resetScores();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    /**
     * Play sound or vibrate if the settings allow it.
     */
    private void buttonResponseConditional() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("sound_master", true)) {
            buttonSoundPlayer.start();
        }
        if (preferences.getBoolean("vibrate_master", false)) {
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                final int duration = preferences.getInt("vibrate_duration", 50);
                vibrator.vibrate(VibrationEffect.createOneShot(duration, 50));
            }
        }
    }
}
