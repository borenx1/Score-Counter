package com.bx.scorecounter.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ScoreboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<ScoreData> scoreData, List<Change> changes);

    @Insert
    void insertChange(Change... changes);

    @Insert
    void insertChange(List<Change> changes);

    /**
     * Ignores on conflict.
     *
     * @param scoreData {@link ScoreData} to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertScoreDataIgnore(ScoreData scoreData);

    @Update
    void updateScoreData(ScoreData... scoreData);

    @Update
    void updateScoreData(List<ScoreData> scoreData);

    /**
     * Update the score field in all the {@link ScoreData} in the session.
     *
     * @param session Session to update score.
     * @param score New score.
     */
    @Query("UPDATE score_data SET score = :score WHERE session = :session")
    void updateScoreDataScore(String session, long score);

    @Query("DELETE FROM change WHERE session = :session AND position > :position")
    void deleteChangesGreaterThanPosition(String session, long position);

    @Query("DELETE FROM score_data WHERE session = :session")
    void deleteAllScoreData(String session);

    @Query("DELETE FROM change WHERE session = :session")
    void deleteAllChanges(String session);

    @Query("SELECT * FROM score_data")
    List<ScoreData> getAllScoreData();

    @Query("SELECT * FROM change")
    List<Change> getAllChanges();

    @Query("SELECT * FROM score_data WHERE session = :session ORDER BY position ASC")
    LiveData<List<ScoreData>> loadScoreData(String session);

    @Query("SELECT * FROM score_data WHERE session = :session ORDER BY position ASC")
    List<ScoreData> getScoreData(String session);

    @Query("SELECT * FROM change WHERE session = :session")
    List<Change> getChanges(String session);

    @Query("SELECT * FROM score_data WHERE session = :session AND position = :position")
    LiveData<ScoreData> loadScoreData(String session, int position);

    @Query("SELECT * FROM score_data WHERE session = :session AND position = :position")
    ScoreData getScoreData(String session, int position);

    /**
     * @param session Session.
     * @return Number of unique positions.
     */
    @Query("SELECT COUNT(DISTINCT position) FROM change WHERE session = :session")
    LiveData<Long> loadChangesLength(String session);

    @Query("SELECT * FROM change WHERE session = :session AND position = :position")
    List<Change> getChanges(String session, long position);

    @Query("SELECT * FROM change WHERE session = :session AND position = (SELECT current_state FROM session WHERE name = :session)")
    List<Change> getCurrentChanges(String session);
}
