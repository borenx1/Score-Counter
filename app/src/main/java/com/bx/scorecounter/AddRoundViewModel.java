package com.bx.scorecounter;

import android.app.Application;
import android.util.SparseArray;

import com.bx.scorecounter.db.Change;
import com.bx.scorecounter.db.ScoreData;
import com.bx.scorecounter.db.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class AddRoundViewModel extends AndroidViewModel {

    private final Repository repository;

    public AddRoundViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public void addChanges(@NonNull Map<Integer, Change.Target> changes) {
        repository.addChanges(Session.DEFAULT_SESSION_NAME, changes);
    }

    /**
     * @param changes List of {@link Change.Target}s in order of position.
     */
    public void addChanges(@NonNull List<Change.Target> changes) {
        final Map<Integer, Change.Target> changesMap = new HashMap<>();
        for (int i = 0; i < changes.size(); i++) {
            changesMap.put(i, changes.get(i));
        }
        addChanges(changesMap);
    }

    @NonNull
    public LiveData<ScoreData> getDefaultSessionScoreDataSync(int position) {
        return repository.getScoreDataSync(Session.DEFAULT_SESSION_NAME, position);
    }
}
