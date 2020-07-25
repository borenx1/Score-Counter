package com.bx.scorecounter.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SessionDao {

    @Insert
    void insert(Session session);

    @Update
    void update(Session session);

    @Query("UPDATE session SET current_state = :currentState WHERE name = :name")
    void updateCurrentState(String name, long currentState);

    @Query("UPDATE session SET current_state = current_state + 1 WHERE name = :name")
    void incrementCurrentState(String name);

    @Query("UPDATE session SET current_state = current_state - 1 WHERE name = :name")
    void decrementCurrentState(String name);

    @Delete
    void delete(Session session);

    @Query("DELETE FROM session WHERE name = :name")
    void delete(String name);

    @Query("SELECT * FROM session ORDER BY date_time DESC")
    LiveData<Session[]> loadAllSessions();

    @Query("SELECT * FROM session ORDER BY date_time DESC")
    List<Session> getAllSessions();

    @Query("SELECT * FROM session WHERE name = :name")
    LiveData<Session> loadSession(String name);

    @Query("SELECT * FROM session WHERE name = :name")
    Session getSession(String name);
}
