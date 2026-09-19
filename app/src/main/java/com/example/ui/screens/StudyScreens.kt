package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Psychology
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.util.ImageStorageHelper
import com.example.ui.viewmodel.SubjectDetailedStats
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionEntity
import com.example.ui.components.HandwrittenNoteViewer
import com.example.ui.components.InteractiveCard
import com.example.ui.components.InteractiveCardBorder
import com.example.ui.components.MindLoopPrimaryButton
import com.example.ui.components.MindLoopSecondaryButton
import com.example.ui.components.StaticCardBorder
import com.example.ui.components.tapAffordance
import com.example.ui.theme.Amber
import com.example.ui.theme.AmberLight
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.DeepIndigoLight
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

// ==========================================
// 0. DIALOGS FOR CUSTOM SUBJECTS & CHAPTERS
// ==========================================
@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (subjectName: String, initialChapter: String?) -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var initialChapter by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Independent Subject",
                fontWeight = FontWeight.Bold,
                color = DeepIndigo,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Add any subject you want to learn (e.g. Psychology, Philosophy, Sociology). You can add chapters, study notes, take tests, and track your stats.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Subject Name (e.g. Psychology)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_subject_name")
                )
                OutlinedTextField(
                    value = initialChapter,
                    onValueChange = { initialChapter = it },
                    label = { Text("First Chapter Name (Optional)") },
                    placeholder = { Text("e.g. 1. Introduction & Research Methods") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_chapter_name")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subjectName.isNotBlank()) {
                        onConfirm(subjectName.trim(), initialChapter.trim().ifBlank { null })
                    }
                },
                enabled = subjectName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_add_subject_button")
            ) {
                Text("Add Subject")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun AddChapterDialog(
    subjectName: String,
    onDismiss: () -> Unit,
    onConfirm: (chapterName: String) -> Unit
) {
    var chapterName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Chapter to $subjectName",
                fontWeight = FontWeight.Bold,
                color = DeepIndigo,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Enter the chapter title for $subjectName:",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = chapterName,
                    onValueChange = { chapterName = it },
                    label = { Text("Chapter Title (e.g. Memory & Cognition)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_add_chapter_title")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (chapterName.isNotBlank()) {
                        onConfirm(chapterName.trim())
                    }
                },
                enabled = chapterName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_add_chapter_button")
            ) {
                Text("Add Chapter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun SubjectDetailedStatsDialog(
    stats: SubjectDetailedStats,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = DeepIndigo)
                Text(
                    text = "${stats.subjectName} Analytics",
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SageGreenLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Accuracy", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text("${stats.accuracyPercent}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SageGreen)
                            Text("${stats.correctAttempts}/${stats.totalAttempts} correct", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = AmberLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Study Time", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text("${stats.totalStudyTimeMinutes}m", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Amber)
                            Text("${stats.notesCount} notes saved", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F8FC)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatRowItem("Chapters Created", "${stats.chaptersCount}")
                        StatRowItem("Notes in Vault", "${stats.notesCount}")
                        StatRowItem("Questions in Bank", "${stats.questionsCount}")
                        StatRowItem("Total Test Attempts", "${stats.totalAttempts}")
                        StatRowItem("Mistakes Logged", "${stats.mistakesCount}", isHighlight = stats.mistakesCount > 0)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close")
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun StatRowItem(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) Terracotta else DeepIndigo
        )
    }
}

@Composable
fun CustomSubjectCardItem(
    subjectName: String,
    subtitle: String,
    badgeText: String = "Self-Study",
    badgeColor: Color = SageGreen,
    testTag: String = "custom_subject_card_${subjectName.lowercase().replace(" ", "_")}",
    onClick: () -> Unit
) {
    InteractiveCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(DeepIndigo.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    tint = DeepIndigo,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subjectName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SageGreenLight)
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeColor
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = DeepIndigo
            )
        }
    }
}

