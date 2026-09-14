package com.example.data.firestore

import android.util.Log
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionAttemptEntity
import com.example.data.entity.QuestionEntity
import com.example.data.entity.StudySessionEntity
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Lightweight logging utility to verify Firestore data fetching and field mappings in Logcat.
 *
 * Filter Logcat by:
 *   tag:FirestoreDataLogger
 */
object FirestoreDataLogger {

    private const val TAG = "FirestoreDataLogger"

    /**
     * Enable or disable detailed per-document logging.
     * Set to true to inspect individual field mappings.
     */
    var isVerboseLoggingEnabled: Boolean = true

    /**
     * Logs the result of a Notes collection fetch from Firestore.
     */
    fun logNotesFetch(source: String, totalDocs: Int, mappedNotes: List<NoteEntity>) {
        val uniqueChapters = mappedNotes.map { it.chapterName }.distinct().size
        val uniqueSubjects = mappedNotes.map { it.subjectName }.distinct().size
        Log.i(
            TAG,
            "📥 [Notes Fetch - $source] Fetched: $totalDocs docs | Successfully Mapped: ${mappedNotes.size} | " +
                "Subjects: $uniqueSubjects | Chapters: $uniqueChapters"
        )
    }

    /**
     * Verifies and logs an individual Note mapping from a Firestore DocumentSnapshot.
     */
    fun logNoteMapping(doc: DocumentSnapshot, note: NoteEntity?, error: Throwable? = null) {
        if (error != null) {
            Log.e(TAG, "❌ [Note Mapping Error] docId=${doc.id} failed: ${error.message}", error)
            return
        }
        if (note == null) {
            Log.w(TAG, "⚠️ [Note Mapping Skipped] docId=${doc.id} produced null entity")
            return
        }

        if (isVerboseLoggingEnabled) {
            val contentPreview = if (note.summaryText.length > 40) {
                note.summaryText.take(40) + "..."
            } else {
                note.summaryText
            }
            Log.d(
                TAG,
                "📝 [Note Mapped] ID=${note.id} | Subject='${note.subjectName}' | Chapter='${note.chapterName}' (#${note.chapterNumber}) | " +
                    "Title='${note.title}' | Content='$contentPreview' | Revisits=${note.revisitCount} | TimeSpent=${note.timeSpentSeconds}s"
            )
        }

        // Integrity checks for common data distortions
        if (note.title.isBlank()) {
            Log.w(TAG, "⚠️ [Data Warning] Note docId=${doc.id} has empty 'title' field")
        }
        if (note.summaryText.isBlank()) {
            Log.w(TAG, "⚠️ [Data Warning] Note docId=${doc.id} has empty 'content' field")
        }
    }

    /**
     * Logs the result of a Questions collection fetch from Firestore.
     */
    fun logQuestionsFetch(source: String, totalDocs: Int, mappedQuestions: List<QuestionEntity>) {
        val mistakesCount = mappedQuestions.count { it.timesWrong > 0 }
        val dueCount = mappedQuestions.count { it.isDue }
        val mcqCount = mappedQuestions.count { it.questionType == "MULTIPLE_CHOICE" }
        val tfCount = mappedQuestions.count { it.questionType == "TRUE_FALSE" }

        Log.i(
            TAG,
            "📥 [Questions Fetch - $source] Fetched: $totalDocs docs | Successfully Mapped: ${mappedQuestions.size} | " +
                "Mistakes(timesWrong>0): $mistakesCount | Due: $dueCount | MCQs: $mcqCount | TrueFalse: $tfCount"
        )
    }

    /**
     * Verifies and logs an individual Question mapping from a Firestore DocumentSnapshot.
     */
    fun logQuestionMapping(doc: DocumentSnapshot, question: QuestionEntity?, error: Throwable? = null) {
        if (error != null) {
            Log.e(TAG, "❌ [Question Mapping Error] docId=${doc.id} failed: ${error.message}", error)
            return
        }
        if (question == null) {
            Log.w(TAG, "⚠️ [Question Mapping Skipped] docId=${doc.id} produced null entity")
            return
        }

        if (isVerboseLoggingEnabled) {
            val qPreview = if (question.questionText.length > 45) {
                question.questionText.take(45) + "..."
            } else {
                question.questionText
            }
            Log.d(
                TAG,
                "❓ [Question Mapped] ID=${question.id} | LinkedNoteId=${question.linkedNoteId ?: "none"} | " +
                    "Subject='${question.subjectName}' | Chapter='${question.chapterName}' | Type=${question.questionType} | " +
                    "Text='$qPreview' | CorrectIdx=${question.correctAnswerIndex} | TimesShown=${question.timesShown} | " +
                    "TimesWrong=${question.timesWrong} | Rating=${question.lastRating ?: "none"} | IsDue=${question.isDue}"
            )
        }

        // Integrity checks for common data distortions
        if (question.questionText.isBlank()) {
            Log.w(TAG, "⚠️ [Data Warning] Question docId=${doc.id} has empty 'question_text'")
        }
        if (question.timesWrong > question.timesShown && question.timesShown > 0) {
            Log.w(
                TAG,
                "⚠️ [Data Inconsistency] Question ID=${question.id} has timesWrong (${question.timesWrong}) > timesShown (${question.timesShown})"
            )
        }
        if (question.questionType == "MULTIPLE_CHOICE" && question.optionA.isBlank()) {
            Log.w(TAG, "⚠️ [Data Warning] MCQ Question ID=${question.id} has empty Option A")
        }
    }

    /**
     * Verifies and logs a StudySession mapping.
     */
    fun logSessionMapping(doc: DocumentSnapshot, session: StudySessionEntity?, error: Throwable? = null) {
        if (error != null) {
            Log.e(TAG, "❌ [Session Mapping Error] docId=${doc.id} failed: ${error.message}", error)
            return
        }
        if (session == null) return

        if (isVerboseLoggingEnabled) {
            Log.d(
                TAG,
                "⏱️ [Session Mapped] SessionId=${session.sessionId} | Subject='${session.subject}' | " +
                    "Chapter='${session.chapter}' | Duration=${session.durationSeconds}s (${session.durationMinutes}m) | Mode=${session.mode}"
            )
        }
    }

    /**
     * Verifies and logs a QuestionAttempt mapping.
     */
    fun logAttemptMapping(doc: DocumentSnapshot, attempt: QuestionAttemptEntity?, error: Throwable? = null) {
        if (error != null) {
            Log.e(TAG, "❌ [Attempt Mapping Error] docId=${doc.id} failed: ${error.message}", error)
            return
        }
        if (attempt == null) return

        if (isVerboseLoggingEnabled) {
            Log.d(
                TAG,
                "🎯 [Attempt Mapped] AttemptId=${attempt.attemptId} | QuestionId=${attempt.questionId} | " +
                    "Correct=${attempt.isCorrect} | Selected='${attempt.selectedAnswer}' | SelfRating=${attempt.selfRating ?: "none"}"
            )
        }
    }
}
