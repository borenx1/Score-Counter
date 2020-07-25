package com.bx.scorecounter;

import com.bx.scorecounter.db.ScoreData;
import com.bx.scorecounter.db.Session;

import java.util.List;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class ScoreboardViewModel extends AndroidViewModel {

    private final Repository repository;
    private final MutableLiveData<Integer> selectedScoreUnit = new MutableLiveData<>();
    private final LiveData<ScoreData> selectedScoreData;

    public ScoreboardViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        selectedScoreUnit.setValue(-1);
        selectedScoreData = Transformations.switchMap(selectedScoreUnit, this::getDefaultSessionScoreDataSync);
    }

    /**
     * @param position Position of selected score unit. Set to a negative number for no selected.
     */
    public void setSelectedScoreUnit(int position) {
        selectedScoreUnit.setValue(position);
    }

    public void toggleSelectedScoreUnit(int position) {
        final Integer currentSelected = selectedScoreUnit.getValue();
        if (currentSelected == null || currentSelected != position) {
            setSelectedScoreUnit(position);
        } else {
            setSelectedScoreUnit(-1);
        }
    }

    public LiveData<ScoreData> getSelectedScoreData() {
        return selectedScoreData;
    }

    @NonNull
    public List<ScoreData> getDefaultSessionScoreData() {
        return repository.getScoreData(Session.DEFAULT_SESSION_NAME);
    }

    @NonNull
    public LiveData<List<ScoreData>> getDefaultSessionScoreDataSync() {
        return repository.getScoreDataSync(Session.DEFAULT_SESSION_NAME);
    }

    @NonNull
    public LiveData<ScoreData> getDefaultSessionScoreDataSync(int position) {
        return repository.getScoreDataSync(Session.DEFAULT_SESSION_NAME, position);
    }

    public void insertDefaultScoreData(int position) {
        repository.insertDefaultScoreData(Session.DEFAULT_SESSION_NAME, position);
    }
}
