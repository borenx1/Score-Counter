package com.bx.scorecounter;

import com.bx.scorecounter.db.Session;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class SettingsViewModel extends AndroidViewModel {

    private final Repository repository;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public void deleteDefaultScoreDataAndChanges() {
        repository.deleteAllScoreDataAndChanges(Session.DEFAULT_SESSION_NAME);
    }
}
