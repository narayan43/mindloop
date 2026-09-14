package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.InitialDataProvider
import com.example.data.entity.QuestionEntity
import com.example.data.firestore.FirestoreRepository
import com.example.ui.components.MindLoopBottomNavBar
import com.example.ui.navigation.BottomNavTab
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MindLoopTheme
import com.example.ui.viewmodel.MindLoopViewModel
import com.example.ui.viewmodel.SrsUrgency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class MindLoopRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInitialDataProvider() {
        val notes = InitialDataProvider.getInitialNotes()
        assertTrue("Notes should not be empty", notes.isNotEmpty())
        val polityNote = notes.find { it.chapterName.contains("Fundamental Rights") }
        assertNotNull("Fundamental rights note must exist", polityNote)
        assertEquals("UPSI", polityNote?.examId)

        val questions = InitialDataProvider.getInitialQuestions()
        assertTrue("Questions should not be empty", questions.isNotEmpty())
        val heartAndSoulQ = questions.find { it.questionText.contains("heart and soul", ignoreCase = true) }
        assertNotNull("Heart and soul question must exist", heartAndSoulQ)
        assertEquals(0, heartAndSoulQ?.correctAnswerIndex)

        val sessions = InitialDataProvider.getInitialSessions()
        assertEquals(8, sessions.size)
    }

    @Test
    fun testBottomNavBarRenderingAndClicks() {
        var selectedTab = BottomNavTab.HOME

        composeTestRule.setContent {
            MindLoopTheme {
                MindLoopBottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }

        composeTestRule.onNodeWithTag("nav_tab_home").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nav_tab_study").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nav_tab_test").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nav_tab_mistakes").assertIsDisplayed()

        // Tap Study tab
        composeTestRule.onNodeWithTag("nav_tab_study").performClick()
        assertEquals(BottomNavTab.STUDY, selectedTab)

        // Tap Test tab
        composeTestRule.onNodeWithTag("nav_tab_test").performClick()
        assertEquals(BottomNavTab.TEST, selectedTab)

        // Tap Mistakes tab
        composeTestRule.onNodeWithTag("nav_tab_mistakes").performClick()
        assertEquals(BottomNavTab.MISTAKES, selectedTab)
    }

    @Test
    fun testHomeScreenActionCardsAndStats() {
        var studyClicked = false
        var testClicked = false
        var mistakesClicked = false

        composeTestRule.setContent {
            MindLoopTheme {
                HomeScreen(
                    onStudyNotesClick = { studyClicked = true },
                    onReviewQuestionsClick = { testClicked = true },
                    onMistakesClick = { mistakesClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("streak_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("today_snapshot_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("section_stats_heading").assertIsDisplayed()
        composeTestRule.onNodeWithTag("stats_streak_grid_card").assertIsDisplayed()

        composeTestRule.onNodeWithTag("action_study_notes").assertIsDisplayed().performClick()
        assertTrue("Study Notes action should trigger", studyClicked)

        composeTestRule.onNodeWithTag("action_review_questions").assertIsDisplayed().performClick()
        assertTrue("Review Questions action should trigger", testClicked)

        composeTestRule.onNodeWithTag("action_mistakes").assertIsDisplayed().performClick()
        assertTrue("Mistakes action should trigger", mistakesClicked)
    }

    @Test
    fun testSpacedRepetitionPrioritization() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = FirestoreRepository(context)
        val viewModel = MindLoopViewModel(repository)

        val criticalQuestion = QuestionEntity(
            id = 1L,
            subjectName = "Polity",
            chapterName = "Fundamental Rights",
            questionText = "Critical question with high mistakes",
            timesShown = 5,
            timesWrong = 4,
            totalAttempts = 5,
            lastRating = "HARD",
            isDue = true
        )

        val hardQuestion = QuestionEntity(
            id = 2L,
            subjectName = "Polity",
            chapterName = "Fundamental Rights",
            questionText = "Hard question with 1 mistake",
            timesShown = 3,
            timesWrong = 1,
            totalAttempts = 3,
            lastRating = "HARD",
            isDue = true
        )

        val mediumQuestion = QuestionEntity(
            id = 3L,
            subjectName = "Polity",
            chapterName = "Fundamental Rights",
            questionText = "Medium question with 0 mistakes",
            timesShown = 2,
            timesWrong = 0,
            totalAttempts = 2,
            lastRating = "MEDIUM",
            isDue = false
        )

        val easyMasteredQuestion = QuestionEntity(
            id = 4L,
            subjectName = "Polity",
            chapterName = "Fundamental Rights",
            questionText = "Mastered easy question",
            timesShown = 6,
            timesWrong = 0,
            totalAttempts = 6,
            lastRating = "EASY",
            isDue = false
        )

        val newQuestion = QuestionEntity(
            id = 5L,
            subjectName = "Polity",
            chapterName = "Fundamental Rights",
            questionText = "Brand new unattempted question",
            timesShown = 0,
            timesWrong = 0,
            totalAttempts = 0,
            lastRating = null,
            isDue = false
        )

        val unprioritizedList = listOf(easyMasteredQuestion, mediumQuestion, newQuestion, criticalQuestion, hardQuestion)
        val prioritizedList = viewModel.prioritizeQuestionsBySrs(unprioritizedList)

        // 1. Critical question with 4 mistakes and HARD rating must be prioritized first
        assertEquals("Most difficult question must be first in SRS order", criticalQuestion.id, prioritizedList.first().id)

        // 2. Hard question must be ahead of medium and mastered easy questions
        val hardIndex = prioritizedList.indexOfFirst { it.id == hardQuestion.id }
        val mediumIndex = prioritizedList.indexOfFirst { it.id == mediumQuestion.id }
        val easyIndex = prioritizedList.indexOfFirst { it.id == easyMasteredQuestion.id }

        assertTrue("Hard question should appear before medium question", hardIndex < mediumIndex)
        assertTrue("Medium question should appear before mastered easy question", mediumIndex < easyIndex)

        // 3. Easy mastered question must be at the end of the queue
        assertEquals("Mastered easy question should be last to avoid review fatigue", easyMasteredQuestion.id, prioritizedList.last().id)

        // 4. Verify SRS Urgency categorization
        val criticalDetails = viewModel.calculateSrsDetails(criticalQuestion)
        assertEquals(SrsUrgency.CRITICAL_MISTAKE, criticalDetails.urgency)
        assertEquals("Critical Repeat", criticalDetails.urgencyLabel)

        val easyDetails = viewModel.calculateSrsDetails(easyMasteredQuestion)
        assertEquals(SrsUrgency.MASTERED, easyDetails.urgency)
        assertEquals("Mastered", easyDetails.urgencyLabel)
    }
}
