package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InitialDataProvider
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionEntity
import com.example.ui.components.InteractiveCard
import com.example.ui.components.InteractiveCardBorder
import com.example.ui.components.MindLoopPrimaryButton
import com.example.ui.components.MindLoopSecondaryButton
import com.example.ui.components.tapAffordance
import com.example.ui.theme.Amber
import com.example.ui.theme.AmberLight
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

// ==========================================
// DATA MODELS FOR HIERARCHICAL MISTAKES
// ==========================================
data class MistakeConceptModel(
    val subjectName: String,
    val chapterName: String,
    val conceptTitle: String,
    val wrongAttempts: Int,
    val masteryProgress: Float,
    val questionId: Long,
    val noteId: Long?,
    val isReel: Boolean = false,
    val reelId: Long? = null
)

data class ExamMistakeSummary(
    val examName: String,
    val totalWrongAttempts: Int,
    val mistakeQuestionsCount: Int,
    val subjectsCount: Int,
    val masteryProgress: Float,
    val isActive: Boolean = false
)

data class SubjectMistakeSummary(
    val examName: String,
    val subjectName: String,
    val totalWrongAttempts: Int,
    val mistakeQuestionsCount: Int,
    val totalQuestions: Int,
    val masteryProgress: Float,
    val chaptersWithMistakes: Int,
    val totalChapters: Int,
    val icon: ImageVector = Icons.AutoMirrored.Outlined.MenuBook
)

data class ChapterMistakeSummary(
    val chapterNumber: Int,
    val chapterName: String,
    val totalWrongAttempts: Int,
    val mistakeQuestionsCount: Int,
    val totalQuestions: Int,
    val masteryProgress: Float,
    val firstQuestionId: Long?,
    val firstNoteId: Long?,
    val concepts: List<MistakeConceptModel>,
    val isReel: Boolean = false,
    val firstReelId: Long? = null
)

sealed class MistakeHierarchyLevel {
    object Exams : MistakeHierarchyLevel()
    data class Subjects(val examName: String) : MistakeHierarchyLevel()
    data class Chapters(val examName: String, val subjectName: String) : MistakeHierarchyLevel()
}