// ==========================================
// 1. PAGE A — EXAM LIST (STUDY)
// ==========================================
@Composable
fun StudyExamListScreen(
    onSelectExam: (String) -> Unit,
    onSelectSubject: (String) -> Unit = {},
    onAddNotesClick: () -> Unit,
    onAddNewSubject: ((String, String?) -> Unit)? = null,
    customSubjects: List<String> = emptyList(),
    allNotes: List<NoteEntity> = emptyList(),
    allQuestions: List<QuestionEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    var showAddSubjectDialog by remember { mutableStateOf(false) }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { subj, initialChap ->
                showAddSubjectDialog = false
                if (onAddNewSubject != null) {
                    onAddNewSubject(subj, initialChap)
                }
                onSelectSubject(subj)
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Study",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                IconButton(onClick = { /* Search */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DeepIndigo
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section 1: Active Exam Card
                item {
                    Text(
                        text = "Enrolled Exam Course",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }

                item {
                    ExamCardItem(
                        examName = "UPSI — Sub Inspector",
                        subtitle = "6 Subjects, 120 Topics",
                        badgeText = "85%",
                        badgeColor = SageGreen,
                        isActive = true,
                        testTag = "exam_card_upsi",
                        onClick = { onSelectExam("UPSI") }
                    )
                }

                // Section 2: Self-Study & Custom Subjects
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Self-Study & Independent Subjects",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        TextButton(
                            onClick = { showAddSubjectDialog = true },
                            modifier = Modifier.testTag("add_custom_subject_study_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = SageGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("+ Add Subject", color = SageGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                val activeCustomSubjects = if (customSubjects.isNotEmpty()) customSubjects else listOf("Psychology")
                items(activeCustomSubjects) { subj ->
                    val notesCount = allNotes.count { it.subjectName.equals(subj, ignoreCase = true) }.let { if (it > 0) it else if (subj.equals("Psychology", ignoreCase = true)) 8 else 0 }
                    val questionsCount = allQuestions.count { it.subjectName.equals(subj, ignoreCase = true) }.let { if (it > 0) it else if (subj.equals("Psychology", ignoreCase = true)) 8 else 0 }
                    CustomSubjectCardItem(
                        subjectName = subj,
                        subtitle = "$notesCount Notes • $questionsCount Test Questions",
                        badgeText = "Self-Study",
                        badgeColor = SageGreen,
                        onClick = { onSelectSubject(subj) }
                    )
                }

                // Section 3: Upcoming Exam Courses
                item {
                    Text(
                        text = "Upcoming Exam Courses",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    ExamCardItem(
                        examName = "UPSC — Civil Services",
                        subtitle = "10 Subjects",
                        badgeText = "Coming Soon",
                        badgeColor = SageGreen,
                        isActive = false,
                        testTag = "exam_card_upsc",
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
                        testTag = "exam_card_ssc",
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
                        testTag = "exam_card_cat",
                        onClick = {}
                    )
                }
                item {
                    ExamCardItem(
                        examName = "GATE — Engineering",
                        subtitle = "10 Subjects",
                        badgeText = "Coming Soon",
                        badgeColor = SageGreen,
                        isActive = false,
                        testTag = "exam_card_gate",
                        onClick = {}
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // Floating Action Button to Add Notes
        FloatingActionButton(
            onClick = onAddNotesClick,
            containerColor = DeepIndigo,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("fab_add_notes")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Notes")
        }
    }
}

@Composable
fun ExamCardItem(
    examName: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    isActive: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val cardModifier = if (isActive) {
        Modifier
            .fillMaxWidth()
            .tapAffordance(shape = RoundedCornerShape(16.dp), elevation = 5.dp)
            .testTag(testTag)
            .clickable(onClick = onClick)
    } else {
        Modifier
            .fillMaxWidth()
            .testTag(testTag)
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, if (isActive) InteractiveCardBorder else StaticCardBorder),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 5.dp else 1.dp,
            pressedElevation = 1.5.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exam Icon Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isActive) DeepIndigo.copy(alpha = 0.08f) else Color(0xFFF1F4F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = if (isActive) DeepIndigo else Color(0xFF9AA7BA),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = examName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) DeepIndigo else Color(0xFF7A8699)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = if (isActive) TextSecondary else Color(0xFFA0ACBE)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isActive) SageGreenLight else Color(0xFFE2EBE5))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive) SageGreen else Color(0xFF5E866D)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isActive) DeepIndigo else Color(0xFFB5BFCE)
            )
        }
    }
}

// ==========================================
// 2. PAGE B — SUBJECTS GRID (STUDY)
// ==========================================
data class SubjectUiModel(
    val name: String,
    val icon: ImageVector,
    val progress: Float,
    val percentageText: String,
    val questionsDueText: String
)

val defaultSubjects = listOf(
    SubjectUiModel("Indian Polity", Icons.Outlined.Description, 0.90f, "90%", "12 Due"),
    SubjectUiModel("Reels Concepts", Icons.Default.Movie, 0.70f, "70%", "8 Due"),
    SubjectUiModel("Geography", Icons.Default.Public, 0.45f, "45%", "4 Due"),
    SubjectUiModel("History", Icons.Default.Schedule, 0.40f, "40%", "18 Due"),
    SubjectUiModel("Current Affairs", Icons.Outlined.Newspaper, 0.50f, "50%", "5 Due"),
    SubjectUiModel("Economics", Icons.Default.BarChart, 0.50f, "50%", "21 Due"),
    SubjectUiModel("General Science", Icons.Default.Biotech, 0.50f, "50%", "3 Due")
)

