package com.example.ui.navigation

enum class BottomNavTab {
    HOME,
    STUDY,
    TEST,
    MISTAKES
}

sealed class ScreenDestination {
    object Home : ScreenDestination()
    object Mistakes : ScreenDestination()

    // Study flow
    object StudyExams : ScreenDestination()
    data class StudySubjectsGrid(val examName: String) : ScreenDestination()
    data class StudySubjectDetail(val examName: String, val subjectName: String) : ScreenDestination()
    data class StudyNotesFeed(val subjectName: String, val chapterName: String, val initialNoteId: Long? = null) : ScreenDestination()
    data class AddNotes(val defaultSubject: String = "Indian Polity", val defaultChapter: String = "Fundamental Rights") : ScreenDestination()

    // Test flow
    object TestExams : ScreenDestination()
    data class TestSubjectsGrid(val examName: String) : ScreenDestination()
    data class TestSubjectDetail(val examName: String, val subjectName: String) : ScreenDestination()
    data class QuestionReview(
        val subjectName: String,
        val chapterName: String? = null,
        val specificQuestionId: Long? = null,
        val onlyMistakes: Boolean = false
    ) : ScreenDestination()

    // Auth flow
    data class Auth(val isSignUp: Boolean = false) : ScreenDestination()

    // Admin & Owner Monitoring Dashboard
    object AdminDashboard : ScreenDestination()

    // Reels flow
    object ReelExams : ScreenDestination()
    data class ReelSubjectsGrid(val examName: String) : ScreenDestination()
    data class ReelSubjectDetail(val examName: String, val subjectName: String) : ScreenDestination()
    data class ReelFeed(
        val examName: String = "UPSI",
        val subjectName: String = "Indian Polity",
        val chapterName: String? = null,
        val initialReelId: Long? = null
    ) : ScreenDestination()
}

