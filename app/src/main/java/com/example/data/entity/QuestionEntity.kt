package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val linkedNoteId: Long? = null,
    val examId: String = "UPSI",
    val subjectName: String,
    val chapterName: String,
    val questionType: String = "MULTIPLE_CHOICE", // "MULTIPLE_CHOICE" or "TRUE_FALSE"
    val questionText: String,
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswerIndex: Int = 0, // 0 for A/True, 1 for B/False, 2 for C, 3 for D
    val timesShown: Int = 0,
    val timesWrong: Int = 0,
    val totalAttempts: Int = 0,
    val totalTimeSpentSeconds: Long = 0,
    val lastRating: String? = null, // "EASY", "MEDIUM", "HARD"
    val isDue: Boolean = true,
    val lastAttemptTimestamp: Long = System.currentTimeMillis(),
    val sourceType: String = "note", // "note" or "reel"
    val sourceId: String = "" // unified source reference (e.g. note ID or reel ID)
)
