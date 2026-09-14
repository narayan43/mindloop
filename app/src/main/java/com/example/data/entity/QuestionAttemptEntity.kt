package com.example.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "question_attempts")
data class QuestionAttemptEntity(
    @PrimaryKey
    @ColumnInfo(name = "attempt_id")
    val attemptId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "question_id")
    val questionId: Long = 0L,

    @ColumnInfo(name = "note_id")
    val noteId: Long? = null,

    @ColumnInfo(name = "subject")
    val subject: String = "",

    @ColumnInfo(name = "chapter")
    val chapter: String = "",

    @ColumnInfo(name = "question_type")
    val questionType: String = "MULTIPLE_CHOICE", // "MULTIPLE_CHOICE" or "TRUE_FALSE"

    @ColumnInfo(name = "shown_at")
    val shownAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "answered_at")
    val answeredAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "time_taken_seconds")
    val timeTakenSeconds: Long = 0L,

    @ColumnInfo(name = "selected_answer")
    val selectedAnswer: String = "",

    @ColumnInfo(name = "is_correct")
    val isCorrect: Boolean = false,

    @ColumnInfo(name = "self_rating")
    val selfRating: String? = null // "EASY", "MEDIUM", "HARD"
)
