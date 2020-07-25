package com.bx.scorecounter;

import android.app.Application;

import com.bx.scorecounter.db.Change;
import com.bx.scorecounter.db.ScoreData;
import com.bx.scorecounter.db.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

public class MainViewModel extends AndroidViewModel {

    private final Repository repository;
    private final LiveData<Long> currentState;
    private final LiveData<Long> changesLength;
    private final LiveData<Boolean> canUndo;
    private final MediatorLiveData<Boolean> canRedo = new MediatorLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);

        currentState = Transformations.map(repository.getSession(Session.DEFAULT_SESSION_NAME), session -> session == null ? 0 : session.currentState);
        changesLength = repository.getChangesLength(Session.DEFAULT_SESSION_NAME);
        canUndo = Transformations.map(currentState, position -> position >= 0);
        canRedo.setValue(false);
        canRedo.addSource(currentState, position -> {
            if (changesLength.getValue() == null) {
                canRedo.setValue(false);
            } else {
                canRedo.setValue(position < changesLength.getValue() - 1);
            }
        });
        // currentState cannot be null
        canRedo.addSource(changesLength, length -> {
            if (currentState.getValue() == null) {
                canRedo.setValue(false);
            } else {
                canRedo.setValue(currentState.getValue() < length - 1);
            }
        });
    }

    @NonNull
    public LiveData<Boolean> canUndo() {
        return canUndo;
    }

    @NonNull
    public LiveData<Boolean> canRedo() {
        return canRedo;
    }

    public void addChange(int position, @NonNull Change.Target change) {
        final Map<Integer, Change.Target> changeMap = new HashMap<>();
        changeMap.put(position, change);
        addChanges(changeMap);
    }

    public void addChanges(@NonNull Map<Integer, Change.Target> changes) {
        repository.addChanges(Session.DEFAULT_SESSION_NAME, changes);
    }

    public void undoDefaultSession() {
        repository.undoSession(Session.DEFAULT_SESSION_NAME);
    }

    public void redoDefaultSession() {
        repository.redoSession(Session.DEFAULT_SESSION_NAME);
    }

    public void resetScores() {
        repository.resetScoreDataScoreAndDeleteChanges(Session.DEFAULT_SESSION_NAME);
    }

    @NonNull
    public List<Session> getAllSessions() {
        return repository.getAllSessions();
    }

    public void saveSession(@NonNull String newSessionName) {
        if (!newSessionName.equals(Session.DEFAULT_SESSION_NAME)) {
            repository.copySession(Session.DEFAULT_SESSION_NAME, newSessionName);
        }
    }

    @NonNull
    public List<ScoreData> getDefaultSessionScoreData() {
        return repository.getScoreData(Session.DEFAULT_SESSION_NAME);
    }
}