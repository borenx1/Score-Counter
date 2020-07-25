package com.bx.scorecounter;

import com.bx.scorecounter.db.AppDatabase;
import com.bx.scorecounter.db.Change;
import com.bx.scorecounter.db.ScoreData;
import com.bx.scorecounter.db.ScoreboardDao;
import com.bx.scorecounter.db.Session;
import com.bx.scorecounter.db.SessionDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

public final class Repository {

    private static final String TAG = "repository";

    private static Repository instance;

    /**
     * Singleton instance for repository.
     *
     * @param application Application.
     * @return Repository singleton.
     */
    @NonNull
    public static synchronized Repository getInstance(@NonNull Application application) {
        if (instance == null) {
            instance = new Repository(application);
        }
        return instance;
    }

    private final ExecutorService executor;
    private final ScoreboardDao scoreboardDao;
    private final SessionDao sessionDao;

    private Repository(@NonNull Application application) {
        executor = Executors.newSingleThreadExecutor();
        final AppDatabase db = AppDatabase.getInstance(application);
        scoreboardDao = db.scoreboardDao();
        sessionDao = db.sessionDao();

//        executor.execute(() -> System.out.println(sessionDao.getAllSessions()));
//        executor.execute(() -> {
//            List<Change> changes = scoreboardDao.getChanges();
//            System.out.println(changes.size() + ": " + changes);
//        });
//        executor.execute(() -> {
//            List<ScoreData> scoreData = scoreboardDao.getScoreData();
//            System.out.println(scoreData.size() + ": " + scoreData);
//        });
    }

    @NonNull
    public LiveData<Session> getSession(String name) {
        return sessionDao.loadSession(name);
    }

    @NonNull
    public LiveData<Session[]> getAllSessionsSync() {
        return sessionDao.loadAllSessions();
    }

    @NonNull
    public List<Session> getAllSessions() {
        return sessionDao.getAllSessions();
    }

    public void deleteSession(@Nullable Session session) {
        if (session != null) {
            executor.execute(() -> sessionDao.delete(session));
        }
    }

    public void copySession(@NonNull String originalSession, @NonNull String newSession) {
        if (!originalSession.equals(newSession)) {
            executor.execute(new CopySessionRunnable(originalSession, newSession));
        } else {
            Log.w(TAG, "original and new session copy, same name: " + originalSession);
        }
    }

    /**
     * @param session Session name.
     * @return Number of {@link Change}s of the sessionName.
     */
    @NonNull
    public LiveData<Long> getChangesLength(String session) {
        return scoreboardDao.loadChangesLength(session);
    }

    @NonNull
    public LiveData<List<ScoreData>> getScoreDataSync(@NonNull String session) {
        return scoreboardDao.loadScoreData(session);
    }

    @NonNull
    public List<ScoreData> getScoreData(@NonNull String session) {
        return scoreboardDao.getScoreData(session);
    }

    @NonNull
    public LiveData<ScoreData> getScoreDataSync(@NonNull String session, int position) {
        return scoreboardDao.loadScoreData(session, position);
    }

    @NonNull
    public ScoreData getScoreData(@NonNull String session, int position) {
        return scoreboardDao.getScoreData(session, position);
    }

    /**
     * Inserts a default {@link ScoreData} into {@link Session}.
     *
     * @param session Name of {@link Session} to insert.
     * @param position Position of the {@link ScoreData}.
     */
    public void insertDefaultScoreData(@NonNull String session, int position) {
        executor.execute(() -> scoreboardDao.insertScoreDataIgnore(ScoreData.defaultScoreData(session, position)));
    }

    /**
     * Performs an action to change the scoreboard. It first deletes all {@link Change}s after the current change, then adds a Change.
     * The {@link ScoreData} is updated to reflect the change.
     *
     * @param session Name of the {@link Session} to edit.
     * @param changes A map of score position - {@link Change.Target} pairs to generate changes from.
     */
    public void addChanges(@NonNull String session, @NonNull Map<Integer, Change.Target> changes) {
        if (changes.size() > 0) {
            executor.execute(new AddScoreDataRunnable(session, changes));
        }
    }

    public void resetScoreDataScoreAndDeleteChanges(@NonNull String session) {
        executor.execute(() -> {
            scoreboardDao.deleteAllChanges(session);
            sessionDao.updateCurrentState(session, -1);
            scoreboardDao.updateScoreDataScore(session, 0);
        });
    }

    public void deleteAllScoreDataAndChanges(@NonNull String session) {
        executor.execute(() -> {
            scoreboardDao.deleteAllChanges(session);
            sessionDao.updateCurrentState(session, -1);
            scoreboardDao.deleteAllScoreData(session);
        });
    }

    public void undoSession(@NonNull String session) {
        executor.execute(new UndoRunnable(session));
    }

    public void redoSession(@NonNull String session) {
        executor.execute(new RedoRunnable(session));
    }

    private class AddScoreDataRunnable implements Runnable {

        final String sessionName;
        final Map<Integer, Change.Target> changes;

