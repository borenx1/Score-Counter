package com.bx.scorecounter;

import com.bx.scorecounter.db.Session;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SessionViewModel extends AndroidViewModel {

    private final Repository repository;

    public SessionViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    @NonNull
    public LiveData<Session[]> getAllSessions() {
        return repository.getAllSessionsSync();
    }

    public void deleteSession(@Nullable Session session) {
        if (session != null && !session.name.equals(Session.DEFAULT_SESSION_NAME)) {
            repository.deleteSession(session);
        }
    }

    public void loadSession(@NonNull String sessionName) {
        if (!sessionName.equals(Session.DEFAULT_SESSION_NAME)) {
            repository.copySession(sessionName, Session.DEFAULT_SESSION_NAME);
        }
    }
}
