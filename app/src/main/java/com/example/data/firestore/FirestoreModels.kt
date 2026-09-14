package com.example.data.firestore

data class FirestoreNote(
    val id: String = "",
    val note_id: Long = 0L,
    val subject: String = "",
    val chapter: String = "",
    val chapter_number: Int = 1,
    val title: String = "",
    val content: String = "",
    val image_uri: String? = null,
    val revisit_count: Int = 1,
    val time_spent_seconds: Long = 0L,
    val created_at: Long = System.currentTimeMillis()
)

data class FirestoreQuestion(
    val id: String = "",
    val question_id: Long = 0L,
    val note_id: Long? = null,
    val source_type: String = "note", // "note" or "reel"
    val source_id: String = "", // unified source reference
    val subject: String = "",
    val chapter: String = "",
    val type: String = "MCQ", // "MCQ" or "True/False"
    val question_text: String = "",
    val option_a: String = "",
    val option_b: String = "",
    val option_c: String = "",
    val option_d: String = "",
    val correct_answer: String = "",
    val correct_answer_index: Int = 0,
    val times_shown: Int = 0,
    val times_wrong: Int = 0,
    val total_attempts: Int = 0,
    val total_time_spent_seconds: Long = 0L,
    val last_rating: String? = null,
    val is_due: Boolean = true,
    val last_attempt_timestamp: Long = System.currentTimeMillis()
)

data class FirestoreReel(
    val id: String = "",
    val reel_id: Long = 0L,
    val exam: String = "UPSI",
    val subject: String = "",
    val chapter: String = "",
    val title: String = "",
    val description: String = "",
    val video_url: String = "",
    val local_cache_path: String? = null,
    val uploaded_by: String = "admin", // "admin" or "student"
    val duration_seconds: Int = 30,
    val watch_count: Int = 0,
    val times_watched: Int = 0,
    val total_watch_time_seconds: Long = 0L,
    val created_at: Long = System.currentTimeMillis()
)

data class FirestoreStudySession(
    val session_id: String = "",
    val note_id: Long = 0L,
    val subject: String = "",
    val chapter: String = "",
    val started_at: Long = System.currentTimeMillis(),
    val ended_at: Long = System.currentTimeMillis(),
    val duration_seconds: Long = 0L,
    val mode: String = "chapter_wise" // "chapter_wise" or "full_subject"
)

data class FirestoreQuestionAttempt(
    val attempt_id: String = "",
    val question_id: Long = 0L,
    val note_id: Long? = null,
    val subject: String = "",
    val chapter: String = "",
    val question_type: String = "MCQ",
    val shown_at: Long = System.currentTimeMillis(),
    val answered_at: Long = System.currentTimeMillis(),
    val time_taken_seconds: Long = 0L,
    val selected_answer: String = "",
    val is_correct: Boolean = false,
    val self_rating: String? = null // "EASY", "MEDIUM", "HARD"
)

data class DashboardWeeklyStat(
    val dayLabel: String,
    val dateStr: String,
    val count: Int
)

data class DashboardChapterAccuracy(
    val chapter: String,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val accuracyPercent: Int
)

data class DashboardMistakeRank(
    val noteId: Long,
    val subject: String,
    val chapter: String,
    val wrongCount: Int,
    val questionCount: Int
)

data class DashboardSummary(
    val todayStudyMinutes: Int = 0,
    val todayQuestionsDone: Int = 0,
    val todayAccuracy: Int = 0,
    val streakDays: Int = 0,
    val weeklyDayCounts: List<Pair<String, Int>> = emptyList(),
    val chapterAccuracies: List<DashboardChapterAccuracy> = emptyList(),
    val totalTimeThisWeekSeconds: Long = 0L,
    val timeBySubject: List<Pair<String, Pair<String, Float>>> = emptyList(),
    val avgTimePerQuestionSeconds: Long = 0L,
    val totalAttemptsCount: Int = 0,
    val overallAccuracyPercent: Int = 0
)