        /**
         * @param sessionName Name of session to add {@link ScoreData}.
         * @param changes A map of score position - {@link Change.Target} pairs.
         */
        AddScoreDataRunnable(@NonNull String sessionName, @NonNull Map<Integer, Change.Target> changes) {
            this.sessionName = sessionName;
            this.changes = changes;
        }

        @Override
        public void run() {
            if (changes.size() == 0) {
                return;
            }
            final Session session = sessionDao.getSession(sessionName);
            if (session == null) {
                return;
            }
            // initialize score data
            final List<ScoreData> oldScoreData = scoreboardDao.getScoreData(sessionName);
            final Map<Integer, ScoreData> scoreDataMap = new HashMap<>();
            for (final ScoreData sd: oldScoreData) {
                scoreDataMap.put(sd.position, sd);
            }
            final List<ScoreData> updated = new ArrayList<>();
            final List<Change> appliedChanges = new ArrayList<>();
            // generate and apply changes
            for (final Integer i: changes.keySet()) {
                final ScoreData originalScoreData = scoreDataMap.get(i);
                if (originalScoreData != null) {
                    final Change change = changes.get(i).getChange(session.currentState + 1, sessionName, originalScoreData);
                    if (change.applyChange(originalScoreData)) {
                        updated.add(originalScoreData);
                        appliedChanges.add(change);
                    }
                }
            }
            // update if there were changes to score data
            if (updated.size() > 0) {
                scoreboardDao.updateScoreData(updated);
                // increment before to stop redo button flicker
                sessionDao.incrementCurrentState(sessionName);
                scoreboardDao.deleteChangesGreaterThanPosition(sessionName, session.currentState);
                scoreboardDao.insertChange(appliedChanges);
            }
        }
    }

    private class UndoRunnable implements Runnable {

        final String sessionName;

        UndoRunnable(@NonNull String session) {
            this.sessionName = session;
        }

        @Override
        public void run() {
            final List<Change> changes = scoreboardDao.getCurrentChanges(sessionName);
            if (changes.size() == 0) {
                return;
            }
            final List<ScoreData> scoreData = scoreboardDao.getScoreData(sessionName);
            // init maps
            final Map<Integer, Change> changeMap = new HashMap<>();
            for (Change c: changes) {
                changeMap.put(c.scorePosition, c);
            }
            final Map<Integer, ScoreData> scoreDataMap = new HashMap<>();
            for (ScoreData sd: scoreData) {
                scoreDataMap.put(sd.position, sd);
            }
            for (int pos: changeMap.keySet()) {
                if (scoreDataMap.get(pos) != null) {
                    changeMap.get(pos).reverseChange(scoreDataMap.get(pos));
                }
            }
            scoreboardDao.updateScoreData(scoreDataMap.values().toArray(new ScoreData[] {}));
            sessionDao.decrementCurrentState(sessionName);
        }
    }

    private class RedoRunnable implements Runnable {

        final String sessionName;

        RedoRunnable(@NonNull String session) {
            this.sessionName = session;
        }

        @Override
        public void run() {
            final Session session = sessionDao.getSession(sessionName);
            if (session == null) {
                return;
            }
            final List<Change> changes = scoreboardDao.getChanges(sessionName, session.currentState + 1);
            if (changes.size() == 0) {
                return;
            }
            final List<ScoreData> scoreData = scoreboardDao.getScoreData(sessionName);
            // init maps
            final Map<Integer, Change> changeMap = new HashMap<>();
            for (Change c: changes) {
                changeMap.put(c.scorePosition, c);
            }
            final Map<Integer, ScoreData> scoreDataMap = new HashMap<>();
            for (ScoreData sd: scoreData) {
                scoreDataMap.put(sd.position, sd);
            }
            for (int pos: changeMap.keySet()) {
                if (scoreDataMap.get(pos) != null) {
                    changeMap.get(pos).applyChange(scoreDataMap.get(pos));
                }
            }
            scoreboardDao.updateScoreData(scoreDataMap.values().toArray(new ScoreData[] {}));
            sessionDao.incrementCurrentState(sessionName);
        }
    }

    private class CopySessionRunnable implements Runnable {

        final String originalSession;
        final String newSession;

        CopySessionRunnable(@NonNull String originalSession, @NonNull String newSession) {
            this.originalSession = originalSession;
            this.newSession = newSession;
        }

        @Override
        public void run() {
            if (originalSession.equals(newSession)) {
                return;
            }
            final Session original = sessionDao.getSession(originalSession);
            if (original == null) {
                return;
            }
            final List<ScoreData> scoreData = scoreboardDao.getScoreData(originalSession);
            final List<Change> changes = scoreboardDao.getChanges(originalSession);
            for (final ScoreData sd: scoreData) {
                sd.session = newSession;
            }
            for (final Change c: changes) {
                c.session = newSession;
            }
            sessionDao.delete(newSession);
            sessionDao.insert(new Session(newSession, original.currentState, System.currentTimeMillis()/1000));
            scoreboardDao.insert(scoreData, changes);
        }
    }
}
