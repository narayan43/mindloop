package com.example.data.model

/**
 * Data models for the unified curriculum hierarchy across Study, Test, and Reels:
 * Exams -> Subjects (under exam OR standalone) -> Chapters.
 *
 * Each item tracks [createdBy] ("admin" vs user's UID or "user"),
 * which enforces edit/delete permissions:
 * - Admin items ("admin") cannot be edited/deleted by regular users.
 * - User-created items ("user" or matching user UID) show edit/delete controls.
 */
data class CurriculumExam(
    val id: String,
    val name: String,
    val subtitle: String = "",
    val createdBy: String = "admin", // "admin" or user UID
    val isEnrolled: Boolean = true
)

data class CurriculumSubject(
    val id: String,
    val examName: String? = null, // null if standalone / independent subject
    val name: String,
    val subtitle: String = "",
    val createdBy: String = "admin", // "admin" or user UID
    val isStandalone: Boolean = false
)

data class CurriculumChapter(
    val id: String,
    val examName: String? = null,
    val subjectName: String,
    val name: String,
    val createdBy: String = "admin" // "admin" or user UID
)