@Composable
fun StudySubjectsGridScreen(
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
                    questionsDueText = "Active"
                )
            }
        defaultSubjects + customModels
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Back button and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepIndigo
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = examName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onAddSubject,
                modifier = Modifier.testTag("add_subject_grid_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject",
                    tint = SageGreen
                )
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
                StudySubjectCard(
                    subject = subject,
                    onClick = { onSelectSubject(subject.name) }
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable { onAddSubject() }
                        .testTag("add_custom_subject_grid_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F5F9)),
                    border = BorderStroke(1.5.dp, DeepIndigo.copy(alpha = 0.2f))
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
                                .background(SageGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "+ Add Subject",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Any topic / domain",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudySubjectCard(
    subject: SubjectUiModel,
    onClick: () -> Unit
) {
    InteractiveCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .heightIn(min = 150.dp)
            .testTag("subject_card_${subject.name.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEEF2F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = subject.icon,
                    contentDescription = null,
                    tint = DeepIndigo,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = subject.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo,
                maxLines = 2
            )

            // Progress bar & Percentage
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE5E9F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(subject.progress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SageGreen)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subject.percentageText,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ==========================================
// 3. PAGE C — SUBJECT DETAIL (STUDY)
// ==========================================
data class ChapterUiModel(
    val number: Int,
    val name: String,
    val notesCount: Int,
    val questionsCount: Int,
    val masteryPercent: Int
)

val defaultChapters = listOf(
    ChapterUiModel(1, "Making of the Constitution", 10, 22, 88),
    ChapterUiModel(2, "Preamble", 8, 13, 82),
    ChapterUiModel(3, "Fundamental Rights", 11, 22, 55),
    ChapterUiModel(4, "Directive Principles", 9, 15, 48),
    ChapterUiModel(5, "Union Executive", 8, 14, 82),
    ChapterUiModel(6, "State Executive", 6, 11, 79)
)

@Composable
fun StudySubjectDetailScreen(
    subjectName: String = "Indian Polity",
    allNotes: List<NoteEntity> = emptyList(),
    allQuestions: List<QuestionEntity> = emptyList(),
    customChapters: List<String> = emptyList(),
    subjectStats: SubjectDetailedStats? = null,
    onStudyFullSubject: () -> Unit,
    onSelectChapter: (String) -> Unit,
    onAddChapter: ((String) -> Unit)? = null,
    onAddNote: (() -> Unit)? = null,
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

    val subjectNotes = remember(allNotes, subjectName) {
        val filtered = allNotes.filter { it.subjectName.equals(subjectName, ignoreCase = true) }
        if (filtered.isNotEmpty()) filtered
        else if (subjectName.equals("Indian Polity", ignoreCase = true)) allNotes
        else emptyList()
    }
    val totalNotesCount = subjectNotes.size

    val chaptersList = remember(subjectNotes, allQuestions, customChapters, subjectName) {
        val chapterNamesFromNotes = subjectNotes
            .map { it.chapterName.trim() }
            .filter { it.isNotBlank() }

        val chapterNamesFromQuestions = allQuestions
            .filter { it.subjectName.equals(subjectName, ignoreCase = true) }
            .map { it.chapterName.trim() }
            .filter { it.isNotBlank() }

        val allUniqueChapterNames = (chapterNamesFromNotes + chapterNamesFromQuestions + customChapters).distinct()

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
                val notesCount = subjectNotes.count {
                    it.chapterName.equals(fullName, ignoreCase = true) ||
                    it.chapterName.contains(cleanName, ignoreCase = true)
                }
                val questionsCount = allQuestions.count {
                    it.subjectName.equals(subjectName, ignoreCase = true) &&
                    (it.chapterName.equals(fullName, ignoreCase = true) || it.chapterName.contains(cleanName, ignoreCase = true))
                }
                val masteredQuestions = allQuestions.count {
                    it.subjectName.equals(subjectName, ignoreCase = true) &&
                    (it.chapterName.equals(fullName, ignoreCase = true) || it.chapterName.contains(cleanName, ignoreCase = true)) &&
                    (it.timesShown - it.timesWrong) > it.timesWrong
                }
                val mastery = if (questionsCount > 0) {
                    ((masteredQuestions.toFloat() / questionsCount) * 100).toInt().coerceIn(20, 95)
                } else if (notesCount > 0) 75 else 0

                ChapterUiModel(
                    number = chNumber,
                    name = fullName,
                    notesCount = notesCount,
                    questionsCount = questionsCount,
                    masteryPercent = mastery
                )
            }
        } else if (subjectName.equals("Indian Polity", ignoreCase = true)) {
            defaultChapters
        } else {
            emptyList()
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

        // Back bar & action buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepIndigo
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = subjectName,
                fontSize = if (subjectName.length > 20) 18.sp else 21.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo,
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
                    modifier = Modifier.testTag("subject_stats_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Subject Stats",
                        tint = DeepIndigo
                    )
                }
            }
            // Add chapter button
            IconButton(
                onClick = { showAddChapterDialog = true },
                modifier = Modifier.testTag("add_chapter_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Chapter",
                    tint = SageGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // "Study Full Subject" button
        MindLoopPrimaryButton(
            onClick = onStudyFullSubject,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("study_full_subject_button"),
            containerColor = DeepIndigo,
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Study Full Subject ($totalNotesCount Notes)",
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
                text = "Chapters",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            TextButton(
                onClick = { showAddChapterDialog = true },
                modifier = Modifier.testTag("add_chapter_text_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = SageGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("+ Add Chapter", color = SageGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (chaptersList.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SageGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Psychology,
                            contentDescription = null,
                            tint = SageGreen,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = "Start Learning $subjectName",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "No chapters have been created yet. Create a chapter to organize your handwritten notes and test questions.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { showAddChapterDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+ Add Chapter")
                        }
                        if (onAddNote != null) {
                            OutlinedButton(
                                onClick = onAddNote,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+ Add Note", color = DeepIndigo)
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chaptersList) { chapter ->
                    ChapterRowItem(
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
                            .testTag("add_chapter_bottom_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = SageGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Add Another Chapter", color = SageGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun ChapterRowItem(
    chapter: ChapterUiModel,
    onClick: () -> Unit
) {
    val badgeBg = when {
        chapter.masteryPercent >= 80 -> SageGreenLight
        chapter.masteryPercent >= 60 -> AmberLight
        else -> TerracottaLight
    }
    val badgeColor = when {
        chapter.masteryPercent >= 80 -> SageGreen
        chapter.masteryPercent >= 60 -> Amber
        else -> Terracotta
    }

    InteractiveCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chapter_item_${chapter.number}"),
        shape = RoundedCornerShape(14.dp),
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = "${chapter.number}. ${chapter.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${chapter.notesCount} Notes • ${chapter.questionsCount} Practice Questions",
                    fontSize = 12.5.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${chapter.masteryPercent}% Mastered",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeColor
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFB5BFCE)
            )
        }
    }
}

// ==========================================
// 4. STUDY NOTES FEED SCREEN (NOTE VIEWER)
// ==========================================
@Composable
fun StudyNotesFeedScreen(
    chapterName: String,
    subjectName: String,
    allNotes: List<NoteEntity>,
    onAddQuestionClick: (noteId: Long, chapterName: String) -> Unit,
    onBack: () -> Unit,
    onRecordTime: (noteId: Long, seconds: Long) -> Unit,
    initialNoteId: Long? = null,
    onStartSession: (sessionId: String, noteId: Long, subject: String, chapter: String, startedAt: Long, mode: String) -> Unit = { _, _, _, _, _, _ -> },
    onEndSession: (sessionId: String, endedAt: Long, durationSeconds: Long, noteId: Long) -> Unit = { _, _, _, _ -> },
    onSaveNote: (subject: String, chapter: String, title: String, content: String, imageUri: String?) -> Unit = { _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    // 1. Filter all notes belonging to this subject
    val subjectNotes = remember(allNotes, subjectName) {
        val filtered = allNotes.filter { it.subjectName.equals(subjectName, ignoreCase = true) }
        if (filtered.isNotEmpty()) filtered else allNotes
    }

    // 2. Discover and sort chapters for this subject
    val subjectChapters = remember(subjectNotes) {
        val found = subjectNotes.map { it.chapterName.trim() }.filter { it.isNotBlank() }.distinct()
        if (found.isNotEmpty()) {
            found.sortedWith { a, b ->
                val numA = a.substringBefore(".").trim().toIntOrNull()
                val numB = b.substringBefore(".").trim().toIntOrNull()
                if (numA != null && numB != null) numA.compareTo(numB) else a.compareTo(b)
            }
        } else {
            listOf(
                "1. Making of the Constitution",
                "2. Preamble",
                "3. Fundamental Rights",
                "4. Directive Principles",
                "5. Union Executive",
                "6. State Executive"
            )
        }
    }

    // 3. Find initial chapter index
    val initialChapterIndex = remember(subjectChapters, chapterName) {
        val cleanTarget = chapterName.substringAfter(".").trim().ifEmpty { chapterName.trim() }
        val idx = subjectChapters.indexOfFirst {
            it.equals(chapterName, ignoreCase = true) ||
            it.contains(cleanTarget, ignoreCase = true) ||
            cleanTarget.contains(it.substringAfter(".").trim(), ignoreCase = true)
        }
        if (idx >= 0) idx else 0
    }

    // Vertical Pager: Swiping up/down moves between Chapters (like TikTok / YouTube Shorts)
    // Strictly bounded to 0 until subjectChapters.size: When on the last chapter, scrolling up encounters the boundary (no extra pages)
    val verticalPagerState = rememberPagerState(
        initialPage = initialChapterIndex.coerceIn(0, (subjectChapters.size - 1).coerceAtLeast(0)),
        pageCount = { subjectChapters.size }
    )

    val currentChapter = subjectChapters.getOrNull(verticalPagerState.currentPage) ?: subjectChapters.firstOrNull() ?: chapterName
    val chNumber = currentChapter.substringBefore(".").trim().toIntOrNull() ?: (verticalPagerState.currentPage + 1)
    val cleanChapterName = currentChapter.substringAfter(".").trim().ifEmpty { currentChapter }
    val headerChapterTitle = "Chapter $chNumber: $cleanChapterName"

    // Active note state tracking
    var activeNote by remember { mutableStateOf<NoteEntity?>(null) }
    var activeNoteIndex by remember { mutableIntStateOf(0) }
    var activeChapterNotesCount by remember { mutableIntStateOf(1) }
    var targetNotePage by remember { mutableStateOf<Int?>(null) }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    // Live study stopwatch starting from 0 for current active session
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    // Real-time session lifecycle tracking per active note
    val currentSessionId = remember(activeNote?.id) {
        java.util.UUID.randomUUID().toString()
    }
    val sessionStartedAt = remember(currentSessionId) {
        System.currentTimeMillis()
    }

    LaunchedEffect(currentSessionId) {
        elapsedSeconds = 0L
        if (activeNote != null) {
            onStartSession(
                currentSessionId,
                activeNote!!.id,
                activeNote!!.subjectName.ifBlank { subjectName },
                activeNote!!.chapterName.ifBlank { currentChapter },
                sessionStartedAt,
                "chapter_wise"
            )
        }
    }

    DisposableEffect(currentSessionId) {
        onDispose {
            if (activeNote != null) {
                val endedAt = System.currentTimeMillis()
                val durationSecs = ((endedAt - sessionStartedAt) / 1000L).coerceAtLeast(elapsedSeconds)
                onEndSession(currentSessionId, endedAt, durationSecs, activeNote!!.id)
                onRecordTime(activeNote!!.id, durationSecs)
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, currentSessionId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_PAUSE) {
                if (activeNote != null) {
                    val now = System.currentTimeMillis()
                    val durationSecs = ((now - sessionStartedAt) / 1000L).coerceAtLeast(elapsedSeconds)
                    onEndSession(currentSessionId, now, durationSecs, activeNote!!.id)
                    onRecordTime(activeNote!!.id, durationSecs)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header: Back button + Chapter Name + Note Counter Badge with Nav Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (activeNote != null) {
                        val endedAt = System.currentTimeMillis()
                        val durationSecs = ((endedAt - sessionStartedAt) / 1000L).coerceAtLeast(elapsedSeconds)
                        onEndSession(currentSessionId, endedAt, durationSecs, activeNote!!.id)
                        onRecordTime(activeNote!!.id, durationSecs)
                    }
                    onBack()
                },
                modifier = Modifier.testTag("study_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepIndigo
                )
            }

            Text(
                text = headerChapterTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )

            // Prominent Note Counter & Slide Affordance in the top-right corner
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SageGreen.copy(alpha = 0.15f)),
                modifier = Modifier.testTag("chapter_note_counter_badge")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    if (activeChapterNotesCount > 1) {
                        IconButton(
                            onClick = {
                                if (activeNoteIndex > 0) targetNotePage = activeNoteIndex - 1
                            },
                            enabled = activeNoteIndex > 0,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Note",
                                tint = if (activeNoteIndex > 0) DeepIndigo else Color.LightGray,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Text(
                        text = "${activeNoteIndex + 1}/$activeChapterNotesCount Notes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    if (activeChapterNotesCount > 1) {
                        IconButton(
                            onClick = {
                                if (activeNoteIndex < activeChapterNotesCount - 1) targetNotePage = activeNoteIndex + 1
                            },
                            enabled = activeNoteIndex < activeChapterNotesCount - 1,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Note",
                                tint = if (activeNoteIndex < activeChapterNotesCount - 1) DeepIndigo else Color.LightGray,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Subtitle row: Progress Dots + Gesture Guide
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Instagram-like Progress Dots for notes in this active chapter
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dotsCount = activeChapterNotesCount.coerceAtMost(10).coerceAtLeast(1)
                repeat(dotsCount) { idx ->
                    val isCurrent = idx == activeNoteIndex
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 8.dp else 5.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) SageGreen else Color(0xFFD4DDD7))
                            .clickable {
                                targetNotePage = idx
                            }
                    )
                }
            }

            Text(
                text = "Slide ⇄ for notes • Scroll ⇅ for chapters",
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Vertical Pager: Swiping up/down moves to Next / Previous Chapter (TikTok / YouTube Shorts style)
        // Strictly bounded to subjectChapters.size so when at the last chapter there is no scroll-up motion
        VerticalPager(
            state = verticalPagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = true
        ) { chapterIdx ->
            val thisChapter = subjectChapters[chapterIdx]
            val thisCleanName = thisChapter.substringAfter(".").trim().ifEmpty { thisChapter }
            val chapterNotes = remember(thisChapter, subjectNotes) {
                val matching = subjectNotes.filter {
                    it.chapterName.equals(thisChapter, ignoreCase = true) ||
                    it.chapterName.contains(thisCleanName, ignoreCase = true)
                }
                matching.ifEmpty {
                    listOf(
                        NoteEntity(
                            id = 10000L + chapterIdx,
                            subjectName = subjectName,
                            chapterName = thisChapter,
                            chapterNumber = chapterIdx + 1,
                            title = "Chapter ${chapterIdx + 1} Summary",
                            summaryText = "Notes for $thisChapter",
                            timeSpentSeconds = 0,
                            revisitCount = 1
                        )
                    )
                }
            }

            ChapterNotesHorizontalPager(
                notes = chapterNotes,
                initialNoteId = if (chapterIdx == initialChapterIndex) initialNoteId else null,
                isActiveChapter = chapterIdx == verticalPagerState.currentPage,
                targetPage = if (chapterIdx == verticalPagerState.currentPage) targetNotePage else null,
                onTargetPageConsumed = { targetNotePage = null },
                onActiveNoteChanged = { note, pageIdx, count ->
                    activeNote = note
                    activeNoteIndex = pageIdx
                    activeChapterNotesCount = count
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons: "+ Note / Photo", "+ Add Question" & Stopwatch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MindLoopPrimaryButton(
                onClick = { showAddNoteDialog = true },
                modifier = Modifier
                    .weight(1.1f)
                    .height(46.dp)
                    .testTag("feed_add_note_button"),
                shape = RoundedCornerShape(12.dp),
                containerColor = SageGreen
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "+ Note / Photo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            MindLoopPrimaryButton(
                onClick = {
                    val noteId = activeNote?.id ?: 1L
                    onAddQuestionClick(noteId, currentChapter)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("feed_add_question_button"),
                shape = RoundedCornerShape(12.dp),
                containerColor = DeepIndigo
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add Question",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            MindLoopPrimaryButton(
                onClick = { isTimerRunning = !isTimerRunning },
                modifier = Modifier
                    .weight(0.9f)
                    .height(46.dp)
                    .testTag("feed_timer_button"),
                shape = RoundedCornerShape(12.dp),
                containerColor = DeepIndigo
            ) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = timeFormatted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showAddNoteDialog) {
        AddNotePhotoDialog(
            initialSubject = subjectName,
            initialChapter = currentChapter,
            availableSubjects = listOf("Indian Polity", "Geography", "History", "Current Affairs", "Economics", "General Science"),
            availableChapters = subjectChapters,
            onDismiss = { showAddNoteDialog = false },
            onSaveNote = { subj, chap, title, content, uri ->
                onSaveNote(subj, chap, title, content, uri)
                showAddNoteDialog = false
            }
        )
    }
}

// Backward-compatible overload
@Composable
fun StudyNotesFeedScreen(
    chapterName: String,
    subjectName: String,
    notes: List<NoteEntity>,
    onAddQuestionClick: () -> Unit,
    onBack: () -> Unit,
    onRecordTime: (noteId: Long, seconds: Long) -> Unit,
    initialNoteId: Long? = null,
    onStartSession: (sessionId: String, noteId: Long, subject: String, chapter: String, startedAt: Long, mode: String) -> Unit = { _, _, _, _, _, _ -> },
    onEndSession: (sessionId: String, endedAt: Long, durationSeconds: Long, noteId: Long) -> Unit = { _, _, _, _ -> },
    onSaveNote: (subject: String, chapter: String, title: String, content: String, imageUri: String?) -> Unit = { _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) = StudyNotesFeedScreen(
    chapterName = chapterName,
    subjectName = subjectName,
    allNotes = notes,
    onAddQuestionClick = { _, _ -> onAddQuestionClick() },
    onBack = onBack,
    onRecordTime = onRecordTime,
    initialNoteId = initialNoteId,
    onStartSession = onStartSession,
    onEndSession = onEndSession,
    onSaveNote = onSaveNote,
    modifier = modifier
)

@Composable
fun AddNotePhotoDialog(
    initialSubject: String,
    initialChapter: String,
    availableSubjects: List<String>,
    availableChapters: List<String>,
    onDismiss: () -> Unit,
    onSaveNote: (subject: String, chapter: String, title: String, content: String, imageUri: String?) -> Unit
) {
    val context = LocalContext.current
    var selectedSubject by remember { mutableStateOf(initialSubject) }
    var selectedChapter by remember { mutableStateOf(initialChapter) }
    var customChapterName by remember { mutableStateOf("") }
    var isCustomChapter by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
    var subjectExpanded by remember { mutableStateOf(false) }
    var chapterExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = uris
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Note / Textbook Photo",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Photo Selection Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("pick_textbook_photo_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedImageUris.isNotEmpty()) Color(0xFFF1F7F4) else Color(0xFFF3F5F9)
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (selectedImageUris.isNotEmpty()) SageGreen else DeepIndigo.copy(alpha = 0.3f)
                    )
                ) {
                    if (selectedImageUris.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedImageUris.forEachIndexed { idx, uri ->
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = "Selected photo ${idx + 1}",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .align(Alignment.TopStart)
                                                .background(DeepIndigo.copy(alpha = 0.8f), CircleShape)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("${idx + 1}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✓ ${selectedImageUris.size} Image Note${if (selectedImageUris.size > 1) "s" else ""} Selected",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SageGreen
                                )
                                Text(
                                    text = "+ Add / Change Photos",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo,
                                    modifier = Modifier.clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = DeepIndigo,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Take / Upload Image Notes",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepIndigo
                            )
                            Text(
                                text = "Select 1 to 15 photos of textbook pages or diagrams",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subject selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { subjectExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Subject: $selectedSubject", fontSize = 13.sp, color = DeepIndigo)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = DeepIndigo)
                        }
                    }
                    DropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                        availableSubjects.forEach { subj ->
                            DropdownMenuItem(
                                text = { Text(subj) },
                                onClick = {
                                    selectedSubject = subj
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Chapter selection
                if (!isCustomChapter) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { chapterExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Chapter: $selectedChapter",
                                    fontSize = 13.sp,
                                    color = DeepIndigo,
                                    maxLines = 1
                                )
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = DeepIndigo)
                            }
                        }
                        DropdownMenu(expanded = chapterExpanded, onDismissRequest = { chapterExpanded = false }) {
                            availableChapters.forEach { chap ->
                                DropdownMenuItem(
                                    text = { Text(chap) },
                                    onClick = {
                                        selectedChapter = chap
                                        chapterExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("+ Type Custom Chapter Name", fontWeight = FontWeight.Bold, color = SageGreen) },
                                onClick = {
                                    isCustomChapter = true
                                    chapterExpanded = false
                                }
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customChapterName,
                        onValueChange = { customChapterName = it },
                        label = { Text("Custom Chapter Name (e.g. Derivatives)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { isCustomChapter = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Pick from list")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Note Title
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Note Title (e.g. Formula Sheet / Article 51)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Note Content / Formulas (Optional)
                OutlinedTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    label = { Text("Key Points / Summary Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Save button
                val finalChapter = if (isCustomChapter && customChapterName.isNotBlank()) customChapterName.trim() else selectedChapter
                val canSave = (noteTitle.isNotBlank() || selectedImageUris.isNotEmpty())

                MindLoopPrimaryButton(
                    onClick = {
                        val persistentUri = if (selectedImageUris.isNotEmpty()) {
                            ImageStorageHelper.saveMultipleImagesToInternalStorage(context, selectedImageUris)
                        } else null

                        val finalTitle = noteTitle.trim().ifBlank {
                            if (persistentUri != null) "Image Note - $finalChapter" else "Note - $finalChapter"
                        }

                        onSaveNote(
                            selectedSubject,
                            finalChapter,
                            finalTitle,
                            noteContent.trim(),
                            persistentUri
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_textbook_note_button"),
                    enabled = canSave,
                    shape = RoundedCornerShape(12.dp),
                    containerColor = DeepIndigo
                ) {
                    Text(
                        text = "Save Note to Chapter",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterNotesHorizontalPager(
    notes: List<NoteEntity>,
    initialNoteId: Long?,
    isActiveChapter: Boolean,
    targetPage: Int?,
    onTargetPageConsumed: () -> Unit,
    onActiveNoteChanged: (NoteEntity?, Int, Int) -> Unit
) {
    val initialIdx = remember(notes, initialNoteId) {
        if (initialNoteId != null) {
            val idx = notes.indexOfFirst { it.id == initialNoteId }
            if (idx >= 0) idx else 0
        } else 0
    }

    // Horizontal Pager: user slides like Instagram multi-photos carousel across notes of this chapter
    val horizontalPagerState = rememberPagerState(
        initialPage = initialIdx.coerceIn(0, (notes.size - 1).coerceAtLeast(0)),
        pageCount = { notes.size.coerceAtLeast(1) }
    )

    LaunchedEffect(targetPage) {
        if (targetPage != null && targetPage in 0 until notes.size) {
            horizontalPagerState.animateScrollToPage(targetPage)
            onTargetPageConsumed()
        }
    }

    LaunchedEffect(horizontalPagerState.currentPage, notes, isActiveChapter) {
        if (isActiveChapter) {
            val note = notes.getOrNull(horizontalPagerState.currentPage) ?: notes.firstOrNull()
            onActiveNoteChanged(note, horizontalPagerState.currentPage, notes.size)
        }
    }

    HorizontalPager(
        state = horizontalPagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = true
    ) { page ->
        val note = notes.getOrNull(page)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp)
        ) {
            HandwrittenNoteViewer(
                note = note,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ==========================================
// 5. ADD NOTES SCREEN
// ==========================================
@Composable
fun AddNotesScreen(
    defaultSubject: String = "Indian Polity",
    defaultChapter: String = "Fundamental Rights",
    customSubjects: List<String> = emptyList(),
    customChapters: Map<String, List<String>> = emptyMap(),
    onSaveNote: (subject: String, chapter: String, title: String, content: String, imageUri: String?) -> Unit,
    onImportCsv: suspend (subject: String, chapter: String, csv: String) -> Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf(defaultSubject) }
    var selectedChapter by remember { mutableStateOf(defaultChapter) }
    var customSubjectName by remember { mutableStateOf("") }
    var isCustomSubject by remember { mutableStateOf(false) }
    var customChapterName by remember { mutableStateOf("") }
    var isCustomChapter by remember { mutableStateOf(false) }
    var subjectExpanded by remember { mutableStateOf(false) }
    var chapterExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var selectedImageUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = uris
        }
    }

    var noteTitle by remember { mutableStateOf("Fundamental Rights — Article 19-21") }
    var uploadedCount by remember { mutableIntStateOf(4) }
    var csvStatusMessage by remember { mutableStateOf<String?>(null) }
    var isUploadingCsv by remember { mutableStateOf(false) }

    val baseSubjects = listOf("Indian Polity", "Geography", "History", "Current Affairs", "Economics", "General Science")
    val allSubjects = (baseSubjects + customSubjects).distinct()

    val availableChaptersForSubject = remember(selectedSubject, customChapters) {
        val customList = customChapters[selectedSubject] ?: emptyList()
        if (customList.isNotEmpty()) {
            customList
        } else if (selectedSubject.equals("Indian Polity", ignoreCase = true)) {
            listOf(
                "1. Making of the Constitution",
                "2. Preamble",
                "3. Fundamental Rights",
                "4. Directive Principles",
                "5. Union Executive",
                "6. State Executive"
            )
        } else {
            listOf("1. Introduction & Overview", "2. Core Principles", "3. Advanced Applications")
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

        // Header with Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepIndigo
                )
            }
            Text(
                text = "Add Notes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upload photo drop zone (with real PhotoPicker)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (selectedImageUris.isNotEmpty()) 140.dp else 110.dp)
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                .testTag("upload_dropzone"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedImageUris.isNotEmpty()) Color(0xFFF1F7F4) else Color(0xFFF2F4F8)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (selectedImageUris.isNotEmpty()) SageGreen else DeepIndigo.copy(alpha = 0.35f)
            )
        ) {
            if (selectedImageUris.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = selectedImageUris.first(),
                            contentDescription = "Textbook photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (selectedImageUris.size > 1) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .align(Alignment.BottomEnd)
                                    .background(DeepIndigo.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("+${selectedImageUris.size}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("✓ ${selectedImageUris.size} Photo${if (selectedImageUris.size > 1) "s" else ""} Selected", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SageGreen)
                        Text("Ready to save to $selectedChapter", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap to add / change", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = DeepIndigo,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap to upload textbook photo",
                        fontSize = 14.sp,
                        color = DeepIndigo,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Choose textbook photo from camera or gallery",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Select Subject Dropdown or Custom Input
        if (!isCustomSubject) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { subjectExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Select Subject", fontSize = 12.sp, color = TextSecondary)
                            Text(selectedSubject, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DeepIndigo)
                        }
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = DeepIndigo)
                    }
                }
                DropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                    allSubjects.forEach { subj ->
                        DropdownMenuItem(
                            text = { Text(subj) },
                            onClick = {
                                selectedSubject = subj
                                subjectExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("+ Type Custom Subject Name", fontWeight = FontWeight.Bold, color = SageGreen) },
                        onClick = {
                            isCustomSubject = true
                            subjectExpanded = false
                        }
                    )
                }
            }
        } else {
            OutlinedTextField(
                value = customSubjectName,
                onValueChange = { customSubjectName = it },
                label = { Text("Custom Subject Name (e.g. Psychology)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { isCustomSubject = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Pick from list")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Select Chapter Dropdown or Custom Input
        if (!isCustomChapter) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { chapterExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Select Chapter", fontSize = 12.sp, color = TextSecondary)
                            Text(selectedChapter, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DeepIndigo)
                        }
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = DeepIndigo)
                    }
                }
                DropdownMenu(expanded = chapterExpanded, onDismissRequest = { chapterExpanded = false }) {
                    availableChaptersForSubject.forEach { chap ->
                        DropdownMenuItem(
                            text = { Text(chap) },
                            onClick = {
                                selectedChapter = chap
                                chapterExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("+ Type Custom Chapter Name", fontWeight = FontWeight.Bold, color = SageGreen) },
                        onClick = {
                            isCustomChapter = true
                            chapterExpanded = false
                        }
                    )
                }
            }
        } else {
            OutlinedTextField(
                value = customChapterName,
                onValueChange = { customChapterName = it },
                label = { Text("Custom Chapter Name (e.g. Cognitive Psychology)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { isCustomChapter = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Pick from list")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Recent Uploads (4) row
        Text(
            text = "Recent Uploads ($uploadedCount)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DeepIndigo
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(uploadedCount.coerceAtMost(4)) { idx ->
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE9ECF0))
                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Simulation of thumbnail page
                    Column(modifier = Modifier.padding(4.dp)) {
                        Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFCBD5E1)))
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(modifier = Modifier.fillMaxWidth(0.8f).height(3.dp).background(Color(0xFFCBD5E1)))
                    }
                    // Check badge
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(DeepIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                    }
                }
            }

            // "Add More" dashed box
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, DeepIndigo.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add\nMore",
                    fontSize = 11.sp,
                    color = DeepIndigo,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bulk Add Questions section with CSV upload
        Text(
            text = "Bulk Add Questions",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DeepIndigo
        )

        Spacer(modifier = Modifier.height(10.dp))

        MindLoopSecondaryButton(
            onClick = {
                // Simulate sample CSV questions addition tagged to selectedSubject -> selectedChapter
                val sampleCsv = """
                    Type,Question,OptionA,OptionB,OptionC,OptionD,CorrectAnswer
                    MCQ,"Which Article of Constitution abolishes Untouchability?","Article 14","Article 15","Article 17","Article 19","C"
                    TF,"Right to Property is a Fundamental Right in India.","True","False","B"
                    MCQ,"How many schedules were there originally in the Indian Constitution?","8 Schedules","10 Schedules","12 Schedules","7 Schedules","A"
                """.trimIndent()
                // In actual runtime, import questions
                csvStatusMessage = "3 Questions imported and linked to $selectedSubject → $selectedChapter!"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("upload_csv_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Outlined.Folder, contentDescription = null, tint = DeepIndigo)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upload CSV File", color = DeepIndigo, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = csvStatusMessage ?: "Questions will be added to $selectedSubject → $selectedChapter",
            fontSize = 11.5.sp,
            color = if (csvStatusMessage != null) SageGreen else TextSecondary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Save to Chapter Button
        MindLoopPrimaryButton(
            onClick = {
                val finalSubject = if (isCustomSubject && customSubjectName.isNotBlank()) customSubjectName.trim() else selectedSubject
                val finalChapter = if (isCustomChapter && customChapterName.isNotBlank()) customChapterName.trim() else selectedChapter

                val persistentUri = if (selectedImageUris.isNotEmpty()) {
                    ImageStorageHelper.saveMultipleImagesToInternalStorage(context, selectedImageUris)
                } else null

                val titleToSave = noteTitle.trim().ifBlank {
                    if (persistentUri != null) "Image Note - $finalChapter" else "Note - $finalChapter"
                }

                onSaveNote(
                    finalSubject,
                    finalChapter,
                    titleToSave,
                    "• Summary: Notes saved for $finalChapter in $finalSubject.\n• Spaced Repetition queue initiated.",
                    persistentUri
                )
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_to_chapter_button"),
            containerColor = DeepIndigo,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Save to Chapter",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
