package com.example.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "session_id")
    val sessionId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "note_id")
    val noteId: Long = 0L,

    @ColumnInfo(name = "subject")
    val subject: String = "",

    @ColumnInfo(name = "chapter")
    val chapter: String = "",

    @ColumnInfo(name = "started_at")
    val startedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "ended_at")
    val endedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Long = 0L,

    @ColumnInfo(name = "mode")
    val mode: String = "chapter_wise", // "chapter_wise" or "full_subject"

    // Backward compatibility fields for dashboard / charts aggregation
    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: String = "Mon",

    @ColumnInfo(name = "subject_name")
    val subjectName: String = "",

    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int = 0,

    @ColumnInfo(name = "questions_attempted")
    val questionsAttempted: Int = 0,

    @ColumnInfo(name = "date_str")
    val dateStr: String = "",

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
