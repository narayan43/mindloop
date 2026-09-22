package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.QuestionEntity
import com.example.data.model.SpacedRepetitionScheduler
import com.example.data.model.SrsNextReviewProjection
import com.example.ui.components.InteractiveCard
import com.example.ui.components.InteractiveCardBorder
import com.example.ui.components.MindLoopPrimaryButton
import com.example.ui.components.MindLoopSecondaryButton
import com.example.ui.components.tapAffordance
import com.example.ui.theme.Amber
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AppBackground
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceElevated
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SubjectDetailedStats
import kotlinx.coroutines.delay

// ==========================================
// 1. PAGE D — EXAM LIST (TEST)
// ==========================================
@Composable
fun TestExamListScreen(
    customSubjects: List<String> = emptyList(),
    onSelectExam: (String) -> Unit,
    onSelectCustomSubject: (String) -> Unit = {},
    onAddNewSubject: (String) -> Unit = {},
    onOpenReelTests: () -> Unit = {},
    onViewSubjectStats: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showAddSubjectDialog by remember { mutableStateOf(false) }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { subjectName, _ ->
                showAddSubjectDialog = false
                onAddNewSubject(subjectName)
            }
        )
    }

    val isDark = LocalIsDarkTheme.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        // Header: "Test Yourself" with target icon styling and "+ Subject" button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconBg = if (isDark) Color(0xFF133322) else SageGreenLight
                val iconTint = if (isDark) Color(0xFF34D399) else SageGreen

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrackChanges,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Test Yourself",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary
                )
            }
            IconButton(
                onClick = { showAddSubjectDialog = true },
                modifier = Modifier.testTag("test_add_subject_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject", tint = if (isDark) Color(0xFF34D399) else SageGreen)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Active: UPSI
            item {
                ExamCardItem(
                    examName = "UPSI — Sub Inspector",
                    subtitle = "6 Subjects, 120 Topics",
                    badgeText = "42 Due Today",
                    badgeColor = SageGreen,
                    isActive = true,
                    testTag = "test_exam_card_upsi",
                    onClick = { onSelectExam("UPSI") }
                )
            }

            // Reels Video Concepts Test Card
            item {
                ExamCardItem(
                    examName = "Reels Video Concepts Test",
                    subtitle = "10 Video Topics • High-Yield Retention Quiz",
                    badgeText = "Reels Session",
                    badgeColor = Terracotta,
                    isActive = true,
                    testTag = "test_exam_card_reels",
                    onClick = onOpenReelTests
                )
            }

            // Self-Study & Custom Subjects Section (e.g. Psychology)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Self-Study & Independent Subjects",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextSecondary
                    )
                    TextButton(
                        onClick = { showAddSubjectDialog = true },
                        modifier = Modifier.testTag("test_add_custom_subject_text_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = if (isDark) Color(0xFF34D399) else SageGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+ Add Subject", color = if (isDark) Color(0xFF34D399) else SageGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            if (customSubjects.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddSubjectDialog = true }
                            .testTag("test_add_subject_promo_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppSurface),
                        border = BorderStroke(1.dp, AppBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val promoIconBg = if (isDark) Color(0xFF133322) else SageGreenLight
                            val promoIconTint = if (isDark) Color(0xFF34D399) else SageGreen

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(promoIconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Psychology, contentDescription = null, tint = promoIconTint, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Add Any Custom Subject", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppTextPrimary)
                                Text("Test yourself on Psychology, Law, Coding, etc.", fontSize = 12.sp, color = AppTextSecondary)
                            }
                            Icon(Icons.Default.Add, contentDescription = null, tint = promoIconTint)
                        }
                    }
                }
            } else {
                items(customSubjects) { subject ->
                    CustomSubjectCardItem(
                        subjectName = subject,
                        subtitle = "Adaptive Quiz & Diagnostics",
                        badgeText = "Self-Study",
                        onClick = { onSelectCustomSubject(subject) }
                    )
                }
            }

            // Other Exam Categories
            item {
                Text(
                    text = "Upcoming Exam Curriculums",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextSecondary,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            // Coming Soon
            item {
                ExamCardItem(
                    examName = "UPSC — Civil Services",
                    subtitle = "10 Subjects",
                    badgeText = "Coming Soon",
                    badgeColor = SageGreen,
                    isActive = false,
                    testTag = "test_exam_card_upsc",
                    onClick = {}
                )
            }
            item {
                ExamCardItem(
                    examName = "SSC — CGL",
                    subtitle = "8 Subjects",
                    badgeText = "Coming Soon",
                    badgeColor = SageGreen,
                    isActive = false,
                    testTag = "test_exam_card_ssc",
                    onClick = {}
                )
            }
            item {
                ExamCardItem(
                    examName = "IBPS — PO",
                    subtitle = "6 Subjects",
                    badgeText = "Coming Soon",
                    badgeColor = SageGreen,
                    isActive = false,
                    testTag = "test_exam_card_ibps",
                    onClick = {}
                )
            }
            item {
                ExamCardItem(
                    examName = "CAT — MBA Entrance",
                    subtitle = "10 Subjects",
                    badgeText = "Coming Soon",
                    badgeColor = SageGreen,
                    isActive = false,
                    testTag = "test_exam_card_cat",
                    onClick = {}
                )
            }
            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}

// ==========================================
// 2. PAGE E — SUBJECTS GRID (TEST)
// ==========================================
@Composable
fun TestSubjectsGridScreen(
    examName: String = "UPSI — Sub Inspector",
    customSubjects: List<String> = emptyList(),
    onSelectSubject: (String) -> Unit,
    onAddSubject: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val combinedSubjects = remember(customSubjects) {
        val existingNames = defaultSubjects.map { it.name.lowercase() }.toSet()
        val customModels = customSubjects
            .filter { !existingNames.contains(it.lowercase()) }
            .map { name ->
                SubjectUiModel(
                    name = name,
                    icon = Icons.Outlined.Psychology,
                    progress = 0.65f,
                    percentageText = "65%",
                    questionsDueText = "Ready to Test"
                )
            }
        defaultSubjects + customModels
    }

    val isDark = LocalIsDarkTheme.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDark) Color(0xFFF8FAFC) else DeepIndigo
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Test: $examName",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onAddSubject,
                modifier = Modifier.testTag("test_grid_add_subject_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject", tint = if (isDark) Color(0xFF34D399) else SageGreen)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(combinedSubjects) { subject ->
                TestSubjectCard(
                    subject = subject,
                    onClick = { onSelectSubject(subject.name) }
                )
            }

            item {
                val addBg = if (isDark) Color(0xFF1E293B) else Color(0xFFF3F5F9)
                val addBorder = if (isDark) Color(0xFF334155) else DeepIndigo.copy(alpha = 0.2f)
                val iconBg = if (isDark) Color(0xFF133322) else SageGreenLight
                val iconTint = if (isDark) Color(0xFF34D399) else SageGreen

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable { onAddSubject() }
                        .testTag("test_add_subject_grid_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = addBg),
                    border = BorderStroke(1.5.dp, addBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "+ Add Subject",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Any topic to test",
                            fontSize = 11.sp,
                            color = AppTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TestSubjectCard(
    subject: SubjectUiModel,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    InteractiveCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .heightIn(min = 150.dp)
            .testTag("test_subject_${subject.name.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val iconBg = if (isDark) Color(0xFF133322) else SageGreenLight
            val iconTint = if (isDark) Color(0xFF34D399) else SageGreen

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.TrackChanges,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = subject.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary,
                maxLines = 2
            )

            val badgeBg = if (isDark) Color(0xFF133322) else SageGreenLight
            val badgeTextColor = if (isDark) Color(0xFF34D399) else SageGreen

            // Questions Due Badge in Sage Green
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBg)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = subject.questionsDueText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeTextColor
                )
            }
        }
    }
}

