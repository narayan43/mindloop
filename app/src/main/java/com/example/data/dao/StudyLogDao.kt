package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyLogDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp ASC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<StudySessionEntity>)

    @Query("UPDATE study_sessions SET ended_at = :endedAt, duration_seconds = :durationSeconds, duration_minutes = :durationMinutes, timestamp = :endedAt WHERE session_id = :sessionId")
    suspend fun updateSessionEnd(sessionId: String, endedAt: Long, durationSeconds: Long, durationMinutes: Int)

    @Query("SELECT * FROM study_sessions WHERE session_id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): StudySessionEntity?
}
