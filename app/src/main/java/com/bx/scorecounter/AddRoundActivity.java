package com.bx.scorecounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bx.scorecounter.db.Change;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class AddRoundActivity extends AppCompatActivity {

    private static final String TAG = "AddRoundActivity";

    private int scoreboardNumber;

    private AddRoundViewModel model;
    private MediaPlayer buttonSoundPlayer;

    private LinearLayout container;
    private List<AddRoundIncrementFragment> incrementFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_round);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        scoreboardNumber = Integer.valueOf(preferences.getString("scoreboard_number", "2"));

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        model = ViewModelProviders.of(this).get(AddRoundViewModel.class);
        buttonSoundPlayer = MediaPlayer.create(this, R.raw.button_click);

        container = findViewById(R.id.add_round_container);

        incrementFragments.clear();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        for (int i = 0; i < scoreboardNumber; i++) {
            final FrameLayout incrementContainer = new FrameLayout(this);
            incrementContainer.setId(i + 1);
            container.addView(incrementContainer, lp);
            final AddRoundIncrementFragment fragment = AddRoundIncrementFragment.newInstance(i);
            transaction.replace(i + 1, fragment);
            incrementFragments.add(fragment);
        }
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_round, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_round:
                final List<Change.Target> changes = new ArrayList<>();
                for (final AddRoundIncrementFragment f: incrementFragments) {
                    changes.add(new Change.Target(f.getIncrement()));
                }
                model.addChanges(changes);
                finish();
                return true;
            default:
                Log.wtf(TAG, "Unknown menu id: " + item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }

    void buttonResponseConditional() {
        final SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
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
