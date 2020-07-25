package com.bx.scorecounter;

import com.bx.scorecounter.ui.dialog.ScoreboardResetDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;


/**
 * Fragment only attached to {@link SettingsActivity}.
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.settings, s);

        // manually add preferences
        Context context = getPreferenceManager().getContext();
        PreferenceCategory vibrateCategory = (PreferenceCategory) findPreference("vibrate_category");

        SeekBarPreference vibrateDuration = new SeekBarPreference(context);
        vibrateDuration.setKey("vibrate_duration");
        vibrateDuration.setTitle(R.string.settings_vibrate_duration);
        vibrateDuration.setMin(20);
        vibrateDuration.setMax(100);
        vibrateDuration.setSeekBarIncrement(10);
        vibrateDuration.setDefaultValue(50);

        vibrateCategory.addPreference(vibrateDuration);

        findPreference("vibrate_duration").setOnPreferenceChangeListener(this);

        findPreference("scoreboard_reset").setOnPreferenceClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findPreference("vibrate_duration").setDependency("vibrate_master");
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "vibrate_duration":
                ((SettingsActivity) getActivity()).vibrate((int) newValue);
                break;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "scoreboard_reset":
                new ScoreboardResetDialogFragment().show(getFragmentManager(), null);
                return true;
            default:
                return false;
        }
    }
}
