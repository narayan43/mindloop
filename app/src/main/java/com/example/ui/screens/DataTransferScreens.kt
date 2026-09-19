package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionAttemptEntity
import com.example.data.entity.QuestionEntity
import com.example.data.entity.ReelEntity
import com.example.data.entity.StudySessionEntity
import com.example.data.model.CurriculumChapter
import com.example.data.model.CurriculumExam
import com.example.data.model.CurriculumSubject
import com.example.data.model.DataBackupManager
import com.example.data.model.MindLoopCourseBackup
import com.example.ui.components.InteractiveCardBorder
import com.example.ui.components.MindLoopPrimaryButton
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MindLoopViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Bottom sheet modal for exporting app data (Notes, Questions, Reels, Curriculum).
 * Excludes user mistake logs and personal attempts.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExportDataBottomSheet(
    allNotes: List<NoteEntity>,
    allQuestions: List<QuestionEntity>,
    allReels: List<ReelEntity>,
    allSubjects: List<CurriculumSubject>,
    allExams: List<CurriculumExam>,
    allChapters: List<CurriculumChapter>,
    allQuestionAttempts: List<QuestionAttemptEntity> = emptyList(),
    allStudySessions: List<StudySessionEntity> = emptyList(),
    currentUserName: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var includeNotes by remember { mutableStateOf(true) }
    var includeQuestions by remember { mutableStateOf(true) }
    var includeReels by remember { mutableStateOf(true) }
    var includeCurriculum by remember { mutableStateOf(true) }
    var includeUserData by remember { mutableStateOf(false) }

    val availableSubjectNames = remember(allSubjects, allNotes, allQuestions, allReels) {
        val names = mutableSetOf<String>()
        names.addAll(allSubjects.map { it.name })
        names.addAll(allNotes.map { it.subjectName })
        names.addAll(allQuestions.map { it.subjectName })
        names.addAll(allReels.map { it.subject })
        names.filter { it.isNotBlank() }.sorted()
    }

    var selectedSubjectFilters by remember { mutableStateOf<Set<String>>(emptySet()) }
    // emptySet() means "All Subjects"

    val isAllSubjectsSelected = selectedSubjectFilters.isEmpty()

    // Filter counts based on selected subjects
    val filteredNotesCount = remember(includeNotes, selectedSubjectFilters, allNotes) {
        if (!includeNotes) 0
        else if (isAllSubjectsSelected) allNotes.size
        else allNotes.count { selectedSubjectFilters.contains(it.subjectName) }
    }

    val filteredQuestionsCount = remember(includeQuestions, selectedSubjectFilters, allQuestions) {
        if (!includeQuestions) 0
        else if (isAllSubjectsSelected) allQuestions.size
        else allQuestions.count { selectedSubjectFilters.contains(it.subjectName) }
    }

    val filteredReelsCount = remember(includeReels, selectedSubjectFilters, allReels) {
        if (!includeReels) 0
        else if (isAllSubjectsSelected) allReels.size
        else allReels.count { selectedSubjectFilters.contains(it.subject) }
    }

    val filteredCurriculumCount = remember(includeCurriculum, selectedSubjectFilters, allSubjects, allChapters) {
        if (!includeCurriculum) 0
        else if (isAllSubjectsSelected) allSubjects.size + allChapters.size
        else {
            val subjs = allSubjects.count { selectedSubjectFilters.contains(it.name) }
            val chaps = allChapters.count { selectedSubjectFilters.contains(it.subjectName) }
            subjs + chaps
        }
    }

    val filteredAttemptsCount = remember(includeUserData, selectedSubjectFilters, allQuestionAttempts) {
        if (!includeUserData) 0
        else if (isAllSubjectsSelected) allQuestionAttempts.size
        else allQuestionAttempts.count { selectedSubjectFilters.contains(it.subject) }
    }

    val filteredSessionsCount = remember(includeUserData, selectedSubjectFilters, allStudySessions) {
        if (!includeUserData) 0
        else if (isAllSubjectsSelected) allStudySessions.size
        else allStudySessions.count { selectedSubjectFilters.contains(it.subject) }
    }

    val totalSelectedItems = filteredNotesCount + filteredQuestionsCount + filteredReelsCount + filteredCurriculumCount + filteredAttemptsCount + filteredSessionsCount

    var isExporting by remember { mutableStateOf(false) }
    var exportedJsonString by remember { mutableStateOf<String?>(null) }
    var showSuccessCard by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundOffWhite,
        modifier = modifier.testTag("export_data_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DeepIndigo.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = DeepIndigo,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Export Study Data",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "Share notes, reels & questions with others",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notice badge: Clean Course Pack vs Full Personal Backup
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (includeUserData) SageGreen.copy(alpha = 0.12f) else DeepIndigo.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, if (includeUserData) SageGreen.copy(alpha = 0.35f) else DeepIndigo.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (includeUserData) Icons.Default.CloudSync else Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (includeUserData) SageGreen else DeepIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (includeUserData) {
                            "Full Personal Backup: Personal test attempts, mistakes notebook, and reading sessions are included for migration."
                        } else {
                            "Clean Course Pack: Personal test attempts, mistakes notebook, and streak remain private on your device."
                        },
                        fontSize = 12.sp,
                        color = DeepIndigo,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selection Controls Header (Select All / Clear All)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Data Categories",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            val hasUserData = allQuestionAttempts.isNotEmpty() || allStudySessions.isNotEmpty()
                            val allOn = includeNotes && includeQuestions && includeReels && includeCurriculum && (!hasUserData || includeUserData)
                            includeNotes = !allOn
                            includeQuestions = !allOn
                            includeReels = !allOn
                            includeCurriculum = !allOn
                            if (hasUserData) {
                                includeUserData = !allOn
                            }
                        }
                    ) {
                        val hasUserData = allQuestionAttempts.isNotEmpty() || allStudySessions.isNotEmpty()
                        val isAllSelected = includeNotes && includeQuestions && includeReels && includeCurriculum && (!hasUserData || includeUserData)
                        Text(
                            text = if (isAllSelected) "Deselect All" else "Select All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepIndigo
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category 1: Study Notes
            ExportCategoryCard(
                icon = Icons.Default.Description,
                title = "Study Notes & Summaries",
                subtitle = "Concepts, bullet points, and chapter numbers",
                countText = "$filteredNotesCount Notes available",
                checked = includeNotes,
                onCheckedChange = { includeNotes = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category 2: Questions & Tests
            ExportCategoryCard(
                icon = Icons.Default.Quiz,
                title = "Question Bank & MCQs",
                subtitle = "Active recall questions, answer options & solutions",
                countText = "$filteredQuestionsCount Questions available",
                checked = includeQuestions,
                onCheckedChange = { includeQuestions = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category 3: Reels & Video Notes
            ExportCategoryCard(
                icon = Icons.Default.Movie,
                title = "Study Reels & Video Notes",
                subtitle = "Concept video clips and attached lessons",
                countText = "$filteredReelsCount Reels available",
                checked = includeReels,
                onCheckedChange = { includeReels = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category 4: Curriculum & Hierarchy
            ExportCategoryCard(
                icon = Icons.Default.LibraryBooks,
                title = "Curriculum & Syllabus Structure",
                subtitle = "Exams, subjects, and chapter categorization",
                countText = "$filteredCurriculumCount Chapters & Subjects",
                checked = includeCurriculum,
                onCheckedChange = { includeCurriculum = it }
            )

            // Category 5: User Activity & Mistakes Notebook
            if (allQuestionAttempts.isNotEmpty() || allStudySessions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                val mistakesCount = allQuestionAttempts.count { !it.isCorrect }
                ExportCategoryCard(
                    icon = Icons.Default.HistoryEdu,
                    title = "My Activity & Mistakes Notebook",
                    subtitle = "Quiz attempts, mistakes records & study session logs",
                    countText = "$filteredAttemptsCount Attempts ($mistakesCount Mistakes) · $filteredSessionsCount Study Sessions",
                    checked = includeUserData,
                    onCheckedChange = { includeUserData = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subject Filter Section
            Text(
                text = "Filter by Subject (Optional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose whether to export the whole course or specific subjects",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // "All Subjects" chip
                FilterChip(
                    selected = isAllSubjectsSelected,
                    onClick = { selectedSubjectFilters = emptySet() },
                    label = { Text("All Subjects (${availableSubjectNames.size})", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepIndigo,
                        selectedLabelColor = Color.White
                    )
                )

                for (subj in availableSubjectNames) {
                    val isSelected = selectedSubjectFilters.contains(subj)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedSubjectFilters = if (isSelected) {
                                selectedSubjectFilters - subj
                            } else {
                                selectedSubjectFilters + subj
                            }
                        },
                        label = { Text(subj, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepIndigo,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Success Card or Export Action Button
            if (showSuccessCard) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SageGreen,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Course Pack (.mlpack) Generated!",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "Complete study pack with $totalSelectedItems items, offline videos & diagrams is ready to share.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    exportedJsonString?.let {
                                        clipboardManager.setText(AnnotatedString(it))
                                        Toast.makeText(context, "JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy JSON", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    // Re-trigger share .mlpack
                                    val backup = createCourseBackupPayload(
                                        allNotes = allNotes,
                                        allQuestions = allQuestions,
                                        allReels = allReels,
                                        allSubjects = allSubjects,
                                        allExams = allExams,
                                        allChapters = allChapters,
                                        allQuestionAttempts = allQuestionAttempts,
                                        allStudySessions = allStudySessions,
                                        selectedSubjects = if (isAllSubjectsSelected) null else selectedSubjectFilters,
                                        includeNotes = includeNotes,
                                        includeQuestions = includeQuestions,
                                        includeReels = includeReels,
                                        includeCurriculum = includeCurriculum,
                                        includeUserData = includeUserData,
                                        userName = currentUserName
                                    )
                                    DataBackupManager.exportAndShareMlpack(context, backup)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share .mlpack", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Privacy notice and Export Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SageGreen,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "🔒 Gallery-Restricted .mlpack Export",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Bundles all notes, questions, and offline video reels (.mp4). When imported on any phone, all media files are saved strictly in app-private storage (.nomedia) and will NEVER appear in device Gallery or Google Photos.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Export Button (appears whenever user has items selected)
                AnimatedVisibility(visible = totalSelectedItems > 0) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MindLoopPrimaryButton(
                            onClick = {
                                if (!isExporting) {
                                    isExporting = true
                                    coroutineScope.launch {
                                        val backup = createCourseBackupPayload(
                                            allNotes = allNotes,
                                            allQuestions = allQuestions,
                                            allReels = allReels,
                                            allSubjects = allSubjects,
                                            allExams = allExams,
                                            allChapters = allChapters,
                                            allQuestionAttempts = allQuestionAttempts,
                                            allStudySessions = allStudySessions,
                                            selectedSubjects = if (isAllSubjectsSelected) null else selectedSubjectFilters,
                                            includeNotes = includeNotes,
                                            includeQuestions = includeQuestions,
                                            includeReels = includeReels,
                                            includeCurriculum = includeCurriculum,
                                            includeUserData = includeUserData,
                                            userName = currentUserName
                                        )
                                        val json = DataBackupManager.serializeBackup(backup)
                                        exportedJsonString = json
                                        val shared = DataBackupManager.exportAndShareMlpack(context, backup)
                                        isExporting = false
                                        showSuccessCard = true
                                        if (shared) {
                                            Toast.makeText(context, "Ready to share .mlpack with students!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("confirm_export_button"),
                            enabled = !isExporting
                        ) {
                            Text(
                                text = if (isExporting) "Packaging .mlpack..." else "Export Complete Pack (.mlpack)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                if (!isExporting) {
                                    isExporting = true
                                    coroutineScope.launch {
                                        val backup = createCourseBackupPayload(
                                            allNotes = allNotes,
                                            allQuestions = allQuestions,
                                            allReels = allReels,
                                            allSubjects = allSubjects,
                                            allExams = allExams,
                                            allChapters = allChapters,
                                            allQuestionAttempts = allQuestionAttempts,
                                            allStudySessions = allStudySessions,
                                            selectedSubjects = if (isAllSubjectsSelected) null else selectedSubjectFilters,
                                            includeNotes = includeNotes,
                                            includeQuestions = includeQuestions,
                                            includeReels = includeReels,
                                            includeCurriculum = includeCurriculum,
                                            includeUserData = includeUserData,
                                            userName = currentUserName
                                        )
                                        val json = DataBackupManager.serializeBackup(backup)
                                        exportedJsonString = json
                                        val shared = DataBackupManager.exportAndShare(context, backup)
                                        isExporting = false
                                        showSuccessCard = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Or Export Lightweight JSON Only (Without Video Files)", fontSize = 12.sp, color = DeepIndigo)
                        }
                    }
                }

                if (totalSelectedItems == 0) {
                    Text(
                        text = "Please select at least one data category above to export.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExportCategoryCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    countText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        shape = RoundedCornerShape(14.dp),
        color = if (checked) Color.White else BackgroundOffWhite,
        border = BorderStroke(
            1.dp,
            if (checked) DeepIndigo.copy(alpha = 0.4f) else InteractiveCardBorder
        ),
        shadowElevation = if (checked) 2.dp else 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (checked) DeepIndigo.copy(alpha = 0.1f) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) DeepIndigo else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (checked) DeepIndigo else TextSecondary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = countText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (checked) SageGreen else TextMuted
                )
            }

            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = DeepIndigo,
                    checkmarkColor = Color.White
                )
            )
        }
    }
}

/**
 * Bottom sheet modal for importing course data from another user / backup.
 * Provides file picker, JSON parser, and inspection preview before importing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportDataBottomSheet(
    onDismiss: () -> Unit,
    onConfirmImport: suspend (MindLoopCourseBackup, Boolean, Boolean, Boolean, Boolean, Boolean) -> MindLoopViewModel.ImportSummary,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var inputModeTab by remember { mutableStateOf(0) } // 0: File Picker, 1: Paste Text
    var rawJsonText by remember { mutableStateOf("") }
    var parsedBackup by remember { mutableStateOf<MindLoopCourseBackup?>(null) }
    var parsedPackageResult by remember { mutableStateOf<com.example.data.model.ParsedPackageResult?>(null) }
    var parseError by remember { mutableStateOf<String?>(null) }

    var importNotes by remember { mutableStateOf(true) }
    var importQuestions by remember { mutableStateOf(true) }
    var importReels by remember { mutableStateOf(true) }
    var importCurriculum by remember { mutableStateOf(true) }
    var importUserData by remember { mutableStateOf(true) }

    var isImporting by remember { mutableStateOf(false) }
    var importSummaryResult by remember { mutableStateOf<MindLoopViewModel.ImportSummary?>(null) }

    // System File Picker for .mlpack (ZIP) or JSON / TXT
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val result = DataBackupManager.unpackBackupFromUri(context, uri)
                result.onSuccess { pkg ->
                    parsedPackageResult = pkg
                    parsedBackup = pkg.backup
                    parseError = null
                }.onFailure { err ->
                    parsedPackageResult = null
                    parsedBackup = null
                    parseError = "Could not open pack: ${err.localizedMessage ?: "Invalid .mlpack or JSON format"}"
                }
            } catch (e: Exception) {
                parsedPackageResult = null
                parsedBackup = null
                parseError = "Error reading file: ${e.message}"
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundOffWhite,
        modifier = modifier.testTag("import_data_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SageGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = SageGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Import Study Data",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "Load shared notes, reels & questions into your app",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Completed Celebration Card
            if (importSummaryResult != null) {
                val res = importSummaryResult!!
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.5f)),
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SageGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Import Complete!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )

                        Text(
                            text = "All study data has been successfully imported into your curriculum and database.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metric grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ImportMetricBadge(count = res.notesImported, label = "Notes")
                            ImportMetricBadge(count = res.questionsImported, label = "Questions")
                            ImportMetricBadge(count = res.reelsImported, label = "Reels")
                            ImportMetricBadge(count = res.subjectsCreated, label = "New Subjects")
                        }

                        if (res.attemptsImported > 0 || res.sessionsImported > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ImportMetricBadge(count = res.attemptsImported, label = "Quiz Attempts")
                                ImportMetricBadge(count = res.sessionsImported, label = "Study Logs")
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        MindLoopPrimaryButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("import_done_button")
                        ) {
                            Text(
                                text = "Done & View Content",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // If not yet parsed, show selection methods: File Picker or Paste JSON
                if (parsedBackup == null) {
                    // Method Tabs
                    TabRow(
                        selectedTabIndex = inputModeTab,
                        containerColor = SurfaceWhite,
                        contentColor = DeepIndigo,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[inputModeTab]),
                                color = DeepIndigo
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, InteractiveCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = inputModeTab == 0,
                            onClick = { inputModeTab = 0 },
                            text = { Text("📁 Select Backup File", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = inputModeTab == 1,
                            onClick = { inputModeTab = 1 },
                            text = { Text("📋 Paste JSON Text", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (inputModeTab == 0) {
                        // File picker button (.mlpack or .json)
                        Surface(
                            onClick = { filePickerLauncher.launch("*/*") },
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceWhite,
                            border = BorderStroke(1.5.dp, DeepIndigo.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(DeepIndigo.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FileOpen,
                                        contentDescription = null,
                                        tint = DeepIndigo,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Choose Study Pack File",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Supports .mlpack complete packs (with offline videos & images) or .json files",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = SageGreen,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "App-restricted storage: Media will not show in phone Gallery",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { filePickerLauncher.launch("*/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Browse Files (.mlpack / .json)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Paste JSON
                        Column {
                            OutlinedTextField(
                                value = rawJsonText,
                                onValueChange = {
                                    rawJsonText = it
                                    if (it.isNotBlank()) {
                                        val result = DataBackupManager.parseBackup(it)
                                        result.onSuccess { b ->
                                            parsedPackageResult = com.example.data.model.ParsedPackageResult(backup = b, isMlpack = false)
                                            parsedBackup = b
                                            parseError = null
                                        }.onFailure { err ->
                                            parsedPackageResult = null
                                            parseError = "Invalid JSON: ${err.message}"
                                        }
                                    } else {
                                        parseError = null
                                    }
                                },
                                label = { Text("Paste MindLoop Backup JSON") },
                                placeholder = { Text("{\"version\": 1, \"appName\": \"MindLoop\", ...}") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DeepIndigo,
                                    unfocusedBorderColor = InteractiveCardBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        val result = DataBackupManager.parseBackup(rawJsonText)
                                        result.onSuccess { b ->
                                            parsedPackageResult = com.example.data.model.ParsedPackageResult(backup = b, isMlpack = false)
                                            parsedBackup = b
                                            parseError = null
                                        }.onFailure { err ->
                                            parsedPackageResult = null
                                            parseError = "Invalid JSON: ${err.message}"
                                        }
                                    },
                                    enabled = rawJsonText.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Parse & Inspect Pack", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (parseError != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Terracotta.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, Terracotta.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Terracotta,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = parseError!!,
                                    fontSize = 12.sp,
                                    color = Terracotta
                                )
                            }
                        }
                    }
                } else {
                    // Parsed Backup Inspection & Preview Panel
                    val backup = parsedBackup!!
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceWhite,
                        border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.4f)),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Course Pack Verified",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepIndigo
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        parsedBackup = null
                                        parsedPackageResult = null
                                        rawJsonText = ""
                                    }
                                ) {
                                    Text("Change File", fontSize = 12.sp, color = DeepIndigo)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Source: ${backup.sourceUser} • Exported: ${backup.exportedDateFormatted.ifBlank { "Recently" }}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Package details & Gallery Privacy Badge
                            if (parsedPackageResult?.isMlpack == true) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DeepIndigo.copy(alpha = 0.06f),
                                    border = BorderStroke(1.dp, DeepIndigo.copy(alpha = 0.2f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = SageGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Complete .mlpack Archive",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepIndigo
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "🎬 ${parsedPackageResult?.videosCount ?: 0} Video Lessons • 🖼️ ${parsedPackageResult?.imagesCount ?: 0} Note Images included",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = SageGreen,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .padding(top = 1.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Privacy Guarantee: Media is restricted to MindLoop app storage (.nomedia enabled). Never visible in phone Gallery or Google Photos.",
                                                fontSize = 11.5.sp,
                                                color = SageGreen,
                                                lineHeight = 15.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Items breakdown with checkboxes
                            Text(
                                text = "Select Content to Import into Your App:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Notes
                            ImportCategoryCheckbox(
                                label = "Study Notes (${backup.notes.size} Notes)",
                                subtitle = "Placed under respective subjects & chapters",
                                checked = importNotes,
                                onCheckedChange = { importNotes = it },
                                count = backup.notes.size
                            )

                            // Questions
                            ImportCategoryCheckbox(
                                label = "Question Bank (${backup.questions.size} Questions)",
                                subtitle = "Ready for Test Yourself and Spaced Repetition",
                                checked = importQuestions,
                                onCheckedChange = { importQuestions = it },
                                count = backup.questions.size
                            )

                            // Reels
                            ImportCategoryCheckbox(
                                label = "Study Reels (${backup.reels.size} Video Lessons)",
                                subtitle = "Appears in Reels Feed under topics",
                                checked = importReels,
                                onCheckedChange = { importReels = it },
                                count = backup.reels.size
                            )

                            // Curriculum
                            ImportCategoryCheckbox(
                                label = "Curriculum (${backup.subjects.size} Subjects, ${backup.chapters.size} Chapters)",
                                subtitle = "Creates any missing subjects or chapters",
                                checked = importCurriculum,
                                onCheckedChange = { importCurriculum = it },
                                count = backup.subjects.size + backup.chapters.size
                            )

                            // User Activity & Mistakes
                            if (backup.hasUserData) {
                                ImportCategoryCheckbox(
                                    label = "User Activity & Mistakes (${backup.userAttempts.size} Attempts, ${backup.mistakesCount} Mistakes, ${backup.studySessions.size} Sessions)",
                                    subtitle = "Restores quiz attempts, mistakes notebook, and reading time logs",
                                    checked = importUserData,
                                    onCheckedChange = { importUserData = it },
                                    count = backup.userAttempts.size + backup.studySessions.size
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Structure placement note
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(modifier = Modifier.padding(10.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.LibraryBooks,
                                        contentDescription = null,
                                        tint = DeepIndigo,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val sampleSubjects = (backup.subjects.map { it.name } + backup.notes.map { it.subjectName }).distinct().take(3)
                                    Text(
                                        text = "Target Subjects: " + sampleSubjects.joinToString(", ") + (if (sampleSubjects.size < backup.subjects.size) " and more..." else ""),
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val anyChecked = (importNotes && backup.notes.isNotEmpty()) ||
                            (importQuestions && backup.questions.isNotEmpty()) ||
                            (importReels && backup.reels.isNotEmpty()) ||
                            (importCurriculum && (backup.subjects.isNotEmpty() || backup.chapters.isNotEmpty())) ||
                            (importUserData && backup.hasUserData)

                    MindLoopPrimaryButton(
                        onClick = {
                            if (!isImporting && anyChecked) {
                                isImporting = true
                                coroutineScope.launch {
                                    val finalBackup = if (parsedPackageResult != null) {
                                        DataBackupManager.commitImportedMedia(context, parsedPackageResult!!)
                                    } else {
                                        backup
                                    }
                                    val summary = onConfirmImport(
                                        finalBackup,
                                        importNotes,
                                        importQuestions,
                                        importReels,
                                        importCurriculum,
                                        importUserData
                                    )
                                    isImporting = false
                                    importSummaryResult = summary
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_import_button"),
                        enabled = !isImporting && anyChecked
                    ) {
                        Text(
                            text = if (isImporting) "Importing Data & Media..." else "Confirm & Import Content",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportCategoryCheckbox(
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    count: Int
) {
    if (count == 0) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = DeepIndigo,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepIndigo
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ImportMetricBadge(
    count: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = count.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DeepIndigo
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

private fun createCourseBackupPayload(
    allNotes: List<NoteEntity>,
    allQuestions: List<QuestionEntity>,
    allReels: List<ReelEntity>,
    allSubjects: List<CurriculumSubject>,
    allExams: List<CurriculumExam>,
    allChapters: List<CurriculumChapter>,
    allQuestionAttempts: List<QuestionAttemptEntity> = emptyList(),
    allStudySessions: List<StudySessionEntity> = emptyList(),
    selectedSubjects: Set<String>?,
    includeNotes: Boolean,
    includeQuestions: Boolean,
    includeReels: Boolean,
    includeCurriculum: Boolean,
    includeUserData: Boolean = false,
    userName: String
): MindLoopCourseBackup {
    val allowedSubjectNames = selectedSubjects ?: allSubjects.map { it.name }.toSet()

    val filteredNotes = if (includeNotes) {
        allNotes.filter { allowedSubjectNames.isEmpty() || allowedSubjectNames.contains(it.subjectName) }.map { n ->
            com.example.data.model.BackupNoteItem(
                id = n.id,
                examId = n.examId,
                subjectName = n.subjectName,
                chapterName = n.chapterName,
                chapterNumber = n.chapterNumber,
                title = n.title,
                summaryText = n.summaryText,
                imageUri = n.imageUri
            )
        }
    } else emptyList()

    val filteredQuestions = if (includeQuestions) {
        allQuestions.filter { allowedSubjectNames.isEmpty() || allowedSubjectNames.contains(it.subjectName) }.map { q ->
            com.example.data.model.BackupQuestionItem(
                id = q.id,
                examId = q.examId,
                subjectName = q.subjectName,
                chapterName = q.chapterName,
                questionType = q.questionType,
                questionText = q.questionText,
                optionA = q.optionA,
                optionB = q.optionB,
                optionC = q.optionC,
                optionD = q.optionD,
                correctAnswerIndex = q.correctAnswerIndex,
                sourceType = q.sourceType,
                sourceId = q.sourceId
            )
        }
    } else emptyList()

    val filteredReels = if (includeReels) {
        allReels.filter { allowedSubjectNames.isEmpty() || allowedSubjectNames.contains(it.subject) }.map { r ->
            com.example.data.model.BackupReelItem(
                id = r.id,
                exam = r.exam,
                subject = r.subject,
                chapter = r.chapter,
                title = r.title,
                description = r.description,
                videoUrl = r.videoUrl,
                durationSeconds = r.durationSeconds,
                uploadedBy = r.uploadedBy
            )
        }
    } else emptyList()

    val filteredSubjects = if (includeCurriculum) {
        if (selectedSubjects != null) allSubjects.filter { selectedSubjects.contains(it.name) } else allSubjects
    } else emptyList()

    val filteredChapters = if (includeCurriculum) {
        if (selectedSubjects != null) allChapters.filter { selectedSubjects.contains(it.subjectName) } else allChapters
    } else emptyList()

    val filteredAttempts = if (includeUserData) {
        allQuestionAttempts.filter { allowedSubjectNames.isEmpty() || allowedSubjectNames.contains(it.subject) }.map { a ->
            com.example.data.model.BackupUserAttemptItem(
                attemptId = a.attemptId,
                questionId = a.questionId,
                noteId = a.noteId,
                subject = a.subject,
                chapter = a.chapter,
                questionType = a.questionType,
                shownAt = a.shownAt,
                answeredAt = a.answeredAt,
                timeTakenSeconds = a.timeTakenSeconds,
                selectedAnswer = a.selectedAnswer,
                isCorrect = a.isCorrect,
                selfRating = a.selfRating
            )
        }
    } else emptyList()

    val filteredSessions = if (includeUserData) {
        allStudySessions.filter { allowedSubjectNames.isEmpty() || allowedSubjectNames.contains(it.subject) }.map { s ->
            com.example.data.model.BackupStudySessionItem(
                sessionId = s.sessionId,
                noteId = s.noteId,
                subject = s.subject,
                chapter = s.chapter,
                startedAt = s.startedAt,
                endedAt = s.endedAt,
                durationSeconds = s.durationSeconds
            )
        }
    } else emptyList()

    return MindLoopCourseBackup(
        version = 1,
        appName = "MindLoop",
        exportedAt = System.currentTimeMillis(),
        sourceUser = userName.ifBlank { "MindLoop Student" },
        exams = if (includeCurriculum) allExams else emptyList(),
        subjects = filteredSubjects,
        chapters = filteredChapters,
        notes = filteredNotes,
        questions = filteredQuestions,
        reels = filteredReels,
        userAttempts = filteredAttempts,
        studySessions = filteredSessions
    )
}