@Composable
fun MistakesScreen(
    mistakeQuestions: List<QuestionEntity>,
    allQuestions: List<QuestionEntity> = emptyList(),
    allNotes: List<NoteEntity> = emptyList(),
    customSubjects: List<String> = emptyList(),
    customChapters: Map<String, List<String>> = emptyMap(),
    onStudyNotes: (subject: String, chapter: String, noteId: Long?) -> Unit,
    onRetest: (subject: String, chapter: String, questionId: Long) -> Unit,
    onRetestSubject: ((subject: String) -> Unit)? = null,
    onRetestChapter: ((subject: String, chapter: String) -> Unit)? = null,
    onWatchReel: ((reelId: Long) -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Current hierarchical navigation state
    var currentLevel by remember { mutableStateOf<MistakeHierarchyLevel>(MistakeHierarchyLevel.Exams) }
    // Optional view mode toggle: "Organized (Hierarchy)" vs "All Mistakes (Ranked List)"
    var showRankedFlatList by remember { mutableStateOf(false) }

    // Intercept back button to navigate backwards through the hierarchy
    BackHandler(enabled = currentLevel !is MistakeHierarchyLevel.Exams || showRankedFlatList) {
        if (showRankedFlatList) {
            showRankedFlatList = false
        } else {
            currentLevel = when (val lvl = currentLevel) {
                is MistakeHierarchyLevel.Chapters -> MistakeHierarchyLevel.Subjects(lvl.examName)
                is MistakeHierarchyLevel.Subjects -> MistakeHierarchyLevel.Exams
                is MistakeHierarchyLevel.Exams -> MistakeHierarchyLevel.Exams
            }
        }
    }

    // 1. Build ranked concepts list dynamically across both hardcoded data and custom subjects
    val allMistakeConcepts = remember(mistakeQuestions, allQuestions) {
        val initialMistakes = InitialDataProvider.getInitialMistakes()
        val initialList = initialMistakes.map { m ->
            val linkedQuestions = mistakeQuestions.filter { q -> m.linkedQuestionIds.contains(q.id) }
            val totalAttempts = linkedQuestions.sumOf { it.totalAttempts }
            val totalWrong = if (m.totalWrongAttempts > 0) m.totalWrongAttempts else linkedQuestions.sumOf { it.timesWrong }
            val mastery = if (totalAttempts > 0) {
                ((totalAttempts - totalWrong).toFloat() / totalAttempts.toFloat()).coerceIn(0.1f, 0.95f)
            } else {
                (1f - (totalWrong.toFloat() / 15f)).coerceIn(0.15f, 0.85f)
            }
            MistakeConceptModel(
                subjectName = "Indian Polity",
                chapterName = m.chapter,
                conceptTitle = m.conceptTitle,
                wrongAttempts = totalWrong,
                masteryProgress = mastery,
                questionId = m.linkedQuestionIds.firstOrNull() ?: 1L,
                noteId = m.noteId
            )
        }

        val initialQuestionIds = initialMistakes.flatMap { it.linkedQuestionIds }.toSet()
        // Include questions from mistakeQuestions or allQuestions that have timesWrong > 0
        val dynamicMistakes = (mistakeQuestions + allQuestions.filter { it.timesWrong > 0 })
            .distinctBy { it.id }
            .filter { !initialQuestionIds.contains(it.id) }
            .map { q ->
                val totalAtt = q.totalAttempts.coerceAtLeast(q.timesShown).coerceAtLeast(q.timesWrong)
                val wrong = q.timesWrong.coerceAtLeast(1)
                val mastery = if (totalAtt > 0) {
                    ((totalAtt - wrong).toFloat() / totalAtt.toFloat()).coerceIn(0.1f, 0.9f)
                } else 0.45f
                val isReel = q.sourceType.equals("reel", ignoreCase = true) || q.subjectName.contains("Reel", ignoreCase = true)
                val rId = if (isReel) q.sourceId.toLongOrNull() ?: 1L else null
                MistakeConceptModel(
                    subjectName = if (isReel) "Reels Concepts" else q.subjectName.ifBlank { "General Studies" },
                    chapterName = q.chapterName.ifBlank { "Core Concepts" },
                    conceptTitle = q.questionText.take(60),
                    wrongAttempts = wrong,
                    masteryProgress = mastery,
                    questionId = q.id,
                    noteId = q.linkedNoteId,
                    isReel = isReel,
                    reelId = rId
                )
            }

        (initialList + dynamicMistakes).sortedByDescending { it.wrongAttempts }
    }

    // 2. Build exams list with aggregate statistics, including Self-Study & Custom Subjects
    val upsiWrongAttempts = remember(allMistakeConcepts) {
        allMistakeConcepts.filter { it.subjectName.contains("Polity", ignoreCase = true) || it.subjectName.contains("Hindi", ignoreCase = true) || it.subjectName.contains("Math", ignoreCase = true) || it.subjectName.contains("Reasoning", ignoreCase = true) || it.subjectName.contains("Science", ignoreCase = true) }
            .sumOf { it.wrongAttempts }
            .coerceAtLeast(38)
    }
    val upsiMistakeQuestionsCount = remember(mistakeQuestions, allMistakeConcepts) {
        val count = mistakeQuestions.count { it.subjectName.contains("Polity", ignoreCase = true) || it.timesWrong > 0 }
        if (count > 0) count else allMistakeConcepts.size
    }

    // Reels mistakes aggregation
    val reelMistakesCount = remember(allMistakeConcepts) {
        val count = allMistakeConcepts.count { it.isReel || it.subjectName.contains("Reel", ignoreCase = true) }
        count.coerceAtLeast(7)
    }
    val reelWrongAttempts = remember(allMistakeConcepts) {
        val sum = allMistakeConcepts.filter { it.isReel || it.subjectName.contains("Reel", ignoreCase = true) }.sumOf { it.wrongAttempts }
        sum.coerceAtLeast(11)
    }

    // Custom subjects aggregated error statistics
    val selfStudyWrongAttempts = remember(allMistakeConcepts, customSubjects) {
        allMistakeConcepts.filter { concept ->
            customSubjects.any { it.equals(concept.subjectName, ignoreCase = true) }
        }.sumOf { it.wrongAttempts }
    }
    val selfStudyMistakesCount = remember(allMistakeConcepts, customSubjects) {
        allMistakeConcepts.count { concept ->
            customSubjects.any { it.equals(concept.subjectName, ignoreCase = true) }
        }
    }

    val examsList = remember(upsiWrongAttempts, upsiMistakeQuestionsCount, customSubjects, selfStudyWrongAttempts, selfStudyMistakesCount, reelMistakesCount, reelWrongAttempts) {
        val list = mutableListOf<ExamMistakeSummary>()

        list.add(
            ExamMistakeSummary(
                examName = "UPSI — Sub Inspector",
                totalWrongAttempts = upsiWrongAttempts,
                mistakeQuestionsCount = upsiMistakeQuestionsCount,
                subjectsCount = 6,
                masteryProgress = 0.62f,
                isActive = true
            )
        )

        // Reels Video Learning Mistakes Section
        list.add(
            ExamMistakeSummary(
                examName = "Reels Video Learning Mistakes",
                totalWrongAttempts = reelWrongAttempts,
                mistakeQuestionsCount = reelMistakesCount,
                subjectsCount = 1,
                masteryProgress = 0.65f,
                isActive = true
            )
        )

        // Always show Self-Study / Custom Subjects entry so users can diagnose Psychology etc.
        val customCount = customSubjects.size.coerceAtLeast(if (customSubjects.isEmpty()) 1 else 0)
        list.add(
            ExamMistakeSummary(
                examName = "Self-Study & Independent Subjects",
                totalWrongAttempts = selfStudyWrongAttempts,
                mistakeQuestionsCount = selfStudyMistakesCount,
                subjectsCount = customCount,
                masteryProgress = if (selfStudyMistakesCount > 0) 0.65f else 0.98f,
                isActive = true
            )
        )

        list.add(
            ExamMistakeSummary(
                examName = "UPSC — Civil Services",
                totalWrongAttempts = 0,
                mistakeQuestionsCount = 0,
                subjectsCount = 10,
                masteryProgress = 1.0f,
                isActive = false
            )
        )
        list.add(
            ExamMistakeSummary(
                examName = "SSC — CGL",
                totalWrongAttempts = 0,
                mistakeQuestionsCount = 0,
                subjectsCount = 8,
                masteryProgress = 1.0f,
                isActive = false
            )
        )
        list.toList()
    }

    // 3. Build subject summaries for the chosen exam
    val currentExamName = (currentLevel as? MistakeHierarchyLevel.Subjects)?.examName
        ?: (currentLevel as? MistakeHierarchyLevel.Chapters)?.examName
        ?: "UPSI — Sub Inspector"

    val subjectsList = remember(currentExamName, allMistakeConcepts, allQuestions, mistakeQuestions, customSubjects, customChapters, reelWrongAttempts, reelMistakesCount) {
        if (currentExamName.contains("Reel", ignoreCase = true)) {
            listOf(
                SubjectMistakeSummary(
                    examName = currentExamName,
                    subjectName = "Reels Concepts",
                    totalWrongAttempts = reelWrongAttempts,
                    mistakeQuestionsCount = reelMistakesCount,
                    totalQuestions = allQuestions.count { it.sourceType.equals("reel", ignoreCase = true) || it.subjectName.contains("Reel", ignoreCase = true) }.coerceAtLeast(10),
                    masteryProgress = 0.65f,
                    chaptersWithMistakes = 5,
                    totalChapters = 6,
                    icon = Icons.Default.Movie
                )
            )
        } else if (currentExamName.contains("Self-Study", ignoreCase = true)) {
            // Provide entries for each custom subject (e.g. Psychology) and Reels Concepts
            val customSubjs = if (customSubjects.isNotEmpty()) customSubjects else listOf("Psychology")
            val subjectsToDisplay = if (customSubjs.any { it.contains("Reel", ignoreCase = true) }) customSubjs else (customSubjs + "Reels Concepts")
            subjectsToDisplay.map { subjectName ->
                val isReelSubj = subjectName.contains("Reel", ignoreCase = true)
                val concepts = if (isReelSubj) {
                    allMistakeConcepts.filter { it.isReel || it.subjectName.contains("Reel", ignoreCase = true) }
                } else {
                    allMistakeConcepts.filter { it.subjectName.equals(subjectName, ignoreCase = true) }
                }
                val wrongAttempts = concepts.sumOf { it.wrongAttempts }
                val mistakeQuestionsCount = concepts.size
                val subjectQuestions = if (isReelSubj) {
                    allQuestions.filter { it.sourceType.equals("reel", ignoreCase = true) || it.subjectName.contains("Reel", ignoreCase = true) }
                } else {
                    allQuestions.filter { it.subjectName.equals(subjectName, ignoreCase = true) }
                }
                val totalQ = subjectQuestions.size.coerceAtLeast(mistakeQuestionsCount).coerceAtLeast(if (isReelSubj) 10 else 1)
                val chapters = if (isReelSubj) 6 else (customChapters[subjectName]?.size ?: 1)

                SubjectMistakeSummary(
                    examName = currentExamName,
                    subjectName = subjectName,
                    totalWrongAttempts = if (isReelSubj) wrongAttempts.coerceAtLeast(reelWrongAttempts) else wrongAttempts,
                    mistakeQuestionsCount = if (isReelSubj) mistakeQuestionsCount.coerceAtLeast(reelMistakesCount) else mistakeQuestionsCount,
                    totalQuestions = totalQ,
                    masteryProgress = if (isReelSubj) 0.65f else if (wrongAttempts > 0) 0.60f else 0.95f,
                    chaptersWithMistakes = if (isReelSubj) 5 else if (wrongAttempts > 0) 1 else 0,
                    totalChapters = chapters,
                    icon = if (isReelSubj) Icons.Default.Movie else Icons.Outlined.Psychology
                )
            }
        } else {
            // Standard UPSI Subjects
            val polityWrongAttempts = allMistakeConcepts.filter { it.subjectName.contains("Polity", ignoreCase = true) }.sumOf { it.wrongAttempts }
            val polityMistakeQuestions = mistakeQuestions.count { it.subjectName.contains("Polity", ignoreCase = true) || it.timesWrong > 0 }
                .coerceAtLeast(allMistakeConcepts.size)

            val baseList = mutableListOf(
                SubjectMistakeSummary(
                    examName = "UPSI — Sub Inspector",
                    subjectName = "Indian Polity",
                    totalWrongAttempts = polityWrongAttempts,
                    mistakeQuestionsCount = polityMistakeQuestions,
                    totalQuestions = 97,
                    masteryProgress = 0.58f,
                    chaptersWithMistakes = 6,
                    totalChapters = 6,
                    icon = Icons.Outlined.Shield
                ),
                SubjectMistakeSummary(
                    examName = "UPSI — Sub Inspector",
                    subjectName = "Reels Concepts",
                    totalWrongAttempts = reelWrongAttempts,
                    mistakeQuestionsCount = reelMistakesCount,
                    totalQuestions = allQuestions.count { it.sourceType.equals("reel", ignoreCase = true) || it.subjectName.contains("Reel", ignoreCase = true) }.coerceAtLeast(10),
                    masteryProgress = 0.65f,
                    chaptersWithMistakes = 5,
                    totalChapters = 6,
                    icon = Icons.Default.Movie
                ),
                SubjectMistakeSummary(
                    examName = "UPSI — Sub Inspector",
                    subjectName = "General Hindi",
                    totalWrongAttempts = 0,
                    mistakeQuestionsCount = 0,
                    totalQuestions = 85,
                    masteryProgress = 0.94f,
                    chaptersWithMistakes = 0,
                    totalChapters = 8,
                    icon = Icons.Filled.AutoStories
                ),
                SubjectMistakeSummary(
                    examName = "UPSI — Sub Inspector",
                    subjectName = "Numerical & Mental Ability (Math)",
                    totalWrongAttempts = 0,
                    mistakeQuestionsCount = 0,
                    totalQuestions = 90,
                    masteryProgress = 0.91f,
                    chaptersWithMistakes = 0,
                    totalChapters = 12,
                    icon = Icons.Filled.BarChart
                ),
                SubjectMistakeSummary(
                    examName = "UPSI — Sub Inspector",
                    subjectName = "Mental Aptitude / Reasoning",
                    totalWrongAttempts = 0,
                    mistakeQuestionsCount = 0,
                    totalQuestions = 75,
                    masteryProgress = 0.88f,
                    chaptersWithMistakes = 0,
                    totalChapters = 7,
                    icon = Icons.Outlined.Psychology
                ),
                SubjectMistakeSummary(
                    examName = "UPSI — Sub Inspector",
                    subjectName = "General Science & Current Affairs",
                    totalWrongAttempts = 0,
                    mistakeQuestionsCount = 0,
                    totalQuestions = 60,
                    masteryProgress = 0.95f,
                    chaptersWithMistakes = 0,
                    totalChapters = 5,
                    icon = Icons.Filled.Biotech
                )
            )

            // Also append any custom subjects if user added them
            customSubjects.forEach { customSubj ->
                val concepts = allMistakeConcepts.filter { it.subjectName.equals(customSubj, ignoreCase = true) }
                val wrong = concepts.sumOf { it.wrongAttempts }
                val qCount = allQuestions.count { it.subjectName.equals(customSubj, ignoreCase = true) }
                val chCount = customChapters[customSubj]?.size ?: 1
                baseList.add(
                    SubjectMistakeSummary(
                        examName = "UPSI — Sub Inspector",
                        subjectName = customSubj,
                        totalWrongAttempts = wrong,
                        mistakeQuestionsCount = concepts.size,
                        totalQuestions = qCount.coerceAtLeast(concepts.size).coerceAtLeast(1),
                        masteryProgress = if (wrong > 0) 0.60f else 0.95f,
                        chaptersWithMistakes = if (wrong > 0) 1 else 0,
                        totalChapters = chCount,
                        icon = Icons.Outlined.Psychology
                    )
                )
            }

            baseList.toList()
        }
    }

    // 4. Build chapter summaries for selected subject dynamically
    val currentSubjectName = (currentLevel as? MistakeHierarchyLevel.Chapters)?.subjectName ?: "Indian Polity"

    val chaptersForSubject = remember(currentSubjectName, allMistakeConcepts, allQuestions, allNotes, customChapters) {
        val isCurrentSubjectReels = currentSubjectName.contains("Reel", ignoreCase = true)
        val conceptsForSubj = if (isCurrentSubjectReels) {
            allMistakeConcepts.filter { it.isReel || it.subjectName.contains("Reel", ignoreCase = true) }
        } else {
            allMistakeConcepts.filter { it.subjectName.equals(currentSubjectName, ignoreCase = true) }
        }
        val questionsForSubj = if (isCurrentSubjectReels) {
            allQuestions.filter { it.sourceType.equals("reel", ignoreCase = true) || it.subjectName.contains("Reel", ignoreCase = true) }
        } else {
            allQuestions.filter { it.subjectName.equals(currentSubjectName, ignoreCase = true) }
        }
        val notesForSubj = allNotes.filter { it.subjectName.equals(currentSubjectName, ignoreCase = true) }
        val definedChapters = customChapters[currentSubjectName] ?: emptyList()

        val uniqueChapterNames = (
            definedChapters +
            notesForSubj.map { it.chapterName.trim() } +
            questionsForSubj.map { it.chapterName.trim() } +
            conceptsForSubj.map { it.chapterName.trim() }
        ).filter { it.isNotBlank() }.distinct()

        if (uniqueChapterNames.isNotEmpty()) {
            uniqueChapterNames.mapIndexed { idx, chFullName ->
                val cleanName = chFullName.substringAfter(".").trim().ifEmpty { chFullName }
                val chConcepts = conceptsForSubj.filter {
                    it.chapterName.equals(chFullName, ignoreCase = true) ||
                    it.chapterName.contains(cleanName, ignoreCase = true)
                }
                val wrongSum = chConcepts.sumOf { it.wrongAttempts }
                val chQuestions = questionsForSubj.filter {
                    it.chapterName.equals(chFullName, ignoreCase = true) ||
                    it.chapterName.contains(cleanName, ignoreCase = true)
                }
                val qCount = chQuestions.size.coerceAtLeast(chConcepts.size).coerceAtLeast(1)
                val chNumber = chFullName.substringBefore(".").trim().toIntOrNull() ?: (idx + 1)
                val firstQ = chConcepts.firstOrNull()?.questionId ?: chQuestions.firstOrNull()?.id
                val firstN = chConcepts.firstOrNull()?.noteId ?: notesForSubj.find {
                    it.chapterName.contains(cleanName, ignoreCase = true)
                }?.id
                val isReelChapter = isCurrentSubjectReels || chConcepts.any { it.isReel }
                val firstReelId = chConcepts.firstOrNull { it.isReel }?.reelId
                    ?: chQuestions.firstOrNull { it.sourceType.equals("reel", ignoreCase = true) }?.sourceId?.toLongOrNull()
                    ?: (if (isReelChapter) chNumber.toLong() else null)

                ChapterMistakeSummary(
                    chapterNumber = chNumber,
                    chapterName = chFullName,
                    totalWrongAttempts = wrongSum,
                    mistakeQuestionsCount = chConcepts.size,
                    totalQuestions = qCount,
                    masteryProgress = if (wrongSum > 0) 0.55f else 0.90f,
                    firstQuestionId = firstQ,
                    firstNoteId = firstN,
                    concepts = chConcepts,
                    isReel = isReelChapter,
                    firstReelId = firstReelId
                )
            }
        } else if (currentSubjectName.equals("Indian Polity", ignoreCase = true)) {
            // Default Polity chapters
            val chapterDefs = listOf(
                Triple(1, "Making of the Constitution", 88),
                Triple(2, "Preamble", 82),
                Triple(3, "Fundamental Rights", 55),
                Triple(4, "Directive Principles", 48),
                Triple(5, "Union Executive", 82),
                Triple(6, "State Executive", 79)
            )

            chapterDefs.map { (num, name, masteryInt) ->
                val concepts = allMistakeConcepts.filter {
                    it.chapterName.contains(name, ignoreCase = true) ||
                    it.chapterName.startsWith("$num.", ignoreCase = true)
                }
                val wrongSum = concepts.sumOf { it.wrongAttempts }
                val firstQ = concepts.firstOrNull()?.questionId
                val firstN = concepts.firstOrNull()?.noteId

                ChapterMistakeSummary(
                    chapterNumber = num,
                    chapterName = name,
                    totalWrongAttempts = wrongSum,
                    mistakeQuestionsCount = concepts.size,
                    totalQuestions = when (num) {
                        1 -> 22
                        2 -> 13
                        3 -> 22
                        4 -> 15
                        5 -> 14
                        else -> 11
                    },
                    masteryProgress = masteryInt / 100f,
                    firstQuestionId = firstQ,
                    firstNoteId = firstN,
                    concepts = concepts
                )
            }
        } else {
            // Single fallback chapter for newly created subject
            listOf(
                ChapterMistakeSummary(
                    chapterNumber = 1,
                    chapterName = "Introduction & Core Topics",
                    totalWrongAttempts = conceptsForSubj.sumOf { it.wrongAttempts },
                    mistakeQuestionsCount = conceptsForSubj.size,
                    totalQuestions = questionsForSubj.size.coerceAtLeast(conceptsForSubj.size).coerceAtLeast(1),
                    masteryProgress = if (conceptsForSubj.isNotEmpty()) 0.55f else 0.95f,
                    firstQuestionId = conceptsForSubj.firstOrNull()?.questionId ?: questionsForSubj.firstOrNull()?.id,
                    firstNoteId = conceptsForSubj.firstOrNull()?.noteId ?: notesForSubj.firstOrNull()?.id,
                    concepts = conceptsForSubj
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // TOP BAR
        MistakesTopBar(
            currentLevel = currentLevel,
            showRankedFlatList = showRankedFlatList,
            onToggleView = { showRankedFlatList = !showRankedFlatList },
            onBackClick = {
                if (showRankedFlatList) {
                    showRankedFlatList = false
                } else {
                    when (val lvl = currentLevel) {
                        is MistakeHierarchyLevel.Chapters -> currentLevel = MistakeHierarchyLevel.Subjects(lvl.examName)
                        is MistakeHierarchyLevel.Subjects -> currentLevel = MistakeHierarchyLevel.Exams
                        is MistakeHierarchyLevel.Exams -> onBack()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // CONTENT DISPLAY BASED ON LEVEL / MODE
        if (showRankedFlatList) {
            // Flat ranked mistakes view
            RankedMistakesFlatListView(
                concepts = allMistakeConcepts,
                onStudyNotes = { concept ->
                    if (concept.isReel && onWatchReel != null) {
                        onWatchReel(concept.reelId ?: 1L)
                    } else {
                        onStudyNotes(concept.subjectName, concept.chapterName, concept.noteId)
                    }
                },
                onRetest = { concept -> onRetest(concept.subjectName, concept.chapterName, concept.questionId) }
            )
        } else {
            when (val lvl = currentLevel) {
                is MistakeHierarchyLevel.Exams -> {
                    MistakesExamListView(
                        exams = examsList,
                        onSelectExam = { examName ->
                            if (examName.contains("Reel", ignoreCase = true)) {
                                currentLevel = MistakeHierarchyLevel.Chapters(examName, "Reels Concepts")
                            } else {
                                currentLevel = MistakeHierarchyLevel.Subjects(examName)
                            }
                        },
                        onViewRankedList = { showRankedFlatList = true }
                    )
                }

                is MistakeHierarchyLevel.Subjects -> {
                    MistakesSubjectListView(
                        examName = lvl.examName,
                        subjects = subjectsList,
                        onSelectSubject = { subjectName ->
                            currentLevel = MistakeHierarchyLevel.Chapters(lvl.examName, subjectName)
                        },
                        onAnalyzeEntireSubject = { subjectName ->
                            if (onRetestSubject != null) {
                                onRetestSubject(subjectName)
                            } else {
                                onRetest(subjectName, "All Chapters", 1L)
                            }
                        }
                    )
                }

                is MistakeHierarchyLevel.Chapters -> {
                    MistakesChapterListView(
                        examName = lvl.examName,
                        subjectName = lvl.subjectName,
                        chapters = chaptersForSubject,
                        onAnalyzeEntireSubject = {
                            if (onRetestSubject != null) {
                                onRetestSubject(lvl.subjectName)
                            } else {
                                onRetest(lvl.subjectName, "All Chapters", 1L)
                            }
                        },
                        onAnalyzeChapterMistakes = { chapter ->
                            if (onRetestChapter != null) {
                                onRetestChapter(lvl.subjectName, chapter.chapterName)
                            } else {
                                onRetest(lvl.subjectName, chapter.chapterName, chapter.firstQuestionId ?: 1L)
                            }
                        },
                        onStudyChapterNotes = { chapter ->
                            if (chapter.isReel && onWatchReel != null) {
                                onWatchReel(chapter.firstReelId ?: 1L)
                            } else {
                                onStudyNotes(lvl.subjectName, chapter.chapterName, chapter.firstNoteId)
                            }
                        },
                        onRetestConcept = { concept ->
                            onRetest(concept.subjectName, concept.chapterName, concept.questionId)
                        },
                        onStudyConceptNotes = { concept ->
                            if (concept.isReel && onWatchReel != null) {
                                onWatchReel(concept.reelId ?: 1L)
                            } else {
                                onStudyNotes(concept.subjectName, concept.chapterName, concept.noteId)
                            }
                        }
                    )
                }
            }
        }
    }
}

// ==========================================
// 1. TOP BAR COMPONENT
// ==========================================
@Composable
private fun MistakesTopBar(
    currentLevel: MistakeHierarchyLevel,
    showRankedFlatList: Boolean,
    onToggleView: () -> Unit,
    onBackClick: () -> Unit
) {
    val titleText = when {
        showRankedFlatList -> "Ranked Mistakes"
        currentLevel is MistakeHierarchyLevel.Chapters -> "${(currentLevel as MistakeHierarchyLevel.Chapters).subjectName} Mistakes"
        currentLevel is MistakeHierarchyLevel.Subjects -> "${(currentLevel as MistakeHierarchyLevel.Subjects).examName} Mistakes"
        else -> "Mistakes & Errors"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("mistakes_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepIndigo
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = titleText,
                fontSize = if (titleText.length > 18) 17.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f, fill = false)
            )
        }

        // View Mode Switcher: Hierarchical vs Flat
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (showRankedFlatList) DeepIndigo else AmberLight)
                .clickable { onToggleView() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("toggle_mistakes_view_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (showRankedFlatList) Icons.Default.FilterList else Icons.Outlined.TrackChanges,
                    contentDescription = null,
                    tint = if (showRankedFlatList) Color.White else Amber,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showRankedFlatList) "By Exam" else "All Errors",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (showRankedFlatList) Color.White else Amber
                )
            }
        }
    }
}

// ==========================================
// 2. LEVEL 1: EXAMS LIST VIEW
// ==========================================
@Composable
private fun MistakesExamListView(
    exams: List<ExamMistakeSummary>,
    onSelectExam: (String) -> Unit,
    onViewRankedList: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Introductory Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, InteractiveCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TerracottaLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.TrackChanges,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Exam-Wise Error Diagnostics",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Text(
                                text = "Select an exam below to explore subject & chapter errors",
                                fontSize = 12.5.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Target Exams",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        items(exams) { exam ->
            InteractiveCard(
                onClick = { onSelectExam(exam.examName) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mistakes_exam_card_${exam.examName.take(4)}"),
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exam.examName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${exam.subjectsCount} Subjects Curriculum",
                                fontSize = 12.5.sp,
                                color = TextSecondary
                            )
                        }

                        if (exam.isActive && exam.totalWrongAttempts > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TerracottaLight)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${exam.totalWrongAttempts} Wrong Attempts",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Terracotta
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SageGreenLight)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Clean",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress / Accuracy Bar
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFEFF2F6))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(exam.masteryProgress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (exam.masteryProgress > 0.7f) SageGreen else Amber)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (exam.totalWrongAttempts > 0) "${exam.mistakeQuestionsCount} mistake questions identified" else "No mistakes registered",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${(exam.masteryProgress * 100).toInt()}% Accuracy",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (exam.masteryProgress > 0.7f) SageGreen else Amber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tap affordance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Explore Subject Mistakes",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepIndigo
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = DeepIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ==========================================
// 3. LEVEL 2: SUBJECTS LIST VIEW
// ==========================================
@Composable
private fun MistakesSubjectListView(
    examName: String,
    subjects: List<SubjectMistakeSummary>,
    onSelectSubject: (String) -> Unit,
    onAnalyzeEntireSubject: (String) -> Unit
) {
    val totalMistakes = remember(subjects) { subjects.sumOf { it.totalWrongAttempts } }
    val weakestSubject = remember(subjects) { subjects.maxByOrNull { it.totalWrongAttempts }?.subjectName ?: "None" }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Overview Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepIndigo),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "$examName • Error Dashboard",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$totalMistakes Total Wrong Attempts",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Highest error concentration: $weakestSubject. Tap 'Analyze Entire Subject Mistakes' to retest, or choose a subject for chapter-wise breakdown.",
                        fontSize = 12.5.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "Subjects in $examName",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(subjects) { subject ->
            val hasErrors = subject.totalWrongAttempts > 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .tapAffordance(shape = RoundedCornerShape(16.dp), elevation = 4.dp)
                    .testTag("mistakes_subject_card_${subject.subjectName.take(5)}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, if (hasErrors) Amber.copy(alpha = 0.6f) else InteractiveCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header: Subject Icon, Name, and Wrong Attempts Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (hasErrors) AmberLight else SageGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = subject.icon,
                                contentDescription = null,
                                tint = if (hasErrors) Amber else SageGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = subject.subjectName,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${subject.totalChapters} Chapters • ${subject.totalQuestions} Questions",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        // Prominent Mistakes Badge (User requirement: all the questions data should be mentioned how many questions of this subject are wrong you did)
                        if (hasErrors) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TerracottaLight)
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${subject.totalWrongAttempts} Wrong",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Terracotta
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SageGreenLight)
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "0 Mistakes",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mastery & Questions Wrong breakdown
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFEFF2F6))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(subject.masteryProgress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (subject.masteryProgress > 0.7f) SageGreen else Amber)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (hasErrors) "${subject.mistakeQuestionsCount} questions wrong across ${subject.chaptersWithMistakes} chapters" else "All questions answered correctly",
                                fontSize = 11.5.sp,
                                color = if (hasErrors) Terracotta else SageGreen,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(subject.masteryProgress * 100).toInt()}% Mastery",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION BUTTON 1 (User requirement: "there should be a button also as wellbe for anlye the entire subjects mistakes")
                    MindLoopPrimaryButton(
                        onClick = { onAnalyzeEntireSubject(subject.subjectName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("analyze_entire_subject_${subject.subjectName.take(5)}"),
                        containerColor = if (hasErrors) SageGreen else DeepIndigo,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasErrors) "Analyze Full Subject (${subject.mistakeQuestionsCount} Qs)" else "Practice Full Subject",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ACTION BUTTON 2 (User requirement: "if you don't want to analyse the entire subject mistake there should be you can click the subject and go chapter wise and analyse chapter wise mistakes")
                    MindLoopSecondaryButton(
                        onClick = { onSelectSubject(subject.subjectName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("view_chapters_subject_${subject.subjectName.take(5)}"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                            contentDescription = null,
                            tint = DeepIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chapter-Wise Breakdown →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepIndigo,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ==========================================
// 4. LEVEL 3: CHAPTERS LIST VIEW (WITH EXPANDABLE CONCEPTS)
// ==========================================
@Composable
private fun MistakesChapterListView(
    examName: String,
    subjectName: String,
    chapters: List<ChapterMistakeSummary>,
    onAnalyzeEntireSubject: () -> Unit,
    onAnalyzeChapterMistakes: (ChapterMistakeSummary) -> Unit,
    onStudyChapterNotes: (ChapterMistakeSummary) -> Unit,
    onRetestConcept: (MistakeConceptModel) -> Unit,
    onStudyConceptNotes: (MistakeConceptModel) -> Unit
) {
    val totalSubjectErrors = remember(chapters) { chapters.sumOf { it.totalWrongAttempts } }
    val totalSubjectMistakeQuestions = remember(chapters) { chapters.sumOf { it.mistakeQuestionsCount } }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // TOP ACTION: Analyze Entire Subject Mistakes Button (always accessible at top of chapter list)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepIndigo),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "$examName • $subjectName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalSubjectErrors Errors Across ${chapters.size} Chapters",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    MindLoopPrimaryButton(
                        onClick = onAnalyzeEntireSubject,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("analyze_full_subject_top_banner"),
                        containerColor = SageGreen,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyze Full Subject Mistakes ($totalSubjectMistakeQuestions Questions)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Chapters in $subjectName",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(chapters) { chapter ->
            ChapterMistakeCardItem(
                chapter = chapter,
                onAnalyzeChapterMistakes = { onAnalyzeChapterMistakes(chapter) },
                onStudyChapterNotes = { onStudyChapterNotes(chapter) },
                onRetestConcept = onRetestConcept,
                onStudyConceptNotes = onStudyConceptNotes
            )
        }

        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ==========================================
// 5. CHAPTER MISTAKE CARD WITH CONCEPTS ACCORDION
// ==========================================
@Composable
private fun ChapterMistakeCardItem(
    chapter: ChapterMistakeSummary,
    onAnalyzeChapterMistakes: () -> Unit,
    onStudyChapterNotes: () -> Unit,
    onRetestConcept: (MistakeConceptModel) -> Unit,
    onStudyConceptNotes: (MistakeConceptModel) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val hasErrors = chapter.totalWrongAttempts > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .tapAffordance(shape = RoundedCornerShape(16.dp), elevation = 4.dp)
            .testTag("mistakes_chapter_card_${chapter.chapterNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, if (hasErrors) Amber.copy(alpha = 0.5f) else InteractiveCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Chapter number + title + error badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (hasErrors) TerracottaLight else SageGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${chapter.chapterNumber}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasErrors) Terracotta else SageGreen
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chapter.chapterName,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${chapter.totalQuestions} Total Questions • ${chapter.concepts.size} Tracked Concepts",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                if (hasErrors) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TerracottaLight)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${chapter.totalWrongAttempts} Wrong",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Terracotta
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SageGreenLight)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Clean",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mastery bar
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFEFF2F6))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(chapter.masteryProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (chapter.masteryProgress > 0.7f) SageGreen else Amber)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (hasErrors) "${chapter.mistakeQuestionsCount} mistake questions to review" else "Optimal retention",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${(chapter.masteryProgress * 100).toInt()}% Accuracy",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (chapter.masteryProgress > 0.7f) SageGreen else Amber
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TWO ACTION BUTTONS PER CHAPTER (User requirement:
            // "in the chapter there should be button for every chapter which chapter you want to you know which chapters mistake you want to analyse or want to study the notes")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Study Notes or Watch Reel for this chapter
                MindLoopSecondaryButton(
                    onClick = onStudyChapterNotes,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("study_notes_chapter_${chapter.chapterNumber}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (chapter.isReel) Icons.Default.PlayCircle else Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = if (chapter.isReel) Terracotta else DeepIndigo,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (chapter.isReel) "Watch Reel" else "Study Notes",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (chapter.isReel) Terracotta else DeepIndigo,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Button 2: Analyze Mistakes for this chapter
                MindLoopPrimaryButton(
                    onClick = onAnalyzeChapterMistakes,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("analyze_chapter_${chapter.chapterNumber}"),
                    shape = RoundedCornerShape(12.dp),
                    containerColor = DeepIndigo
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrackChanges,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Analyze Mistakes",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Accordion toggle to inspect specific concept cards
            if (chapter.concepts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                        .testTag("expand_concepts_${chapter.chapterNumber}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isExpanded) "Hide Concept Details (${chapter.concepts.size})" else "View Concept Details (${chapter.concepts.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Expandable sub-cards for individual concepts
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        chapter.concepts.forEachIndexed { idx, concept ->
                            SubConceptMistakeCardItem(
                                concept = concept,
                                onStudyNotes = { onStudyConceptNotes(concept) },
                                onRetest = { onRetestConcept(concept) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. SUB-CONCEPT CARD ITEM (EXPANDED INSIDE CHAPTER)
// ==========================================
@Composable
private fun SubConceptMistakeCardItem(
    concept: MistakeConceptModel,
    onStudyNotes: () -> Unit,
    onRetest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundOffWhite),
        border = BorderStroke(1.dp, InteractiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = concept.conceptTitle,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TerracottaLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${concept.wrongAttempts} wrong",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Terracotta
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Two compact buttons: Study Notes / Watch Reel & Retest
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MindLoopSecondaryButton(
                    onClick = onStudyNotes,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (concept.isReel) Icons.Default.PlayCircle else Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = if (concept.isReel) Terracotta else DeepIndigo,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (concept.isReel) "Watch Reel" else "Notes",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (concept.isReel) Terracotta else DeepIndigo
                    )
                }

                MindLoopPrimaryButton(
                    onClick = onRetest,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    containerColor = DeepIndigo
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrackChanges,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Retest", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

// ==========================================
// 7. FLAT RANKED MISTAKES LIST VIEW
// ==========================================
@Composable
private fun RankedMistakesFlatListView(
    concepts: List<MistakeConceptModel>,
    onStudyNotes: (MistakeConceptModel) -> Unit,
    onRetest: (MistakeConceptModel) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "All concepts ranked across subjects by error frequency. Master high-yield items first.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }

        itemsIndexed(concepts) { index, concept ->
            MistakeCardItem(
                index = index,
                concept = concept,
                onStudyNotes = { onStudyNotes(concept) },
                onRetest = { onRetest(concept) }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ==========================================
// 8. CARD ITEM FOR FLAT RANKED VIEW
// ==========================================
@Composable
fun MistakeCardItem(
    index: Int,
    concept: MistakeConceptModel,
    onStudyNotes: () -> Unit,
    onRetest: () -> Unit
) {
    val isTopMistake = index == 0
    val borderModifier = if (isTopMistake) {
        Modifier.border(1.5.dp, Amber, RoundedCornerShape(16.dp))
    } else {
        Modifier
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .tapAffordance(shape = RoundedCornerShape(16.dp), elevation = 5.dp)
            .then(borderModifier)
            .testTag("mistake_item_$index"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, if (isTopMistake) Amber else InteractiveCardBorder),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
            pressedElevation = 1.5.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row with Concept Title and "X wrong attempts" badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = concept.conceptTitle,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${concept.subjectName} • ${concept.chapterName}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(TerracottaLight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${concept.wrongAttempts} wrong attempts",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Terracotta
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mastery progress bar
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFEFF2F6))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(concept.masteryProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (concept.masteryProgress > 0.5f) SageGreen else Amber)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mastery: ${(concept.masteryProgress * 100).toInt()}%",
                    fontSize = 11.5.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two buttons: Study Notes / Watch Reel and Retest
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MindLoopSecondaryButton(
                    onClick = onStudyNotes,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("mistake_study_notes_$index"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (concept.isReel) Icons.Default.PlayCircle else Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = if (concept.isReel) Terracotta else DeepIndigo,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (concept.isReel) "Watch Reel" else "Study Notes",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (concept.isReel) Terracotta else DeepIndigo
                    )
                }

                MindLoopPrimaryButton(
                    onClick = onRetest,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("mistake_retest_$index"),
                    shape = RoundedCornerShape(12.dp),
                    containerColor = DeepIndigo
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrackChanges,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Retest",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