// ==========================================
// 3. PAGE F — SUBJECT DETAIL (TEST)
// ==========================================
@Composable
fun TestSubjectDetailScreen(
    subjectName: String = "Indian Polity",
    allQuestions: List<QuestionEntity> = emptyList(),
    customChapters: List<String> = emptyList(),
    subjectStats: SubjectDetailedStats? = null,
    onTestFullSubject: () -> Unit,
    onSelectChapter: (String) -> Unit,
    onAddChapter: ((String) -> Unit)? = null,
    onViewStats: (() -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddChapterDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }

    if (showAddChapterDialog) {
        AddChapterDialog(
            subjectName = subjectName,
            onDismiss = { showAddChapterDialog = false },
            onConfirm = { chapterName ->
                showAddChapterDialog = false
                onAddChapter?.invoke(chapterName)
            }
        )
    }

    if (showStatsDialog && subjectStats != null) {
        SubjectDetailedStatsDialog(
            stats = subjectStats,
            onDismiss = { showStatsDialog = false }
        )
    }

    val subjectQuestions = remember(allQuestions, subjectName) {
        val isReelSubject = subjectName.equals("Reels", ignoreCase = true) ||
            subjectName.equals("Reels Concepts", ignoreCase = true) ||
            subjectName.contains("Reel", ignoreCase = true)
        val filtered = if (isReelSubject) {
            allQuestions.filter { it.sourceType.equals("reel", ignoreCase = true) || it.subjectName.contains("Reel", ignoreCase = true) }
        } else {
            allQuestions.filter { it.subjectName.equals(subjectName, ignoreCase = true) }
        }
        if (filtered.isNotEmpty()) filtered
        else if (subjectName.equals("Indian Polity", ignoreCase = true)) allQuestions
        else emptyList()
    }
    val totalQuestionsCount = subjectQuestions.size

    val chaptersList = remember(subjectQuestions, customChapters, subjectName) {
        val chapterNamesFromQuestions = subjectQuestions
            .map { it.chapterName.trim() }
            .filter { it.isNotBlank() }

        val allUniqueChapterNames = (chapterNamesFromQuestions + customChapters).distinct()

        if (allUniqueChapterNames.isNotEmpty()) {
            val sortedNames = allUniqueChapterNames.sortedWith { a, b ->
                val numA = a.substringBefore(".").trim().toIntOrNull()
                val numB = b.substringBefore(".").trim().toIntOrNull()
                if (numA != null && numB != null) numA.compareTo(numB)
                else a.compareTo(b)
            }
            sortedNames.mapIndexed { index, fullName ->
                val cleanName = fullName.substringAfter(".").trim().ifEmpty { fullName }
                val chNumber = fullName.substringBefore(".").trim().toIntOrNull() ?: (index + 1)
                val qCount = subjectQuestions.count {
                    it.chapterName.equals(fullName, ignoreCase = true) || it.chapterName.contains(cleanName, ignoreCase = true)
                }
                val masteredQuestions = subjectQuestions.count {
                    (it.chapterName.equals(fullName, ignoreCase = true) || it.chapterName.contains(cleanName, ignoreCase = true)) &&
                    (it.timesShown - it.timesWrong) > it.timesWrong
                }
                val mastery = if (qCount > 0) {
                    ((masteredQuestions.toFloat() / qCount) * 100).toInt().coerceIn(20, 95)
                } else 70

                ChapterUiModel(
                    number = chNumber,
                    name = fullName,
                    notesCount = 0,
                    questionsCount = qCount,
                    masteryPercent = mastery
                )
            }
        } else if (subjectName.equals("Indian Polity", ignoreCase = true)) {
            defaultChapters
        } else {
            emptyList()
        }
    }

    val isDark = LocalIsDarkTheme.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDark) Color(0xFFF8FAFC) else DeepIndigo
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = subjectName,
                fontSize = if (subjectName.length > 20) 18.sp else 21.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 24.sp
            )
            // Stats button
            if (subjectStats != null || onViewStats != null) {
                IconButton(
                    onClick = {
                        if (subjectStats != null) showStatsDialog = true
                        else onViewStats?.invoke()
                    },
                    modifier = Modifier.testTag("test_subject_stats_button")
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = "Subject Stats", tint = if (isDark) Color(0xFF818CF8) else DeepIndigo)
                }
            }
            // Add chapter button
            IconButton(
                onClick = { showAddChapterDialog = true },
                modifier = Modifier.testTag("test_add_chapter_top_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Chapter", tint = if (isDark) Color(0xFF34D399) else SageGreen)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Sage Green "Test Full Subject" button with target icon
        MindLoopPrimaryButton(
            onClick = onTestFullSubject,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("test_full_subject_button"),
            containerColor = if (isDark) Color(0xFF059669) else SageGreen,
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.TrackChanges,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Test Full Subject ($totalQuestionsCount Questions)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chapters to Test",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextSecondary
            )
            TextButton(
                onClick = { showAddChapterDialog = true },
                modifier = Modifier.testTag("test_add_chapter_text_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = if (isDark) Color(0xFF34D399) else SageGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("+ Add Chapter", color = if (isDark) Color(0xFF34D399) else SageGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (chaptersList.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                border = BorderStroke(1.dp, AppBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val emptyIconBg = if (isDark) Color(0xFF133322) else SageGreenLight
                    val emptyIconTint = if (isDark) Color(0xFF34D399) else SageGreen

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(emptyIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            contentDescription = null,
                            tint = emptyIconTint,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = "Ready to Test $subjectName",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "No questions have been logged for this subject yet. You can add a chapter or start testing the full subject directly.",
                        fontSize = 13.sp,
                        color = AppTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onTestFullSubject,
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF059669) else SageGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Test Anyway", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chaptersList) { chapter ->
                    TestChapterRowItem(
                        chapter = chapter,
                        onClick = { onSelectChapter(chapter.name) }
                    )
                }
                item {
                    OutlinedButton(
                        onClick = { showAddChapterDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("test_add_another_chapter_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, (if (isDark) Color(0xFF34D399) else SageGreen).copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = if (isDark) Color(0xFF34D399) else SageGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Add Another Chapter", color = if (isDark) Color(0xFF34D399) else SageGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun TestChapterRowItem(
    chapter: ChapterUiModel,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    InteractiveCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("test_chapter_${chapter.number}"),
        shape = RoundedCornerShape(14.dp),
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconBg = if (isDark) Color(0xFF133322) else SageGreenLight
            val iconTint = if (isDark) Color(0xFF34D399) else SageGreen

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.TrackChanges,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${chapter.number}. ${chapter.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${chapter.questionsCount} Total • 5 Due Today",
                    fontSize = 12.5.sp,
                    color = AppTextSecondary
                )
            }

            val badgeBg = if (isDark) Color(0xFF133322) else SageGreenLight
            val badgeTextColor = if (isDark) Color(0xFF34D399) else SageGreen

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${chapter.masteryPercent}% Acc.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeTextColor
                )
            }
        }
    }
}

// ==========================================
// 4. QUESTION REVIEW SCREEN (FLASHCARDS)
// ==========================================
@Composable
fun QuestionReviewScreen(
    chapterName: String,
    subjectName: String,
    questions: List<QuestionEntity>,
    onViewSourceNote: (noteId: Long?) -> Unit,
    onViewSourceReel: (reelId: Long) -> Unit = {},
    isReelTest: Boolean = false,
    onRecordAttempt: (questionId: Long, isCorrect: Boolean, rating: String, timeSpentSec: Long) -> Unit,
    onLogQuestionAttempt: (
        attemptId: String,
        questionId: Long,
        noteId: Long?,
        subject: String,
        chapter: String,
        questionType: String,
        shownAt: Long,
        answeredAt: Long,
        timeTakenSeconds: Long,
        selectedAnswer: String,
        isCorrect: Boolean,
        selfRating: String?
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onUpdateAttemptRating: (attemptId: String, questionId: Long, rating: String) -> Unit = { _, _, _ -> },
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = questions.getOrNull(currentIndex) ?: questions.firstOrNull()

    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    // Timestamps and identifiers for real-time attempt logging
    var currentAttemptId by remember { mutableStateOf("") }
    var questionShownAt by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var questionAnsweredAt by remember { mutableLongStateOf(0L) }
    var computedTimeTakenSecs by remember { mutableLongStateOf(0L) }

    // Timer per question
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var timerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(currentQuestion) {
        selectedAnswerIndex = null
        isSubmitted = false
        elapsedSeconds = 0L
        timerRunning = true
        currentAttemptId = java.util.UUID.randomUUID().toString()
        questionShownAt = System.currentTimeMillis()
        questionAnsweredAt = 0L
        computedTimeTakenSecs = 0L
    }

    LaunchedEffect(timerRunning) {
        while (timerRunning) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    // Function to handle immediate answer logging
    val onOptionSelected: (Int, String) -> Unit = { idx, answerText ->
        if (!isSubmitted && currentQuestion != null) {
            selectedAnswerIndex = idx
            isSubmitted = true
            timerRunning = false
            val now = System.currentTimeMillis()
            questionAnsweredAt = now
            val timeTaken = ((now - questionShownAt) / 1000L).coerceAtLeast(elapsedSeconds)
            computedTimeTakenSecs = timeTaken
            val isCorrect = idx == currentQuestion.correctAnswerIndex

            // Write row immediately to question_attempts log (self_rating is null until tapped)
            onLogQuestionAttempt(
                currentAttemptId,
                currentQuestion.id,
                currentQuestion.linkedNoteId,
                currentQuestion.subjectName.ifBlank { subjectName },
                currentQuestion.chapterName.ifBlank { chapterName },
                currentQuestion.questionType,
                questionShownAt,
                questionAnsweredAt,
                timeTaken,
                answerText,
                isCorrect,
                null
            )
        }
    }

    val timerFormatted = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)
    val isDark = LocalIsDarkTheme.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = if (isDark) Color(0xFFF8FAFC) else DeepIndigo)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Reviewing: $chapterName",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${currentIndex + 1}/${questions.size.coerceAtLeast(1)} due today",
                    fontSize = 12.sp,
                    color = AppTextSecondary
                )
            }

            // Stopwatch pill
            val timerPillBg = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
            val timerPillTint = if (isDark) Color(0xFFF8FAFC) else DeepIndigo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(timerPillBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = timerPillTint, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(timerFormatted, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = timerPillTint)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (currentQuestion == null) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("All caught up! No more due questions.", fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF34D399) else SageGreen)
            }
            return
        }

        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("question_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            border = BorderStroke(1.dp, AppBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Question Type & Spaced Repetition (SRS) Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val qTypeBg = if (currentQuestion.questionType == "TRUE_FALSE") {
                        if (isDark) Color(0xFF133322) else SageGreenLight
                    } else {
                        if (isDark) Color(0xFF1E1B4B).copy(alpha = 0.5f) else DeepIndigo.copy(alpha = 0.08f)
                    }
                    val qTypeColor = if (currentQuestion.questionType == "TRUE_FALSE") {
                        if (isDark) Color(0xFF34D399) else SageGreen
                    } else {
                        if (isDark) Color(0xFF818CF8) else DeepIndigo
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(qTypeBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (currentQuestion.questionType == "TRUE_FALSE") "True / False" else "Multiple Choice",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = qTypeColor
                        )
                    }

                    // SRS Urgency Badge
                    val isCritical = currentQuestion.timesWrong >= 2 || (currentQuestion.timesWrong >= 1 && currentQuestion.lastRating == "HARD")
                    val isHard = currentQuestion.timesWrong == 1 || currentQuestion.lastRating == "HARD"
                    val (srsBadge, srsBg, srsColor) = when {
                        isCritical -> Triple(
                            "SRS: Critical Repeat",
                            if (isDark) Color(0xFF3B151E) else Terracotta.copy(alpha = 0.15f),
                            if (isDark) Color(0xFFF87171) else Terracotta
                        )
                        isHard -> Triple(
                            "SRS: High Difficulty",
                            if (isDark) Color(0xFF3B2D13) else Amber.copy(alpha = 0.18f),
                            if (isDark) Color(0xFFFBBF24) else Amber
                        )
                        currentQuestion.isDue -> Triple(
                            "SRS: Due for Review",
                            if (isDark) Color(0xFF1E293B) else DeepIndigo.copy(alpha = 0.10f),
                            if (isDark) Color(0xFF94A3B8) else DeepIndigo
                        )
                        currentQuestion.totalAttempts == 0 -> Triple(
                            "SRS: New Concept",
                            if (isDark) Color(0xFF1E293B) else Color(0xFF64748B).copy(alpha = 0.12f),
                            if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                        else -> Triple(
                            "SRS: Mastered",
                            if (isDark) Color(0xFF133322) else SageGreenLight,
                            if (isDark) Color(0xFF34D399) else SageGreen
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(srsBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = srsBadge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = srsColor
                        )
                    }
                }

                if (currentQuestion.timesWrong > 0 || currentQuestion.lastRating == "HARD") {
                    Spacer(modifier = Modifier.height(8.dp))
                    val warnColor = if (currentQuestion.timesWrong >= 2) {
                        if (isDark) Color(0xFFF87171) else Terracotta
                    } else {
                        if (isDark) Color(0xFFFBBF24) else Amber
                    }
                    Text(
                        text = if (currentQuestion.timesWrong > 0)
                            "Prioritized by Spaced Repetition: ${currentQuestion.timesWrong} mistake${if (currentQuestion.timesWrong > 1) "s" else ""} on record"
                        else
                            "Prioritized by Spaced Repetition: Previously rated Hard",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = warnColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentQuestion.questionText,
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Answer options
        if (currentQuestion.questionType == "TRUE_FALSE") {
            // 2 large buttons: True / False
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(0 to "True", 1 to "False").forEach { (idx, label) ->
                    val isSelected = selectedAnswerIndex == idx
                    val isCorrect = isSubmitted && idx == currentQuestion.correctAnswerIndex
                    val isWrongSelected = isSubmitted && isSelected && idx != currentQuestion.correctAnswerIndex

                    val bg = when {
                        isCorrect -> if (isDark) Color(0xFF133322) else SageGreenLight
                        isWrongSelected -> if (isDark) Color(0xFF3B151E) else TerracottaLight
                        isSelected -> if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        else -> AppSurface
                    }
                    val borderC = when {
                        isCorrect -> if (isDark) Color(0xFF34D399) else SageGreen
                        isWrongSelected -> if (isDark) Color(0xFFF87171) else Terracotta
                        isSelected -> if (isDark) Color(0xFF818CF8) else DeepIndigo
                        else -> AppBorder
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .tapAffordance(shape = RoundedCornerShape(14.dp), elevation = 3.dp)
                            .clickable {
                                onOptionSelected(idx, label)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = bg),
                        border = BorderStroke(1.5.dp, borderC)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTextPrimary)
                        }
                    }
                }
            }
        } else {
            // 4 Multiple Choice buttons (A, B, C, D)
            val options = listOf(
                0 to currentQuestion.optionA,
                1 to currentQuestion.optionB,
                2 to currentQuestion.optionC,
                3 to currentQuestion.optionD
            ).filter { it.second.isNotBlank() }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { (idx, optText) ->
                    val isSelected = selectedAnswerIndex == idx
                    val isCorrect = isSubmitted && idx == currentQuestion.correctAnswerIndex
                    val isWrongSelected = isSubmitted && isSelected && idx != currentQuestion.correctAnswerIndex

                    val bg = when {
                        isCorrect -> if (isDark) Color(0xFF133322) else SageGreenLight
                        isWrongSelected -> if (isDark) Color(0xFF3B151E) else TerracottaLight
                        isSelected -> if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        else -> AppSurface
                    }
                    val borderC = when {
                        isCorrect -> if (isDark) Color(0xFF34D399) else SageGreen
                        isWrongSelected -> if (isDark) Color(0xFFF87171) else Terracotta
                        isSelected -> if (isDark) Color(0xFF818CF8) else DeepIndigo
                        else -> AppBorder
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tapAffordance(shape = RoundedCornerShape(12.dp), elevation = 2.5.dp)
                            .clickable {
                                onOptionSelected(idx, optText)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = bg),
                        border = BorderStroke(1.5.dp, borderC)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val badgeBg = if (isSelected || isCorrect) {
                                if (isDark) Color(0xFF6366F1) else DeepIndigo
                            } else {
                                if (isDark) Color(0xFF334155) else Color(0xFFEEF2F6)
                            }
                            val badgeTextColor = if (isSelected || isCorrect) {
                                Color.White
                            } else {
                                if (isDark) Color(0xFFF8FAFC) else DeepIndigo
                            }

                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(badgeBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A' + idx).toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = optText,
                                fontSize = 14.5.sp,
                                color = AppTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // "🔗 View Source Reel" or "🔗 View Source Note" secondary button
        val sourceBtnTint = if (isDark) Color(0xFF818CF8) else DeepIndigo
        if (isReelTest || currentQuestion.sourceType.equals("reel", ignoreCase = true) ||
            (currentQuestion.sourceId.isNotBlank() && currentQuestion.linkedNoteId == null)
        ) {
            val reelId = currentQuestion.sourceId.toLongOrNull() ?: 1L
            MindLoopSecondaryButton(
                onClick = { onViewSourceReel(reelId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("view_source_reel_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = sourceBtnTint, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Source Reel", color = sourceBtnTint, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        } else {
            MindLoopSecondaryButton(
                onClick = { onViewSourceNote(currentQuestion.linkedNoteId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("view_source_note_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = null, tint = sourceBtnTint, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Source Note", color = sourceBtnTint, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Spaced Repetition Dynamic Next Review Projection
        val isAnswerCorrect = selectedAnswerIndex == currentQuestion.correctAnswerIndex
        val effectiveMistakes = currentQuestion.timesWrong + (if (isAnswerCorrect) 0 else 1)
        val (easyProj, medProj, hardProj) = remember(currentQuestion, isAnswerCorrect) {
            SpacedRepetitionScheduler.getProjectionsForQuestion(
                question = currentQuestion,
                isCurrentAnswerCorrect = isAnswerCorrect
            )
        }

        // Spaced Repetition Rating Prompt
        Text(
            text = "Select recall difficulty to schedule next review:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Selectable Rating Buttons: Easy / Medium / Hard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Easy Button (Sage Green)
            MindLoopPrimaryButton(
                onClick = {
                    val correct = selectedAnswerIndex == currentQuestion.correctAnswerIndex
                    val finalTime = if (computedTimeTakenSecs > 0) computedTimeTakenSecs else elapsedSeconds.toLong()
                    if (currentAttemptId.isNotBlank()) {
                        onUpdateAttemptRating(currentAttemptId, currentQuestion.id, "EASY")
                    }
                    onRecordAttempt(currentQuestion.id, correct, "EASY", finalTime)
                    if (currentIndex < questions.size - 1) currentIndex++ else onBack()
                },
                modifier = Modifier.weight(1f).height(58.dp).testTag("rating_easy"),
                shape = RoundedCornerShape(12.dp),
                containerColor = if (isDark) Color(0xFF059669) else SageGreen,
                elevation = 4.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Easy", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                    Text(easyProj.relativeTimeText, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.92f))
                }
            }

            // Medium Button (Amber)
            MindLoopPrimaryButton(
                onClick = {
                    val correct = selectedAnswerIndex == currentQuestion.correctAnswerIndex
                    val finalTime = if (computedTimeTakenSecs > 0) computedTimeTakenSecs else elapsedSeconds.toLong()
                    if (currentAttemptId.isNotBlank()) {
                        onUpdateAttemptRating(currentAttemptId, currentQuestion.id, "MEDIUM")
                    }
                    onRecordAttempt(currentQuestion.id, correct, "MEDIUM", finalTime)
                    if (currentIndex < questions.size - 1) currentIndex++ else onBack()
                },
                modifier = Modifier.weight(1f).height(58.dp).testTag("rating_medium"),
                shape = RoundedCornerShape(12.dp),
                containerColor = if (isDark) Color(0xFFD97706) else Amber,
                elevation = 4.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Medium", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                    Text(medProj.relativeTimeText, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.92f))
                }
            }

            // Hard Button (Terracotta)
            MindLoopPrimaryButton(
                onClick = {
                    val correct = selectedAnswerIndex == currentQuestion.correctAnswerIndex
                    val finalTime = if (computedTimeTakenSecs > 0) computedTimeTakenSecs else elapsedSeconds.toLong()
                    if (currentAttemptId.isNotBlank()) {
                        onUpdateAttemptRating(currentAttemptId, currentQuestion.id, "HARD")
                    }
                    onRecordAttempt(currentQuestion.id, correct, "HARD", finalTime)
                    if (currentIndex < questions.size - 1) currentIndex++ else onBack()
                },
                modifier = Modifier.weight(1f).height(58.dp).testTag("rating_hard"),
                shape = RoundedCornerShape(12.dp),
                containerColor = if (isDark) Color(0xFFDC2626) else Terracotta,
                elevation = 4.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hard", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                    Text(hardProj.relativeTimeText, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.92f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Review Schedule (SRS) Details Card placed below buttons
        val srsSurfaceBg = if (isDark) Color(0xFF1E293B) else DeepIndigo.copy(alpha = 0.04f)
        val srsSurfaceBorder = if (isDark) Color(0xFF334155) else DeepIndigo.copy(alpha = 0.12f)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = srsSurfaceBg,
            border = BorderStroke(1.dp, srsSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF818CF8) else DeepIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Next Review Schedule (SRS)",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTextPrimary
                        )
                    }

                    val badgeBg = if (effectiveMistakes > 0) {
                        if (isDark) Color(0xFF3B151E) else Terracotta.copy(alpha = 0.12f)
                    } else {
                        if (isDark) Color(0xFF133322) else SageGreenLight
                    }
                    val badgeColor = if (effectiveMistakes > 0) {
                        if (isDark) Color(0xFFF87171) else Terracotta
                    } else {
                        if (isDark) Color(0xFF34D399) else SageGreen
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (effectiveMistakes > 0) "$effectiveMistakes Mistake${if (effectiveMistakes > 1) "s" else ""} on record" else "0 Mistakes",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (effectiveMistakes > 0) {
                        "Next repetition interval is shortened based on your $effectiveMistakes past mistake${if (effectiveMistakes > 1) "s" else ""}:"
                    } else {
                        "Intervals expand progressively as recall confidence increases:"
                    },
                    fontSize = 11.5.sp,
                    color = AppTextSecondary,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3 mini schedule cards displaying exact next review intervals & calendar days
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SrsSchedulePill(
                        label = "Easy",
                        badge = easyProj.intervalBadge,
                        dayText = easyProj.scheduledDayName,
                        color = if (isDark) Color(0xFF34D399) else SageGreen,
                        modifier = Modifier.weight(1f)
                    )
                    SrsSchedulePill(
                        label = "Medium",
                        badge = medProj.intervalBadge,
                        dayText = medProj.scheduledDayName,
                        color = if (isDark) Color(0xFFFBBF24) else Amber,
                        modifier = Modifier.weight(1f)
                    )
                    SrsSchedulePill(
                        label = "Hard",
                        badge = hardProj.intervalBadge,
                        dayText = hardProj.scheduledDayName,
                        color = if (isDark) Color(0xFFF87171) else Terracotta,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SrsSchedulePill(
    label: String,
    badge: String,
    dayText: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "+$badge",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AppTextPrimary
            )
            Text(
                text = dayText,
                fontSize = 9.5.sp,
                color = AppTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
