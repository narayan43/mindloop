package com.example.data.model

import com.example.data.entity.QuestionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Encapsulates the projected next review interval and scheduled target date
 * for a question under a specific difficulty self-rating ("EASY", "MEDIUM", "HARD").
 */
data class SrsNextReviewProjection(
    val rating: String,            // "EASY", "MEDIUM", "HARD"
    val intervalHours: Float,      // e.g. 2.0f, 6.0f, 36.0f, 120.0f
    val intervalBadge: String,     // e.g. "2h", "6h", "1.5d", "3d", "5d", "7d"
    val relativeTimeText: String,  // e.g. "In 2 hours", "In 6 hours", "In 1.5 days", "In 5 days"
    val scheduledDayName: String,  // e.g. "Today, 11:30 PM", "Tomorrow (Sat)", "Mon, Sep 22"
    val fullTargetDateText: String,// e.g. "Scheduled for Mon, Sep 22 at 8:00 AM"
    val targetTimestamp: Long,
    val repetitionStage: String,   // e.g. "Urgent Remediation", "Progressive Recall", "Strong Retention"
    val description: String        // e.g. "Prioritized due to 2 previous mistakes"
)

/**
 * Spaced Repetition (SRS) Scheduler engine.
 * Computes exact next review intervals dynamically based on:
 * 1. Historical error count (`timesWrong`)
 * 2. Current answer correctness
 * 3. Total repetition attempts
 * 4. User self-rating (Easy, Medium, Hard)
 */
object SpacedRepetitionScheduler {

    /**
     * Projects the exact next review date, days/hours interval, and schedule description
     * given the current question, whether the user's latest answer was correct,
     * and the chosen difficulty rating ("EASY", "MEDIUM", "HARD").
     */
    fun projectNextReview(
        question: QuestionEntity,
        isCurrentAnswerCorrect: Boolean,
        rating: String,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SrsNextReviewProjection {
        val totalMistakes = question.timesWrong + (if (isCurrentAnswerCorrect) 0 else 1)
        val priorAttempts = question.totalAttempts + 1
        val upperRating = rating.uppercase()

        val (intervalHours, stage, desc) = when (upperRating) {
            "HARD" -> {
                when {
                    totalMistakes >= 3 -> Triple(
                        2.0f,
                        "Urgent Remediation",
                        "High mistake rate ($totalMistakes mistakes). Scheduled for rapid drill in 2 hours."
                    )
                    totalMistakes in 1..2 -> Triple(
                        6.0f,
                        "Active Drill",
                        "Difficult concept with $totalMistakes past mistake${if (totalMistakes > 1) "s" else ""}. Repeating today in 6 hours."
                    )
                    else -> Triple(
                        18.0f,
                        "Hard Review",
                        "Challenging concept rated Hard. Interval set to 18 hours for solid retention."
                    )
                }
            }
            "MEDIUM" -> {
                when {
                    totalMistakes >= 3 -> Triple(
                        12.0f,
                        "Careful Reinforcement",
                        "Moderate recall with $totalMistakes past mistakes. Scheduled for tomorrow morning in 12 hours."
                    )
                    totalMistakes in 1..2 -> Triple(
                        36.0f,
                        "Progressive Recall",
                        "Moderate recall. Spaced to 1.5 days (36 hours)."
                    )
                    else -> Triple(
                        72.0f,
                        "Steady Spacing",
                        "Smooth recall with clean mistake record. Spaced to 3 days (72 hours)."
                    )
                }
            }
            "EASY" -> {
                when {
                    totalMistakes >= 3 -> Triple(
                        24.0f,
                        "Mistake Recovery",
                        "Answered easily, but verified after $totalMistakes past mistakes. Spaced to 1 day."
                    )
                    totalMistakes in 1..2 -> Triple(
                        72.0f,
                        "Strengthened Recall",
                        "Mastered with minor error history. Spaced out to 3 days (72 hours)."
                    )
                    priorAttempts >= 4 -> Triple(
                        240.0f,
                        "Long-term Retention",
                        "Consistent mastery across multiple reviews. Spaced out to 10 days."
                    )
                    priorAttempts in 2..3 -> Triple(
                        168.0f,
                        "Strong Retention",
                        "Clean error record with solid recall. Spaced out to 7 days (1 week)."
                    )
                    else -> Triple(
                        120.0f,
                        "Initial Mastery",
                        "Clean first-time mastery. Spaced out to 5 days."
                    )
                }
            }
            else -> Triple(24.0f, "Standard Review", "Standard 1-day spacing.")
        }

        val targetTimestamp = currentTimeMillis + (intervalHours * 3600 * 1000).toLong()

        val intervalBadge = when {
            intervalHours < 24.0f -> "${intervalHours.toInt()}h"
            intervalHours == 24.0f -> "1d"
            intervalHours == 36.0f -> "1.5d"
            else -> "${(intervalHours / 24.0f).toInt()}d"
        }

        val relativeTimeText = when {
            intervalHours < 1.0f -> "< 1 hr"
            intervalHours < 24.0f -> "In ${intervalHours.toInt()} hrs"
            intervalHours == 24.0f -> "In 1 day"
            intervalHours == 36.0f -> "In 1.5 days"
            else -> "In ${(intervalHours / 24.0f).toInt()} days"
        }

        val targetCal = Calendar.getInstance().apply { timeInMillis = targetTimestamp }
        val currentCal = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }

        val isSameDay = targetCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                targetCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR)

        val isTomorrow = targetCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                targetCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR) + 1

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        val scheduledDayName = when {
            isSameDay -> "Today (${timeFormat.format(Date(targetTimestamp))})"
            isTomorrow -> "Tomorrow (${dayOfWeekFormat.format(Date(targetTimestamp))})"
            else -> dateFormat.format(Date(targetTimestamp))
        }

        val fullTargetDateText = when {
            isSameDay -> "Today at ${timeFormat.format(Date(targetTimestamp))}"
            isTomorrow -> "Tomorrow at ${timeFormat.format(Date(targetTimestamp))}"
            else -> "${dateFormat.format(Date(targetTimestamp))} at ${timeFormat.format(Date(targetTimestamp))}"
        }

        return SrsNextReviewProjection(
            rating = upperRating,
            intervalHours = intervalHours,
            intervalBadge = intervalBadge,
            relativeTimeText = relativeTimeText,
            scheduledDayName = scheduledDayName,
            fullTargetDateText = fullTargetDateText,
            targetTimestamp = targetTimestamp,
            repetitionStage = stage,
            description = desc
        )
    }

    /**
     * Returns the 3 projections for Easy, Medium, and Hard together
     * so UI components can display them simultaneously.
     */
    fun getProjectionsForQuestion(
        question: QuestionEntity,
        isCurrentAnswerCorrect: Boolean,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): Triple<SrsNextReviewProjection, SrsNextReviewProjection, SrsNextReviewProjection> {
        val easy = projectNextReview(question, isCurrentAnswerCorrect, "EASY", currentTimeMillis)
        val medium = projectNextReview(question, isCurrentAnswerCorrect, "MEDIUM", currentTimeMillis)
        val hard = projectNextReview(question, isCurrentAnswerCorrect, "HARD", currentTimeMillis)
        return Triple(easy, medium, hard)
    }
}
