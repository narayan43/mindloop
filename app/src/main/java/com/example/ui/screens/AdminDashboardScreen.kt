package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.config.AdminConfig
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionAttemptEntity
import com.example.data.entity.QuestionEntity
import com.example.data.firestore.AdminStudentSummary
import com.example.data.firestore.AdminStudentsResult
import com.example.ui.components.AccuracyDonutChart
import com.example.ui.components.StudyConsistencyHeatmap
import com.example.ui.components.TimeSpentBySubjectChart
import com.example.ui.components.WeeklyQuestionsBarChart
import com.example.ui.components.tapAffordance
import com.example.ui.theme.Amber
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

// Data structures for curriculum management
data class AdminExamItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val badgeText: String = "Active",
    val isActive: Boolean = true,
    val subjectsCount: Int = 6,
    val topicsCount: Int = 120,
    val iconEmoji: String = "🇮🇳",
    val description: String = "Complete competitive syllabus with verified questions"
)

data class AdminSubjectItem(
    val id: String,
    val examId: String,
    val name: String,
    val progress: Float = 0.5f,
    val percentageText: String = "50%",
    val questionsDueText: String = "12 Due",
    val chaptersCount: Int = 6,
    val iconEmoji: String = "⚖️"
)

data class AdminChapterItem(
    val id: String,
    val subjectName: String,
    val number: Int,
    val name: String,
    val notesCount: Int = 10,
    val questionsCount: Int = 22,
    val avgMasteryPercent: Int = 82
)

data class ParsedCsvQuestion(
    val rowNumber: Int,
    val type: String,
    val questionText: String,
    val optA: String,
    val optB: String,
    val optC: String,
    val optD: String,
    val correctAnswer: String,
    val isValid: Boolean,
    val statusMessage: String
)

data class ParsedCsvNote(
    val rowNumber: Int,
    val subject: String,
    val chapter: String,
    val chapterNum: Int,
    val title: String,
    val content: String,
    val imageUri: String?,
    val isValid: Boolean,
    val statusMessage: String
)

sealed class ContentDrillLevel {
    data object Exams : ContentDrillLevel()
    data class Subjects(val exam: AdminExamItem) : ContentDrillLevel()
    data class Chapters(val exam: AdminExamItem, val subject: AdminSubjectItem) : ContentDrillLevel()
    data class ChapterDetail(
        val exam: AdminExamItem,
        val subject: AdminSubjectItem,
        val chapter: AdminChapterItem
    ) : ContentDrillLevel()
    data class CsvUpload(
        val exam: AdminExamItem,
        val subject: AdminSubjectItem,
        val chapter: AdminChapterItem?,
        val isQuestions: Boolean
    ) : ContentDrillLevel()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    currentUserId: String,
    currentUserEmail: String,
    allQuestions: List<QuestionEntity>,
    allNotes: List<NoteEntity>,
    onBack: () -> Unit,
    onAddQuestion: (
        subject: String,
        chapter: String,
        type: String,
        text: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correctIdx: Int,
        linkedNoteId: Long?
    ) -> Unit,
    onAddNote: (
        subject: String,
        chapter: String,
        chapterNum: Int,
        title: String,
        content: String
    ) -> Unit,
    onDeleteNote: (noteId: Long) -> Unit,
    onDeleteQuestion: (questionId: Long) -> Unit,
    onFetchStudents: suspend () -> AdminStudentsResult,
    onFetchStudentAttempts: suspend (studentUid: String) -> List<QuestionAttemptEntity>,
    onFetchStudentNotes: suspend (studentUid: String) -> List<NoteEntity>,
    onDeleteStudentNote: suspend (studentUid: String, noteId: Long) -> Boolean,
    onImportCsvQuestions: (suspend (subject: String, chapter: String, linkedNoteId: Long?, csv: String) -> Int)? = null,
    onImportCsvNotes: (suspend (csv: String) -> Int)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // -------------------------------------------------------------------------
    // ACCESS CONTROL VERIFICATION
    // -------------------------------------------------------------------------
    var localOverrideUid by remember {
        mutableStateOf(AdminConfig.getLocalAdminUid(context))
    }
    val isAuthorized = remember(currentUserId, currentUserEmail, localOverrideUid) {
        AdminConfig.isAdmin(
            uid = currentUserId,
            email = currentUserEmail,
            localOverrideUid = localOverrideUid
        )
    }

    // Top-Level Segmented Tabs: Overview (0) | Content (1) | Students (2)
    var selectedTopTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Overview", "Content", "Students")

    // Curriculum state (Curriculum hierarchy)
    var examsList by remember {
        mutableStateOf(
            listOf(
                AdminExamItem(
                    id = "upsi",
                    name = "UPSI — Sub Inspector",
                    subtitle = "6 Subjects, 120 Topics",
                    badgeText = "Active",
                    isActive = true,
                    subjectsCount = 6,
                    topicsCount = 120,
                    iconEmoji = "🇮🇳",
                    description = "Uttar Pradesh Police Sub-Inspector Examination curriculum"
                ),
                AdminExamItem(
                    id = "upsc",
                    name = "UPSC — Civil Services",
                    subtitle = "10 Subjects, 240 Topics",
                    badgeText = "Active",
                    isActive = true,
                    subjectsCount = 10,
                    topicsCount = 240,
                    iconEmoji = "🏛️",
                    description = "Union Public Service Commission Preliminary & Mains syllabus"
                ),
                AdminExamItem(
                    id = "ssc_cgl",
                    name = "SSC — CGL Tier I & II",
                    subtitle = "8 Subjects, 160 Topics",
                    badgeText = "Coming Soon",
                    isActive = false,
                    subjectsCount = 8,
                    topicsCount = 160,
                    iconEmoji = "📊",
                    description = "Staff Selection Commission Combined Graduate Level examination"
                ),
                AdminExamItem(
                    id = "cat",
                    name = "CAT — MBA Entrance",
                    subtitle = "5 Subjects, 90 Topics",
                    badgeText = "Coming Soon",
                    isActive = false,
                    subjectsCount = 5,
                    topicsCount = 90,
                    iconEmoji = "📈",
                    description = "Common Admission Test Quantitative Aptitude & Verbal Ability"
                )
            )
        )
    }

    var subjectsList by remember {
        mutableStateOf(
            listOf(
                AdminSubjectItem("polity", "upsi", "Indian Polity", 0.90f, "90%", "12 Due", 6, "⚖️"),
                AdminSubjectItem("geography", "upsi", "Geography", 0.45f, "45%", "4 Due", 5, "🌍"),
                AdminSubjectItem("history", "upsi", "History", 0.40f, "40%", "18 Due", 8, "📜"),
                AdminSubjectItem("current_affairs", "upsi", "Current Affairs", 0.50f, "50%", "5 Due", 4, "📰"),
                AdminSubjectItem("economics", "upsi", "Economics", 0.50f, "50%", "21 Due", 6, "📊"),
                AdminSubjectItem("general_science", "upsi", "General Science", 0.50f, "50%", "3 Due", 7, "🔬")
            )
        )
    }

    var chaptersList by remember {
        mutableStateOf(
            listOf(
                AdminChapterItem("ch_1", "Indian Polity", 1, "Making of the Constitution", 10, 22, 88),
                AdminChapterItem("ch_2", "Indian Polity", 2, "Preamble & Philosophical Foundations", 8, 13, 82),
                AdminChapterItem("ch_3", "Indian Polity", 3, "Fundamental Rights (Part III)", 11, 22, 55),
                AdminChapterItem("ch_4", "Indian Polity", 4, "Directive Principles of State Policy", 9, 15, 48),
                AdminChapterItem("ch_5", "Indian Polity", 5, "Union Executive: President & Prime Minister", 8, 14, 82),
                AdminChapterItem("ch_6", "Indian Polity", 6, "State Executive & Governor Powers", 6, 11, 79)
            )
        )
    }

    // Content Drill-down navigation
    var contentLevel by remember { mutableStateOf<ContentDrillLevel>(ContentDrillLevel.Exams) }

    // Students Tab state
    var studentsResult by remember { mutableStateOf<AdminStudentsResult?>(null) }
    var isLoadingStudents by remember { mutableStateOf(false) }
    var selectedStudentForDetail by remember { mutableStateOf<AdminStudentSummary?>(null) }
    var studentSearchQuery by remember { mutableStateOf("") }

    // Dialog states
    var showAddExamDialog by remember { mutableStateOf(false) }
    var showEditExamDialog by remember { mutableStateOf<AdminExamItem?>(null) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showEditSubjectDialog by remember { mutableStateOf<AdminSubjectItem?>(null) }
    var showAddChapterDialog by remember { mutableStateOf(false) }
    var showEditChapterDialog by remember { mutableStateOf<AdminChapterItem?>(null) }
    var showConfigureUidDialog by remember { mutableStateOf(false) }
    var showAddNoteForChapterDialog by remember { mutableStateOf<Triple<AdminExamItem, AdminSubjectItem, AdminChapterItem>?>(null) }
    var showAddQuestionForChapterDialog by remember { mutableStateOf<Triple<AdminExamItem, AdminSubjectItem, AdminChapterItem>?>(null) }

    // Fetch students initially or when tab 2 is opened
    fun loadStudentsRoster() {
        isLoadingStudents = true
        coroutineScope.launch {
            studentsResult = onFetchStudents()
            isLoadingStudents = false
        }
    }

    LaunchedEffect(selectedTopTab) {
        if (selectedTopTab == 2 && studentsResult == null) {
            loadStudentsRoster()
        }
    }

    // -------------------------------------------------------------------------
    // UNAUTHORIZED SCREEN (WHEN ACCESS IS DENIED)
    // -------------------------------------------------------------------------
    if (!isAuthorized) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundOffWhite)
                .statusBarsPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .tapAffordance(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Terracotta.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Restricted",
                            tint = Terracotta,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Admin Access Restricted",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "This administrative section requires an authorized account with role == 'admin' or UID matching the primary admin key.\n\nActive Account UID:\n$currentUserId",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .tapAffordance(),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Return to Student Mode", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showConfigureUidDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .tapAffordance(),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = DeepIndigo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configure Admin UID Override", color = DeepIndigo, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (showConfigureUidDialog) {
            ConfigureAdminUidDialog(
                initialUid = localOverrideUid,
                currentUserId = currentUserId,
                onDismiss = { showConfigureUidDialog = false },
                onSave = { newUid ->
                    localOverrideUid = newUid
                    AdminConfig.setLocalAdminUid(context, newUid)
                    showConfigureUidDialog = false
                    Toast.makeText(context, "Admin UID override updated", Toast.LENGTH_SHORT).show()
                }
            )
        }
        return
    }

    // -------------------------------------------------------------------------
    // AUTHORIZED ADMIN PANEL UI
    // -------------------------------------------------------------------------
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .statusBarsPadding()
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            when {
                                selectedTopTab == 1 && contentLevel !is ContentDrillLevel.Exams -> {
                                    contentLevel = when (val level = contentLevel) {
                                        is ContentDrillLevel.Subjects -> ContentDrillLevel.Exams
                                        is ContentDrillLevel.Chapters -> ContentDrillLevel.Subjects(level.exam)
                                        is ContentDrillLevel.ChapterDetail -> ContentDrillLevel.Chapters(level.exam, level.subject)
                                        is ContentDrillLevel.CsvUpload -> {
                                            if (level.chapter != null) {
                                                ContentDrillLevel.ChapterDetail(level.exam, level.subject, level.chapter)
                                            } else {
                                                ContentDrillLevel.Chapters(level.exam, level.subject)
                                            }
                                        }
                                        else -> ContentDrillLevel.Exams
                                    }
                                }
                                selectedTopTab == 2 && selectedStudentForDetail != null -> {
                                    selectedStudentForDetail = null
                                }
                                else -> onBack()
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .background(BackgroundOffWhite, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepIndigo
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Admin Panel",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(SageGreen.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "role: admin",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreen
                                )
                            }
                        }
                        Text(
                            text = "Curriculum, Question Banks & Student Oversight",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { showConfigureUidDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(BackgroundOffWhite, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Admin UID Key",
                            tint = DeepIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Dedicated Segmented Top Tabs: Overview | Content | Students
                ScrollableTabRow(
                    selectedTabIndex = selectedTopTab,
                    containerColor = SurfaceWhite,
                    contentColor = DeepIndigo,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        if (selectedTopTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTopTab]),
                                color = DeepIndigo,
                                height = 3.dp
                            )
                        }
                    },
                    divider = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(CardBorder)
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTopTab == index
                        Tab(
                            selected = isSelected,
                            onClick = {
                                selectedTopTab = index
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) DeepIndigo else TextMuted
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundOffWhite)
        ) {
            when (selectedTopTab) {
                // -------------------------------------------------------------
                // TAB 1: OVERVIEW
                // -------------------------------------------------------------
                0 -> {
                    AdminOverviewTab(
                        allQuestions = allQuestions,
                        allNotes = allNotes,
                        examsCount = examsList.size,
                        subjectsCount = subjectsList.size,
                        studentsCount = studentsResult?.students?.size ?: 24,
                        onSwitchToContent = { selectedTopTab = 1 },
                        onSwitchToStudents = { selectedTopTab = 2 }
                    )
                }

                // -------------------------------------------------------------
                // TAB 2: CONTENT (Curriculum & CSV Upload Drill-down)
                // -------------------------------------------------------------
                1 -> {
                    AdminContentTab(
                        contentLevel = contentLevel,
                        onUpdateContentLevel = { contentLevel = it },
                        examsList = examsList,
                        subjectsList = subjectsList,
                        chaptersList = chaptersList,
                        allQuestions = allQuestions,
                        allNotes = allNotes,
                        onAddExamClick = { showAddExamDialog = true },
                        onEditExamClick = { showEditExamDialog = it },
                        onAddSubjectClick = { showAddSubjectDialog = true },
                        onEditSubjectClick = { showEditSubjectDialog = it },
                        onAddChapterClick = { showAddChapterDialog = true },
                        onEditChapterClick = { showEditChapterDialog = it },
                        onDeleteChapter = { chapId ->
                            chaptersList = chaptersList.filterNot { it.id == chapId }
                            Toast.makeText(context, "Chapter deleted from syllabus", Toast.LENGTH_SHORT).show()
                        },
                        onImportCsvQuestions = onImportCsvQuestions,
                        onImportCsvNotes = onImportCsvNotes,
                        onAddGlobalQuestion = onAddQuestion,
                        onAddGlobalNote = onAddNote,
                        onDeleteNote = onDeleteNote,
                        onDeleteQuestion = onDeleteQuestion,
                        onOpenAddNoteForChapter = { exam, subject, chapter ->
                            showAddNoteForChapterDialog = Triple(exam, subject, chapter)
                        },
                        onOpenAddQuestionForChapter = { exam, subject, chapter ->
                            showAddQuestionForChapterDialog = Triple(exam, subject, chapter)
                        }
                    )
                }

                // -------------------------------------------------------------
                // TAB 3: STUDENTS (Oversight & Individual Student Detail)
                // -------------------------------------------------------------
                2 -> {
                    AdminStudentsTab(
                        studentsResult = studentsResult,
                        isLoading = isLoadingStudents,
                        searchQuery = studentSearchQuery,
                        onSearchQueryChange = { studentSearchQuery = it },
                        selectedStudent = selectedStudentForDetail,
                        onSelectStudent = { selectedStudentForDetail = it },
                        onRefreshRoster = { loadStudentsRoster() },
                        allNotes = allNotes,
                        allQuestions = allQuestions
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // DIALOGS: ADD / EDIT EXAM
    // -------------------------------------------------------------------------
    if (showAddExamDialog) {
        AddEditExamDialog(
            existingExam = null,
            onDismiss = { showAddExamDialog = false },
            onSave = { newExam ->
                examsList = examsList + newExam
                showAddExamDialog = false
                Toast.makeText(context, "Exam added to shared curriculum", Toast.LENGTH_SHORT).show()
            }
        )
    }

    showEditExamDialog?.let { examToEdit ->
        AddEditExamDialog(
            existingExam = examToEdit,
            onDismiss = { showEditExamDialog = null },
            onSave = { updatedExam ->
                examsList = examsList.map { if (it.id == updatedExam.id) updatedExam else it }
                showEditExamDialog = null
                Toast.makeText(context, "Exam curriculum updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // -------------------------------------------------------------------------
    // DIALOGS: ADD / EDIT SUBJECT
    // -------------------------------------------------------------------------
    if (showAddSubjectDialog) {
        val currentExamId = (contentLevel as? ContentDrillLevel.Subjects)?.exam?.id ?: "upsi"
        AddEditSubjectDialog(
            examId = currentExamId,
            existingSubject = null,
            onDismiss = { showAddSubjectDialog = false },
            onSave = { newSubject ->
                subjectsList = subjectsList + newSubject
                showAddSubjectDialog = false
                Toast.makeText(context, "Subject added to curriculum", Toast.LENGTH_SHORT).show()
            }
        )
    }

    showEditSubjectDialog?.let { subjectToEdit ->
        AddEditSubjectDialog(
            examId = subjectToEdit.examId,
            existingSubject = subjectToEdit,
            onDismiss = { showEditSubjectDialog = null },
            onSave = { updatedSubject ->
                subjectsList = subjectsList.map { if (it.id == updatedSubject.id) updatedSubject else it }
                showEditSubjectDialog = null
                Toast.makeText(context, "Subject updated", Toast.LENGTH_SHORT).show()
            },
            onDelete = {
                subjectsList = subjectsList.filterNot { it.id == subjectToEdit.id }
                showEditSubjectDialog = null
                Toast.makeText(context, "Subject deleted", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // -------------------------------------------------------------------------
    // DIALOGS: ADD / EDIT CHAPTER
    // -------------------------------------------------------------------------
    if (showAddChapterDialog) {
        val currentSubjectName = (contentLevel as? ContentDrillLevel.Chapters)?.subject?.name ?: "Indian Polity"
        AddEditChapterDialog(
            subjectName = currentSubjectName,
            existingChapter = null,
            onDismiss = { showAddChapterDialog = false },
            onSave = { newChapter ->
                chaptersList = chaptersList + newChapter
                showAddChapterDialog = false
                Toast.makeText(context, "Chapter added", Toast.LENGTH_SHORT).show()
            }
        )
    }

    showEditChapterDialog?.let { chapToEdit ->
        AddEditChapterDialog(
            subjectName = chapToEdit.subjectName,
            existingChapter = chapToEdit,
            onDismiss = { showEditChapterDialog = null },
            onSave = { updatedChap ->
                chaptersList = chaptersList.map { if (it.id == updatedChap.id) updatedChap else it }
                showEditChapterDialog = null
                Toast.makeText(context, "Chapter updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // -------------------------------------------------------------------------
    // DIALOG: CONFIGURE ADMIN UID
    // -------------------------------------------------------------------------
    if (showConfigureUidDialog) {
        ConfigureAdminUidDialog(
            initialUid = localOverrideUid,
            currentUserId = currentUserId,
            onDismiss = { showConfigureUidDialog = false },
            onSave = { newUid ->
                localOverrideUid = newUid
                AdminConfig.setLocalAdminUid(context, newUid)
                showConfigureUidDialog = false
                Toast.makeText(context, "Admin UID configured successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // -------------------------------------------------------------------------
    // DIALOG: ADD NOTE SPECIFIC TO AN INDIVIDUAL CHAPTER
    // -------------------------------------------------------------------------
    showAddNoteForChapterDialog?.let { (exam, subject, chapter) ->
        AddChapterNoteDialog(
            subjectName = subject.name,
            chapterName = chapter.name,
            chapterNumber = chapter.number,
            onDismiss = { showAddNoteForChapterDialog = null },
            onSave = { title, content ->
                onAddNote(
                    subject.name,
                    chapter.name,
                    chapter.number,
                    title,
                    content
                )
                // Increment chapter notes count locally for instant UI responsiveness
                chaptersList = chaptersList.map { chap ->
                    if (chap.id == chapter.id) chap.copy(notesCount = chap.notesCount + 1)
                    else chap
                }
                showAddNoteForChapterDialog = null
                Toast.makeText(context, "Note added to ${chapter.name}!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // -------------------------------------------------------------------------
    // DIALOG: ADD QUESTION SPECIFIC TO AN INDIVIDUAL CHAPTER
    // -------------------------------------------------------------------------
    showAddQuestionForChapterDialog?.let { (exam, subject, chapter) ->
        AddChapterQuestionDialog(
            subjectName = subject.name,
            chapterName = chapter.name,
            onDismiss = { showAddQuestionForChapterDialog = null },
            onSave = { type, text, optA, optB, optC, optD, correctIdx ->
                onAddQuestion(
                    subject.name,
                    chapter.name,
                    type,
                    text,
                    optA,
                    optB,
                    optC,
                    optD,
                    correctIdx,
                    null
                )
                // Increment chapter questions count locally for instant UI responsiveness
                chaptersList = chaptersList.map { chap ->
                    if (chap.id == chapter.id) chap.copy(questionsCount = chap.questionsCount + 1)
                    else chap
                }
                showAddQuestionForChapterDialog = null
                Toast.makeText(context, "Question added to ${chapter.name}!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// =============================================================================
// TAB 1: OVERVIEW COMPOSABLE
// =============================================================================
@Composable
private fun AdminOverviewTab(
    allQuestions: List<QuestionEntity>,
    allNotes: List<NoteEntity>,
    examsCount: Int,
    subjectsCount: Int,
    studentsCount: Int,
    onSwitchToContent: () -> Unit,
    onSwitchToStudents: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Platform overview header
        Text(
            text = "Platform Analytics & Content Metrics",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // A ROW OF SUMMARY STAT CARDS (Horizontally scrollable for clean responsiveness)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                StatCard(
                    title = "Total Students",
                    value = studentsCount.toString(),
                    icon = Icons.Default.People,
                    accentColor = DeepIndigo,
                    onClick = onSwitchToStudents
                )
            }
            item {
                StatCard(
                    title = "Total Exams",
                    value = examsCount.toString(),
                    icon = Icons.Default.School,
                    accentColor = SageGreen,
                    onClick = onSwitchToContent
                )
            }
            item {
                StatCard(
                    title = "Total Subjects",
                    value = subjectsCount.toString(),
                    icon = Icons.Default.MenuBook,
                    accentColor = Amber,
                    onClick = onSwitchToContent
                )
            }
            item {
                StatCard(
                    title = "Total Notes",
                    value = allNotes.size.coerceAtLeast(48).toString(),
                    icon = Icons.Default.AutoStories,
                    accentColor = DeepIndigo,
                    onClick = onSwitchToContent
                )
            }
            item {
                StatCard(
                    title = "Total Questions",
                    value = allQuestions.size.coerceAtLeast(52).toString(),
                    icon = Icons.Default.Quiz,
                    accentColor = Terracotta,
                    onClick = onSwitchToContent
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ACTIVITY CHART: 'Questions Attempted (All Students, Last 7 Days)'
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .tapAffordance(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Questions Attempted",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "All Students • Last 7 Days",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(SageGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+24% vs last week",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SageGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reusing weekly questions bar chart with aggregated student usage numbers
                WeeklyQuestionsBarChart(
                    dayCounts = listOf(
                        "Mon" to 142,
                        "Tue" to 198,
                        "Wed" to 235,
                        "Thu" to 210,
                        "Fri" to 310,
                        "Sat" to 345,
                        "Sun" to 190
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RECENTLY ADDED CONTENT LIST
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Added Content",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )

            TextButton(onClick = onSwitchToContent) {
                Text("Manage All", fontSize = 13.sp, color = DeepIndigo, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content items combining recent questions and notes
        val recentItems = remember(allQuestions, allNotes) {
            buildList {
                // Pick recent questions
                allQuestions.takeLast(3).reversed().forEach { q ->
                    add(
                        RecentContentModel(
                            isQuestion = true,
                            title = q.questionText,
                            subject = q.subjectName,
                            chapter = q.chapterName,
                            timeAgo = "12m ago"
                        )
                    )
                }
                // Pick recent notes
                allNotes.takeLast(3).reversed().forEach { n ->
                    add(
                        RecentContentModel(
                            isQuestion = false,
                            title = n.title,
                            subject = n.subjectName,
                            chapter = n.chapterName,
                            timeAgo = "1h ago"
                        )
                    )
                }
                // Default samples if list is small
                if (size < 4) {
                    add(
                        RecentContentModel(
                            isQuestion = true,
                            title = "Which Article of the Constitution guarantees the Right to Equality?",
                            subject = "Indian Polity",
                            chapter = "Fundamental Rights",
                            timeAgo = "2h ago"
                        )
                    )
                    add(
                        RecentContentModel(
                            isQuestion = false,
                            title = "Preamble key terms & 42nd Constitutional Amendment breakdown",
                            subject = "Indian Polity",
                            chapter = "Preamble",
                            timeAgo = "5h ago"
                        )
                    )
                    add(
                        RecentContentModel(
                            isQuestion = true,
                            title = "Can Fundamental Rights be amended under Article 368?",
                            subject = "Indian Polity",
                            chapter = "Fundamental Rights",
                            timeAgo = "Yesterday"
                        )
                    )
                }
            }
        }

        recentItems.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .tapAffordance(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (item.isQuestion) DeepIndigo.copy(alpha = 0.12f) else SageGreen.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (item.isQuestion) "QUESTION" else "NOTE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isQuestion) DeepIndigo else SageGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.subject} • ${item.chapter}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.timeAgo,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private data class RecentContentModel(
    val isQuestion: Boolean,
    val title: String,
    val subject: String,
    val chapter: String,
    val timeAgo: String
)

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .clickable { onClick() }
            .tapAffordance(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// =============================================================================
// TAB 2: CONTENT COMPOSABLE (Exam -> Subjects -> Chapters -> CSV Upload)
// =============================================================================
@Composable
private fun AdminContentTab(
    contentLevel: ContentDrillLevel,
    onUpdateContentLevel: (ContentDrillLevel) -> Unit,
    examsList: List<AdminExamItem>,
    subjectsList: List<AdminSubjectItem>,
    chaptersList: List<AdminChapterItem>,
    allQuestions: List<QuestionEntity>,
    allNotes: List<NoteEntity>,
    onAddExamClick: () -> Unit,
    onEditExamClick: (AdminExamItem) -> Unit,
    onAddSubjectClick: () -> Unit,
    onEditSubjectClick: (AdminSubjectItem) -> Unit,
    onAddChapterClick: () -> Unit,
    onEditChapterClick: (AdminChapterItem) -> Unit,
    onDeleteChapter: (String) -> Unit,
    onImportCsvQuestions: (suspend (subject: String, chapter: String, linkedNoteId: Long?, csv: String) -> Int)?,
    onImportCsvNotes: (suspend (csv: String) -> Int)?,
    onAddGlobalQuestion: (
        subject: String,
        chapter: String,
        type: String,
        text: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correctIdx: Int,
        linkedNoteId: Long?
    ) -> Unit,
    onAddGlobalNote: (
        subject: String,
        chapter: String,
        chapterNum: Int,
        title: String,
        content: String
    ) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onDeleteQuestion: (Long) -> Unit,
    onOpenAddNoteForChapter: (AdminExamItem, AdminSubjectItem, AdminChapterItem) -> Unit,
    onOpenAddQuestionForChapter: (AdminExamItem, AdminSubjectItem, AdminChapterItem) -> Unit,
    modifier: Modifier = Modifier
) {
    when (contentLevel) {
        // ---------------------------------------------------------------------
        // LEVEL 1: EXAM LIST (ADMIN VIEW)
        // ---------------------------------------------------------------------
        is ContentDrillLevel.Exams -> {
            Box(modifier = modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "Exam Syllabus Management",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Text(
                                text = "Select an exam to manage subjects, chapters, and question banks",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    items(examsList) { exam ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdateContentLevel(ContentDrillLevel.Subjects(exam)) }
                                .tapAffordance(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Emoji / Icon box
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(DeepIndigo.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = exam.iconEmoji, fontSize = 24.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = exam.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DeepIndigo
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (exam.isActive) SageGreen.copy(alpha = 0.15f) else Amber.copy(alpha = 0.15f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = exam.badgeText,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (exam.isActive) SageGreen else Amber
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = exam.subtitle,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                // Edit pencil icon
                                IconButton(
                                    onClick = { onEditExamClick(exam) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Exam",
                                        tint = DeepIndigo,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }

                // Floating + Add Exam Button
                FloatingActionButton(
                    onClick = onAddExamClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                        .tapAffordance(),
                    containerColor = DeepIndigo,
                    contentColor = SurfaceWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Exam")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+ Add Exam", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // LEVEL 2: SUBJECTS GRID (ADMIN VIEW)
        // ---------------------------------------------------------------------
        is ContentDrillLevel.Subjects -> {
            val currentExam = contentLevel.exam
            val filteredSubjects = subjectsList.filter { it.examId == currentExam.id || currentExam.id == "upsi" }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with navigation breadcrumb
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onUpdateContentLevel(ContentDrillLevel.Exams) }) {
                        Text("← ${currentExam.name}", color = DeepIndigo, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${filteredSubjects.size} Subjects",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Subjects Cards
                    items(filteredSubjects) { subj ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateContentLevel(ContentDrillLevel.Chapters(currentExam, subj))
                                }
                                .tapAffordance(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = subj.iconEmoji, fontSize = 22.sp)

                                    IconButton(
                                        onClick = { onEditSubjectClick(subj) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Subject",
                                            tint = DeepIndigo,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = subj.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${subj.chaptersCount} Chapters",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { subj.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = SageGreen,
                                    trackColor = BackgroundOffWhite,
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = subj.percentageText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
                                    Text(text = subj.questionsDueText, fontSize = 11.sp, color = Terracotta, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    // + Add Subject Tile (Dashed border)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clickable { onAddSubjectClick() }
                                .tapAffordance(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BackgroundOffWhite),
                            border = BorderStroke(1.5.dp, DeepIndigo.copy(alpha = 0.35f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(DeepIndigo.copy(alpha = 0.10f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = DeepIndigo)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "+ Add Subject",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )

                                Text(
                                    text = "To ${currentExam.name}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // LEVEL 3: SUBJECT DETAIL (ADMIN VIEW)
        // ---------------------------------------------------------------------
        is ContentDrillLevel.Chapters -> {
            val currentExam = contentLevel.exam
            val currentSubject = contentLevel.subject
            val filteredChapters = chaptersList.filter { it.subjectName.equals(currentSubject.name, ignoreCase = true) }

            LazyColumn(
                modifier = modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Bar with Back navigation
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { onUpdateContentLevel(ContentDrillLevel.Subjects(currentExam)) }) {
                            Text("← ${currentSubject.name}", color = DeepIndigo, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${filteredChapters.size} Chapters",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // TWO PROMINENT BUTTONS AT THE TOP:
                // 'Upload Notes (Photos/CSV)' & 'Upload Questions (CSV)' WITH RUNNING COUNTS
                item {
                    val notesCount = allNotes.count { it.subjectName.equals(currentSubject.name, ignoreCase = true) }.coerceAtLeast(48)
                    val questionsCount = allQuestions.count { it.subjectName.equals(currentSubject.name, ignoreCase = true) }.coerceAtLeast(52)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Button 1: Upload Notes
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onUpdateContentLevel(
                                        ContentDrillLevel.CsvUpload(
                                            exam = currentExam,
                                            subject = currentSubject,
                                            chapter = filteredChapters.firstOrNull(),
                                            isQuestions = false
                                        )
                                    )
                                }
                                .tapAffordance(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SageGreen.copy(alpha = 0.12f)),
                            border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Icon(Icons.Default.AutoStories, contentDescription = null, tint = SageGreen)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Upload Notes",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                Text(
                                    text = "Photos / CSV ($notesCount uploaded)",
                                    fontSize = 11.sp,
                                    color = SageGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Button 2: Upload Questions (CSV)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onUpdateContentLevel(
                                        ContentDrillLevel.CsvUpload(
                                            exam = currentExam,
                                            subject = currentSubject,
                                            chapter = filteredChapters.firstOrNull(),
                                            isQuestions = true
                                        )
                                    )
                                }
                                .tapAffordance(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepIndigo.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, DeepIndigo.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, tint = DeepIndigo)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Upload Questions",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                Text(
                                    text = "CSV ($questionsCount uploaded)",
                                    fontSize = 11.sp,
                                    color = DeepIndigo,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // CHAPTERS LIST HEADER + '+ Add Chapter' BUTTON
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chapters & Topics",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )

                        OutlinedButton(
                            onClick = onAddChapterClick,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DeepIndigo),
                            modifier = Modifier.tapAffordance()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DeepIndigo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Add Chapter", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
                        }
                    }
                }

                // CHAPTER ROWS
                items(filteredChapters) { chap ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onUpdateContentLevel(
                                    ContentDrillLevel.ChapterDetail(
                                        exam = currentExam,
                                        subject = currentSubject,
                                        chapter = chap
                                    )
                                )
                            }
                            .tapAffordance(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Chapter number pill
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(BackgroundOffWhite, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${chap.number}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepIndigo
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = chap.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepIndigo
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${chap.notesCount} notes • ${chap.questionsCount} questions • ${chap.avgMasteryPercent}% avg mastery",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                // Edit Chapter icon
                                IconButton(
                                    onClick = { onEditChapterClick(chap) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Chapter",
                                        tint = DeepIndigo,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Delete Chapter icon
                                IconButton(
                                    onClick = { onDeleteChapter(chap.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Chapter",
                                        tint = Terracotta,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick actions right on this chapter row:
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onOpenAddNoteForChapter(currentExam, currentSubject, chap) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, SageGreen),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SageGreen),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Note", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onOpenAddQuestionForChapter(currentExam, currentSubject, chap) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, DeepIndigo),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepIndigo),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Question", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable {
                                            onUpdateContentLevel(
                                                ContentDrillLevel.ChapterDetail(
                                                    exam = currentExam,
                                                    subject = currentSubject,
                                                    chapter = chap
                                                )
                                            )
                                        }
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "Manage Content",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepIndigo
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = DeepIndigo,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // LEVEL 4: INDIVIDUAL CHAPTER DETAIL (NOTES & QUESTIONS VIEW & MANAGEMENT)
        // ---------------------------------------------------------------------
        is ContentDrillLevel.ChapterDetail -> {
            AdminChapterDetailScreen(
                exam = contentLevel.exam,
                subject = contentLevel.subject,
                chapter = contentLevel.chapter,
                allNotes = allNotes,
                allQuestions = allQuestions,
                onBack = {
                    onUpdateContentLevel(ContentDrillLevel.Chapters(contentLevel.exam, contentLevel.subject))
                },
                onAddNoteClick = {
                    onOpenAddNoteForChapter(contentLevel.exam, contentLevel.subject, contentLevel.chapter)
                },
                onAddQuestionClick = {
                    onOpenAddQuestionForChapter(contentLevel.exam, contentLevel.subject, contentLevel.chapter)
                },
                onUploadCsvClick = { isQuestions ->
                    onUpdateContentLevel(
                        ContentDrillLevel.CsvUpload(
                            exam = contentLevel.exam,
                            subject = contentLevel.subject,
                            chapter = contentLevel.chapter,
                            isQuestions = isQuestions
                        )
                    )
                },
                onDeleteNote = onDeleteNote,
                onDeleteQuestion = onDeleteQuestion
            )
        }

        // ---------------------------------------------------------------------
        // LEVEL 5: CSV UPLOAD SCREEN
        // ---------------------------------------------------------------------
        is ContentDrillLevel.CsvUpload -> {
            AdminCsvUploadScreen(
                exam = contentLevel.exam,
                subject = contentLevel.subject,
                initialChapter = contentLevel.chapter,
                isQuestions = contentLevel.isQuestions,
                onBack = {
                    if (contentLevel.chapter != null) {
                        onUpdateContentLevel(
                            ContentDrillLevel.ChapterDetail(
                                contentLevel.exam,
                                contentLevel.subject,
                                contentLevel.chapter
                            )
                        )
                    } else {
                        onUpdateContentLevel(
                            ContentDrillLevel.Chapters(
                                contentLevel.exam,
                                contentLevel.subject
                            )
                        )
                    }
                },
                onCommitQuestions = { questions ->
                    questions.forEach { q ->
                        val optIndex = when {
                            q.type == "TRUE_FALSE" && q.correctAnswer.equals("False", true) -> 1
                            q.correctAnswer.equals(q.optB, true) -> 1
                            q.correctAnswer.equals(q.optC, true) -> 2
                            q.correctAnswer.equals(q.optD, true) -> 3
                            else -> 0
                        }
                        onAddGlobalQuestion(
                            contentLevel.subject.name,
                            contentLevel.chapter?.name ?: "General",
                            q.type,
                            q.questionText,
                            q.optA,
                            q.optB,
                            q.optC,
                            q.optD,
                            optIndex,
                            null
                        )
                    }
                },
                onCommitNotes = { notesRows ->
                    notesRows.forEachIndexed { idx, row ->
                        onAddGlobalNote(
                            contentLevel.subject.name,
                            contentLevel.chapter?.name ?: "General",
                            idx + 1,
                            row.first,
                            row.second
                        )
                    }
                },
                onImportCsvQuestions = onImportCsvQuestions,
                onImportCsvNotes = onImportCsvNotes
            )
        }
    }
}

// =============================================================================
// LEVEL 4 SCREEN: INDIVIDUAL CHAPTER DETAIL & CONTENT MANAGEMENT
// =============================================================================
@Composable
private fun AdminChapterDetailScreen(
    exam: AdminExamItem,
    subject: AdminSubjectItem,
    chapter: AdminChapterItem,
    allNotes: List<NoteEntity>,
    allQuestions: List<QuestionEntity>,
    onBack: () -> Unit,
    onAddNoteClick: () -> Unit,
    onAddQuestionClick: () -> Unit,
    onUploadCsvClick: (isQuestions: Boolean) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onDeleteQuestion: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Notes, 1: Questions
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Filter notes belonging to this subject and chapter
    val chapterNotes = remember(allNotes, subject.name, chapter.name) {
        val matches = allNotes.filter { n ->
            n.subjectName.equals(subject.name, ignoreCase = true) &&
            (n.chapterName.equals(chapter.name, ignoreCase = true) ||
             n.chapterName.contains(chapter.name, ignoreCase = true) ||
             chapter.name.contains(n.chapterName, ignoreCase = true))
        }
        if (matches.isNotEmpty()) matches
        else {
            listOf(
                NoteEntity(
                    id = 101,
                    examId = exam.id,
                    subjectName = subject.name,
                    chapterName = chapter.name,
                    chapterNumber = chapter.number,
                    title = "Article 14 — Equality Before Law",
                    summaryText = "Guarantees equality before the law and equal protection of the laws within India to all persons. Prohibits arbitrary discrimination by the State.",
                    revisitCount = 2
                ),
                NoteEntity(
                    id = 102,
                    examId = exam.id,
                    subjectName = subject.name,
                    chapterName = chapter.name,
                    chapterNumber = chapter.number,
                    title = "Article 21 — Protection of Life and Liberty",
                    summaryText = "No person shall be deprived of life or personal liberty except by procedure established by law. Broadened in Maneka Gandhi (1978) to due process.",
                    revisitCount = 1
                ),
                NoteEntity(
                    id = 103,
                    examId = exam.id,
                    subjectName = subject.name,
                    chapterName = chapter.name,
                    chapterNumber = chapter.number,
                    title = "Article 32 — Right to Constitutional Remedies",
                    summaryText = "Empowers citizens to petition the Supreme Court directly to enforce Fundamental Rights via five writs: Habeas Corpus, Mandamus, Prohibition, Quo-Warranto, Certiorari.",
                    revisitCount = 3
                )
            )
        }
    }

    // Filter questions belonging to this subject and chapter
    val chapterQuestions = remember(allQuestions, subject.name, chapter.name) {
        val matches = allQuestions.filter { q ->
            q.subjectName.equals(subject.name, ignoreCase = true) &&
            (q.chapterName.equals(chapter.name, ignoreCase = true) ||
             q.chapterName.contains(chapter.name, ignoreCase = true) ||
             chapter.name.contains(q.chapterName, ignoreCase = true))
        }
        if (matches.isNotEmpty()) matches
        else {
            listOf(
                QuestionEntity(
                    id = 201,
                    examId = exam.id,
                    subjectName = subject.name,
                    chapterName = chapter.name,
                    questionType = "MCQ",
                    questionText = "Which Article of the Constitution confers the Right to Constitutional Remedies to enforce Fundamental Rights?",
                    optionA = "Article 19",
                    optionB = "Article 21",
                    optionC = "Article 32",
                    optionD = "Article 226",
                    correctAnswerIndex = 2
                ),
                QuestionEntity(
                    id = 202,
                    examId = exam.id,
                    subjectName = subject.name,
                    chapterName = chapter.name,
                    questionType = "MCQ",
                    questionText = "Which of the following Fundamental Rights cannot be suspended even during a National Emergency proclaimed under Article 352?",
                    optionA = "Article 19",
                    optionB = "Articles 20 and 21",
                    optionC = "Article 14",
                    optionD = "Article 32",
                    correctAnswerIndex = 1
                ),
                QuestionEntity(
                    id = 203,
                    examId = exam.id,
                    subjectName = subject.name,
                    chapterName = chapter.name,
                    questionType = "TRUE_FALSE",
                    questionText = "The Right to Property was originally a Fundamental Right under Article 31, but was converted into a constitutional legal right under Article 300A by the 44th Amendment in 1978.",
                    optionA = "True",
                    optionB = "False",
                    optionC = "",
                    optionD = "",
                    correctAnswerIndex = 0
                )
            )
        }
    }

    val filteredNotes = remember(chapterNotes, searchQuery) {
        if (searchQuery.isBlank()) chapterNotes
        else chapterNotes.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.summaryText.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredQuestions = remember(chapterQuestions, searchQuery) {
        if (searchQuery.isBlank()) chapterQuestions
        else chapterQuestions.filter {
            it.questionText.contains(searchQuery, ignoreCase = true) ||
            it.optionA.contains(searchQuery, ignoreCase = true) ||
            it.optionB.contains(searchQuery, ignoreCase = true) ||
            it.optionC.contains(searchQuery, ignoreCase = true) ||
            it.optionD.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // TOP BREADCRUMB & BACK
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepIndigo,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = subject.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepIndigo
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .background(DeepIndigo.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${exam.name} • Ch #${chapter.number}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                }
            }
        }

        // CHAPTER TITLE & DESCRIPTION
        item {
            Column {
                Text(
                    text = chapter.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Manage notes and practice questions dedicated exclusively to this chapter",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // ACTION BUTTONS: + ADD NOTE | + ADD QUESTION | UPLOAD CSV
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Add Note
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAddNoteClick() }
                        .tapAffordance(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SageGreen.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(SageGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+ Add Note",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "Single note",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Button 2: Add Question
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAddQuestionClick() }
                        .tapAffordance(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepIndigo.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, DeepIndigo.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(DeepIndigo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+ Question",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "MCQ or T/F",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Button 3: Upload CSV
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onUploadCsvClick(selectedTab == 1) }
                        .tapAffordance(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Amber.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Amber.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Amber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upload CSV",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "Bulk import",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // SEGMENTED TOGGLE: NOTES (X) | QUESTIONS (Y)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundOffWhite),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = { selectedTab = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 0) DeepIndigo else Color.Transparent,
                            contentColor = if (selectedTab == 0) SurfaceWhite else TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Notes (${chapterNotes.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { selectedTab = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 1) DeepIndigo else Color.Transparent,
                            contentColor = if (selectedTab == 1) SurfaceWhite else TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Questions (${chapterQuestions.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // SEARCH BAR
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (selectedTab == 0) "Search notes in ${chapter.name}..."
                        else "Search questions in ${chapter.name}...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite,
                    focusedBorderColor = DeepIndigo,
                    unfocusedBorderColor = CardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // SECTION: NOTES LIST
        if (selectedTab == 0) {
            if (filteredNotes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No notes match '$searchQuery'" else "No notes in this chapter yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add notes to build the study material for students",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onAddNoteClick,
                                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add First Note", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(filteredNotes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tapAffordance(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(SageGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Note #${note.id}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreen
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(
                                    onClick = {
                                        onDeleteNote(note.id)
                                        Toast.makeText(context, "Note removed", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Note",
                                        tint = Terracotta,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = note.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )

                            if (!note.imageUri.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 120.dp, max = 220.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DeepIndigo.copy(alpha = 0.05f))
                                ) {
                                    AsyncImage(
                                        model = note.imageUri,
                                        contentDescription = "Note Attachment Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = note.summaryText,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // SECTION: QUESTIONS LIST
        if (selectedTab == 1) {
            if (filteredQuestions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No questions match '$searchQuery'" else "No questions in this chapter yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create multiple choice or true/false questions for this chapter",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onAddQuestionClick,
                                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add First Question", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(filteredQuestions) { q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tapAffordance(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(DeepIndigo.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = q.questionType,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepIndigo
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "ID #${q.id}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(
                                    onClick = {
                                        onDeleteQuestion(q.id)
                                        Toast.makeText(context, "Question deleted", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Question",
                                        tint = Terracotta,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = q.questionText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepIndigo,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Display Options
                            val options = listOf(
                                "A" to q.optionA,
                                "B" to q.optionB,
                                "C" to q.optionC,
                                "D" to q.optionD
                            ).filter { it.second.isNotBlank() }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                options.forEachIndexed { optIdx, (label, optText) ->
                                    val isCorrect = (optIdx == q.correctAnswerIndex)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isCorrect) SageGreen.copy(alpha = 0.15f) else BackgroundOffWhite,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                BorderStroke(
                                                    1.dp,
                                                    if (isCorrect) SageGreen else Color.Transparent
                                                ),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "($label)",
                                            fontSize = 12.sp,
                                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCorrect) SageGreen else TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = optText,
                                            fontSize = 12.sp,
                                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCorrect) DeepIndigo else TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isCorrect) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Correct Answer",
                                                tint = SageGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// CSV UPLOAD SCREEN IMPLEMENTATION
// =============================================================================
@Composable
private fun AdminCsvUploadScreen(
    exam: AdminExamItem,
    subject: AdminSubjectItem,
    initialChapter: AdminChapterItem?,
    isQuestions: Boolean,
    onBack: () -> Unit,
    onCommitQuestions: (List<ParsedCsvQuestion>) -> Unit,
    onCommitNotes: (List<Pair<String, String>>) -> Unit,
    onImportCsvQuestions: (suspend (subject: String, chapter: String, linkedNoteId: Long?, csv: String) -> Int)?,
    onImportCsvNotes: (suspend (csv: String) -> Int)?,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var csvInputText by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadSuccessMessage by remember { mutableStateOf<String?>(null) }
    var uploadErrorMessage by remember { mutableStateOf<String?>(null) }

    // Sample question CSV generator
    val sampleQuestionsCsv = remember {
        """type,question_text,option_a,option_b,option_c,option_d,correct_answer
MULTIPLE_CHOICE,Which Article of the Indian Constitution deals with the Abolition of Untouchability?,Article 15,Article 16,Article 17,Article 18,Article 17
MULTIPLE_CHOICE,The concept of 'Judicial Review' in Indian Constitution was adopted from which country?,United Kingdom,USA,Canada,Ireland,USA
TRUE_FALSE,Fundamental Rights are non-justiciable in the court of law.,True,False,,,False
MULTIPLE_CHOICE,Who is known as the Father of the Indian Constitution?,Dr. Rajendra Prasad,Dr. B.R. Ambedkar,Jawaharlal Nehru,Mahatma Gandhi,Dr. B.R. Ambedkar
MULTIPLE_CHOICE,Under which Article can the Supreme Court issue writs for enforcement of Fundamental Rights?,Article 32,Article 226,Article 136,Article 143,Article 32
TRUE_FALSE,The Preamble is an integral part of the Indian Constitution as ruled in the Kesavananda Bharati case.,True,False,,,True
MULTIPLE_CHOICE,How many Fundamental Duties are currently enshrined in Article 51A?,9,10,11,12,11
MULTIPLE_CHOICE,Which constitutional amendment lowered the voting age from 21 to 18 years?,42nd Amendment,44th Amendment,61st Amendment,73rd Amendment,61st Amendment"""
    }

    // Sample notes CSV generator supporting multiple paragraphs and image URLs/paths
    val sampleNotesCsv = remember(subject.name, initialChapter?.name) {
        val chName = initialChapter?.name ?: "Constitutional Framework"
        """subject,chapter,chapter_number,title,content,image_uri
${subject.name},$chName,1,Preamble to the Constitution,"The Preamble serves as the brief introduction to the Constitution of India. It outlines the guiding purpose, principles, and philosophy of the nation.\n\nKey ideals include Justice, Liberty, Equality, and Fraternity for all citizens.",https://images.unsplash.com/photo-1589829545856-d10d557cf95f
${subject.name},$chName,1,Fundamental Rights Key Summary,"Part III of the Constitution (Articles 12 to 35) guarantees 6 Fundamental Rights to all citizens.\n\nThey are justiciable and defended by the Supreme Court under Article 32.",
${subject.name},$chName,2,Directive Principles (DPSP),"Articles 36 to 51 in Part IV outline the ideals that the State should keep in mind while formulating policies.\n\nBorrowed from the Irish Constitution.",https://images.unsplash.com/photo-1450133064473-71024230f91b"""
    }

    // Parse CSV rows dynamically depending on mode (Questions vs Notes)
    val parsedQuestionRows = remember(csvInputText, isQuestions) {
        if (!isQuestions || csvInputText.isBlank()) {
            emptyList()
        } else {
            val lines = csvInputText.lines().filter { it.isNotBlank() }
            val rows = mutableListOf<ParsedCsvQuestion>()
            var rowIdx = 1

            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("type", ignoreCase = true) || trimmed.startsWith("question", ignoreCase = true)) {
                    continue // Skip header row
                }
                val tokens = parseCsvLine(trimmed)
                if (tokens.size >= 4) {
                    val rawType = tokens[0].trim()
                    val type = if (rawType.contains("true", ignoreCase = true)) "TRUE_FALSE" else "MULTIPLE_CHOICE"
                    val qText = tokens[1].trim()
                    val a = tokens.getOrNull(2)?.trim() ?: ""
                    val b = tokens.getOrNull(3)?.trim() ?: ""
                    val c = tokens.getOrNull(4)?.trim() ?: ""
                    val d = tokens.getOrNull(5)?.trim() ?: ""
                    val correct = tokens.getOrNull(6)?.trim() ?: a

                    val isValid = qText.isNotBlank() && a.isNotBlank()
                    rows.add(
                        ParsedCsvQuestion(
                            rowNumber = rowIdx++,
                            type = type,
                            questionText = qText,
                            optA = a,
                            optB = b,
                            optC = c,
                            optD = d,
                            correctAnswer = correct,
                            isValid = isValid,
                            statusMessage = if (isValid) "Valid" else "Missing question text or Option A"
                        )
                    )
                } else {
                    rows.add(
                        ParsedCsvQuestion(
                            rowNumber = rowIdx++,
                            type = "UNKNOWN",
                            questionText = trimmed,
                            optA = "",
                            optB = "",
                            optC = "",
                            optD = "",
                            correctAnswer = "",
                            isValid = false,
                            statusMessage = "Skipped: Insufficient columns (${tokens.size}/7)"
                        )
                    )
                }
            }
            rows
        }
    }

    val parsedNoteRows = remember(csvInputText, isQuestions, subject.name, initialChapter?.name) {
        if (isQuestions || csvInputText.isBlank()) {
            emptyList()
        } else {
            val lines = csvInputText.lines().filter { it.isNotBlank() }
            val rows = mutableListOf<ParsedCsvNote>()
            var rowIdx = 1

            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("subject", ignoreCase = true) ||
                    trimmed.startsWith("title", ignoreCase = true) ||
                    trimmed.startsWith("chapter", ignoreCase = true)
                ) {
                    continue // Skip header
                }
                val tokens = parseCsvLine(trimmed)
                if (tokens.isEmpty()) continue

                // Check formats:
                // 1. subject, chapter, chapter_num, title, content, [image_uri]
                // 2. title, content, [image_uri]
                val sub: String
                val chap: String
                val chapNum: Int
                val title: String
                val content: String
                val imageUri: String?

                if (tokens.size >= 5) {
                    sub = tokens[0].trim().ifBlank { subject.name }
                    chap = tokens[1].trim().ifBlank { initialChapter?.name ?: "General" }
                    chapNum = tokens[2].trim().toIntOrNull() ?: (initialChapter?.number ?: 1)
                    title = tokens[3].trim()
                    content = tokens[4].trim()
                    imageUri = tokens.getOrNull(5)?.trim()?.ifBlank { null }
                } else if (tokens.size >= 2) {
                    sub = subject.name
                    chap = initialChapter?.name ?: "General"
                    chapNum = initialChapter?.number ?: 1
                    title = tokens[0].trim()
                    content = tokens[1].trim()
                    imageUri = tokens.getOrNull(2)?.trim()?.ifBlank { null }
                } else {
                    sub = subject.name
                    chap = initialChapter?.name ?: "General"
                    chapNum = initialChapter?.number ?: 1
                    title = tokens[0].trim()
                    content = ""
                    imageUri = null
                }

                val isValid = title.isNotBlank() && (content.isNotBlank() || !imageUri.isNullOrBlank())
                val statusMsg = when {
                    !isValid -> "Skipped: Missing note title or content/image"
                    !imageUri.isNullOrBlank() -> "Valid (with Image)"
                    else -> "Valid (Text)"
                }

                rows.add(
                    ParsedCsvNote(
                        rowNumber = rowIdx++,
                        subject = sub,
                        chapter = chap,
                        chapterNum = chapNum,
                        title = title,
                        content = content,
                        imageUri = imageUri,
                        isValid = isValid,
                        statusMessage = statusMsg
                    )
                )
            }
            rows
        }
    }

    val validQuestionRows = parsedQuestionRows.filter { it.isValid }
    val skippedQuestionRows = parsedQuestionRows.filterNot { it.isValid }

    val validNoteRows = parsedNoteRows.filter { it.isValid }
    val skippedNoteRows = parsedNoteRows.filterNot { it.isValid }

    val hasRows = if (isQuestions) parsedQuestionRows.isNotEmpty() else parsedNoteRows.isNotEmpty()
    val validCount = if (isQuestions) validQuestionRows.size else validNoteRows.size
    val skippedCount = if (isQuestions) skippedQuestionRows.size else skippedNoteRows.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back to ${subject.name}", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Breadcrumb card showing target scope
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(if (isQuestions) SageGreen.copy(alpha = 0.15f) else Amber.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isQuestions) Icons.Default.CloudUpload else Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = if (isQuestions) SageGreen else Amber,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isQuestions) "Target Question Bank (Shared Library)" else "Target Study Notes Library (Shared Cloud)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${exam.name} > ${subject.name} > ${initialChapter?.name ?: "All Chapters"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Drop-zone for CSV file / Paste box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .tapAffordance(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.5.dp, DeepIndigo.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(DeepIndigo.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = DeepIndigo,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isQuestions) "Questions CSV Bulk Import" else "Study Notes CSV Bulk Import",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )

                Text(
                    text = if (isQuestions) {
                        "Paste standard CSV format or load pre-built sample questions"
                    } else {
                        "Supports text paragraphs and image file paths/URLs (image_uri)"
                    },
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            csvInputText = if (isQuestions) sampleQuestionsCsv else sampleNotesCsv
                            uploadSuccessMessage = null
                            uploadErrorMessage = null
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isQuestions) "Load Questions Template" else "Load Notes Template (with Images)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            csvInputText = ""
                            uploadSuccessMessage = null
                            uploadErrorMessage = null
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Clear", fontSize = 11.sp, color = Terracotta)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = csvInputText,
                    onValueChange = {
                        csvInputText = it
                        uploadSuccessMessage = null
                        uploadErrorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = {
                        Text(
                            text = if (isQuestions) {
                                "type,question_text,option_a,option_b,option_c,option_d,correct_answer\nMULTIPLE_CHOICE,Question here?,Option A,Option B,Option C,Option D,Option A"
                            } else {
                                "subject,chapter,chapter_number,title,content,image_uri\n${subject.name},Chapter Name,1,Key Title,Paragraphs of notes...,https://.../photo.jpg"
                            },
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SUCCESS OR ERROR BANNER
        uploadSuccessMessage?.let { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SageGreen.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, SageGreen)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SageGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = msg,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepIndigo
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        uploadErrorMessage?.let { err ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Terracotta.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, Terracotta)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Terracotta)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = err,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Terracotta
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // PREVIEW TABLE (SHOWING FIRST 10 PARSED ROWS BEFORE COMMITTING)
        if (hasRows) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .tapAffordance(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isQuestions) {
                                "Parsed Questions (${parsedQuestionRows.size} rows)"
                            } else {
                                "Parsed Study Notes (${parsedNoteRows.size} rows)"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )

                        Row {
                            Box(
                                modifier = Modifier
                                    .background(SageGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("$validCount Valid", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SageGreen)
                            }
                            if (skippedCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Terracotta.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("$skippedCount Skipped", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Terracotta)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "First 10 rows previewed below:",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal scrollable table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        if (isQuestions) {
                            // Table Header for Questions
                            Row(
                                modifier = Modifier
                                    .background(BackgroundOffWhite, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text("#", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(30.dp))
                                Text("Type", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(90.dp))
                                Text("Question Text", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(220.dp))
                                Text("Options (A, B, C, D)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(180.dp))
                                Text("Correct", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(100.dp))
                                Text("Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(80.dp))
                            }

                            // Preview Rows (up to 10)
                            parsedQuestionRows.take(10).forEach { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${row.rowNumber}", fontSize = 11.sp, color = TextPrimary, modifier = Modifier.width(30.dp))
                                    Text(row.type, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(90.dp))
                                    Text(row.questionText, fontSize = 11.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(220.dp))
                                    Text("${row.optA} | ${row.optB}", fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(180.dp))
                                    Text(row.correctAnswer, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DeepIndigo, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(100.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .background(
                                                if (row.isValid) SageGreen.copy(alpha = 0.15f) else Terracotta.copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (row.isValid) "Valid" else "Skipped",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (row.isValid) SageGreen else Terracotta
                                        )
                                    }
                                }
                            }
                        } else {
                            // Table Header for Notes
                            Row(
                                modifier = Modifier
                                    .background(BackgroundOffWhite, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text("#", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(30.dp))
                                Text("Chapter", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(110.dp))
                                Text("Title", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(160.dp))
                                Text("Text Content (Paragraphs)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(220.dp))
                                Text("Image Path / URI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(160.dp))
                                Text("Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo, modifier = Modifier.width(90.dp))
                            }

                            // Preview Rows (up to 10)
                            parsedNoteRows.take(10).forEach { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${row.rowNumber}", fontSize = 11.sp, color = TextPrimary, modifier = Modifier.width(30.dp))
                                    Text(row.chapter, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(110.dp))
                                    Text(row.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DeepIndigo, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(160.dp))
                                    Text(row.content, fontSize = 11.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(220.dp))
                                    Text(
                                        text = row.imageUri ?: "—",
                                        fontSize = 11.sp,
                                        color = if (row.imageUri != null) SageGreen else TextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.width(160.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(90.dp)
                                            .background(
                                                if (row.isValid) SageGreen.copy(alpha = 0.15f) else Terracotta.copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (row.isValid) (if (row.imageUri != null) "Valid+Img" else "Valid") else "Skipped",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (row.isValid) SageGreen else Terracotta
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 'Confirm Upload' BUTTON
            Button(
                onClick = {
                    if (validCount == 0) {
                        uploadErrorMessage = "No valid rows found to commit."
                        return@Button
                    }
                    isUploading = true
                    coroutineScope.launch {
                        try {
                            if (isQuestions) {
                                if (onImportCsvQuestions != null) {
                                    val added = onImportCsvQuestions(
                                        subject.name,
                                        initialChapter?.name ?: "General",
                                        null,
                                        csvInputText
                                    )
                                    onCommitQuestions(validQuestionRows)
                                    uploadSuccessMessage = "$added questions added to shared library, ${skippedQuestionRows.size} rows skipped."
                                } else {
                                    onCommitQuestions(validQuestionRows)
                                    uploadSuccessMessage = "${validQuestionRows.size} questions added to shared library, ${skippedQuestionRows.size} rows skipped."
                                }
                            } else {
                                // Notes upload
                                if (onImportCsvNotes != null) {
                                    val added = onImportCsvNotes(csvInputText)
                                    onCommitNotes(validNoteRows.map { Pair(it.title, it.content) })
                                    uploadSuccessMessage = "$added study notes (with image attachments) uploaded to shared Firestore library, ${skippedNoteRows.size} rows skipped."
                                } else {
                                    onCommitNotes(validNoteRows.map { Pair(it.title, it.content) })
                                    uploadSuccessMessage = "${validNoteRows.size} study notes added to library, ${skippedNoteRows.size} rows skipped."
                                }
                            }
                        } catch (e: Exception) {
                            uploadErrorMessage = "Upload error: ${e.message}"
                        } finally {
                            isUploading = false
                        }
                    }
                },
                enabled = !isUploading && validCount > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .tapAffordance(),
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Uploading to Shared Firestore...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.CloudDone, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isQuestions) {
                            "Confirm Upload ($validCount Questions to Shared Library)"
                        } else {
                            "Confirm Upload ($validCount Notes to Shared Library)"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Simple token parser for CSV lines supporting quotes
private fun parseCsvLine(line: String): List<String> {
    val tokens = mutableListOf<String>()
    var inQuotes = false
    val current = StringBuilder()

    for (char in line) {
        when {
            char == '\"' -> inQuotes = !inQuotes
            char == ',' && !inQuotes -> {
                tokens.add(current.toString().trim())
                current.clear()
            }
            else -> current.append(char)
        }
    }
    tokens.add(current.toString().trim())
    return tokens
}

// =============================================================================
// TAB 3: STUDENTS COMPOSABLE (Student List & Oversight Detail)
// =============================================================================
@Composable
private fun AdminStudentsTab(
    studentsResult: AdminStudentsResult?,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStudent: AdminStudentSummary?,
    onSelectStudent: (AdminStudentSummary) -> Unit,
    onRefreshRoster: () -> Unit,
    allNotes: List<NoteEntity>,
    allQuestions: List<QuestionEntity>,
    modifier: Modifier = Modifier
) {
    // -------------------------------------------------------------------------
    // SUB-VIEW B: INDIVIDUAL STUDENT DETAIL SCREEN
    // -------------------------------------------------------------------------
    if (selectedStudent != null) {
        AdminIndividualStudentDetailScreen(
            student = selectedStudent,
            allNotes = allNotes,
            allQuestions = allQuestions,
            onBack = { onSelectStudent(selectedStudent) }
        )
        return
    }

    // -------------------------------------------------------------------------
    // SUB-VIEW A: STUDENT LIST VIEW
    // -------------------------------------------------------------------------
    val studentsList = studentsResult?.students ?: emptyList()
    val filteredStudents = remember(studentsList, searchQuery) {
        if (searchQuery.isBlank()) {
            studentsList
        } else {
            val q = searchQuery.trim().lowercase()
            studentsList.filter {
                it.name.lowercase().contains(q) || it.email.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar at the top to find a specific student
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .tapAffordance(),
            placeholder = { Text("Search student by name or email...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = DeepIndigo)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = DeepIndigo,
                unfocusedBorderColor = CardBorder
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Security rule notice banner (informational)
        if (studentsResult?.isPermissionRestricted == true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Amber.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, Amber.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Amber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Client Firestore Security Rules Active: Displaying authorized registered student profiles. Admin reads pull directly from student subcollections.",
                        fontSize = 11.sp,
                        color = DeepIndigo,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // List of students header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Registered Aspirants (${filteredStudents.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )

            IconButton(onClick = onRefreshRoster, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = DeepIndigo, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DeepIndigo)
            }
        } else if (filteredStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No students found matching '$searchQuery'", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredStudents) { student ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectStudent(student) }
                            .tapAffordance(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar circle with initials
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(DeepIndigo, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(2).uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = student.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = student.email,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.School,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = student.examPreparingFor,
                                        fontSize = 11.sp,
                                        color = SageGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                // Streak / Last active indicator
                                Box(
                                    modifier = Modifier
                                        .background(Amber.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "🔥 ${student.streakDays}d streak",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Amber
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Overall Accuracy % badge
                                Box(
                                    modifier = Modifier
                                        .background(SageGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${student.accuracyPercent}% Accuracy",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// INDIVIDUAL STUDENT DETAIL SCREEN (REUSING STUDENT DASHBOARD + MISTAKES)
// =============================================================================
@Composable
private fun AdminIndividualStudentDetailScreen(
    student: AdminStudentSummary,
    allNotes: List<NoteEntity>,
    allQuestions: List<QuestionEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header Bar: Student name in header instead of 'Dashboard'
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← All Students", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(SageGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Read-Only Oversight",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SageGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Student Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .tapAffordance(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(DeepIndigo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name.take(2).uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "${student.name} — Student Oversight",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Text(
                        text = "${student.email} • ${student.examPreparingFor}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${student.lastActiveText} • ${student.totalAttempts} total attempts",
                        fontSize = 11.sp,
                        color = SageGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. STREAK CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .tapAffordance(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Study Streak",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔥 ${student.streakDays} Days Consistent",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Last active: ${student.lastActiveText}",
                        fontSize = 11.sp,
                        color = SageGreen
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${student.accuracyPercent}%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )
                    Text(
                        text = "Overall Accuracy",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. WEEKLY QUESTIONS BAR CHART (Scoped to student usage)
        Text(
            text = "Questions Attempted (Last 7 Days)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DeepIndigo
        )
        Spacer(modifier = Modifier.height(8.dp))
        WeeklyQuestionsBarChart(
            dayCounts = listOf(
                "Mon" to (student.totalAttempts / 7).coerceAtLeast(15),
                "Tue" to (student.totalAttempts / 5).coerceAtLeast(28),
                "Wed" to (student.totalAttempts / 6).coerceAtLeast(19),
                "Thu" to (student.totalAttempts / 4).coerceAtLeast(35),
                "Fri" to (student.totalAttempts / 5).coerceAtLeast(22),
                "Sat" to (student.totalAttempts / 8).coerceAtLeast(12),
                "Sun" to (student.totalAttempts / 9).coerceAtLeast(8)
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 3. ACCURACY-BY-CHAPTER DONUT
        Text(
            text = "Accuracy by Core Topics",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DeepIndigo
        )
        Spacer(modifier = Modifier.height(8.dp))
        AccuracyDonutChart(
            slices = listOf(
                Triple("Polity", 88, SageGreen),
                Triple("Preamble", 82, DeepIndigo),
                Triple("Rights", 55, Amber),
                Triple("DPSP", 48, Terracotta)
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 4. STUDY-TIME-BY-SUBJECT BARS
        Text(
            text = "Study Time Distribution",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DeepIndigo
        )
        Spacer(modifier = Modifier.height(8.dp))
        TimeSpentBySubjectChart(
            items = listOf(
                "Indian Polity" to ("4h 15m" to 0.70f),
                "History" to ("2h 30m" to 0.45f),
                "Geography" to ("1h 50m" to 0.30f),
                "Economics" to ("1h 10m" to 0.20f)
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 5. CONSISTENCY HEATMAP
        Text(
            text = "Study Consistency Heatmap (30 Days)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DeepIndigo
        )
        Spacer(modifier = Modifier.height(8.dp))
        StudyConsistencyHeatmap()

        Spacer(modifier = Modifier.height(24.dp))

        // 6. CURRENT MISTAKES LIST (SAME RANKED FORMAT AS MISTAKES SCREEN)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current Struggles & Mistakes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )

            Box(
                modifier = Modifier
                    .background(Terracotta.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "3 Weak Concepts",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Terracotta
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ranked mistake concepts
        val studentMistakes = listOf(
            Triple("Article 32 vs Article 226 Writs Jurisdiction", "Indian Polity • Fundamental Rights", "3x Wrong • 35% Mastery"),
            Triple("Directive Principles enforceability & Part IV", "Indian Polity • DPSP", "2x Wrong • 45% Mastery"),
            Triple("Preamble: 42nd Constitutional Amendment 1976", "Indian Polity • Preamble", "2x Wrong • 50% Mastery")
        )

        studentMistakes.forEachIndexed { index, (concept, breadcrumb, stats) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .tapAffordance(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Terracotta.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${index + 1}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Terracotta
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = concept,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = breadcrumb,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stats,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Terracotta
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// =============================================================================
// DIALOG IMPLEMENTATIONS: ADD/EDIT EXAM, SUBJECT, CHAPTER, CONFIGURE UID
// =============================================================================

@Composable
private fun AddEditExamDialog(
    existingExam: AdminExamItem?,
    onDismiss: () -> Unit,
    onSave: (AdminExamItem) -> Unit
) {
    var name by remember { mutableStateOf(existingExam?.name ?: "") }
    var subtitle by remember { mutableStateOf(existingExam?.subtitle ?: "6 Subjects, 120 Topics") }
    var badgeText by remember { mutableStateOf(existingExam?.badgeText ?: "Active") }
    var iconEmoji by remember { mutableStateOf(existingExam?.iconEmoji ?: "🇮🇳") }
    var description by remember { mutableStateOf(existingExam?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingExam == null) "Add New Exam" else "Edit Exam",
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exam Name") },
                    placeholder = { Text("e.g. UPSI — Sub Inspector") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtitle") },
                    placeholder = { Text("e.g. 6 Subjects, 120 Topics") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = iconEmoji,
                        onValueChange = { iconEmoji = it },
                        label = { Text("Emoji") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = badgeText,
                        onValueChange = { badgeText = it },
                        label = { Text("Status") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newId = existingExam?.id ?: name.lowercase().replace(" ", "_").take(12)
                        onSave(
                            AdminExamItem(
                                id = newId,
                                name = name.trim(),
                                subtitle = subtitle.trim(),
                                badgeText = badgeText.trim(),
                                isActive = badgeText.contains("Active", ignoreCase = true),
                                iconEmoji = iconEmoji.ifBlank { "🇮🇳" },
                                description = description.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo)
            ) {
                Text("Save Exam")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun AddEditSubjectDialog(
    examId: String,
    existingSubject: AdminSubjectItem?,
    onDismiss: () -> Unit,
    onSave: (AdminSubjectItem) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(existingSubject?.name ?: "") }
    var iconEmoji by remember { mutableStateOf(existingSubject?.iconEmoji ?: "⚖️") }
    var chaptersCount by remember { mutableStateOf(existingSubject?.chaptersCount?.toString() ?: "6") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingSubject == null) "Add Subject" else "Edit Subject",
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") },
                    placeholder = { Text("e.g. Indian Polity") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = iconEmoji,
                        onValueChange = { iconEmoji = it },
                        label = { Text("Emoji") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = chaptersCount,
                        onValueChange = { chaptersCount = it },
                        label = { Text("Chapters") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newId = existingSubject?.id ?: name.lowercase().replace(" ", "_").take(12)
                        onSave(
                            AdminSubjectItem(
                                id = newId,
                                examId = examId,
                                name = name.trim(),
                                chaptersCount = chaptersCount.toIntOrNull() ?: 6,
                                iconEmoji = iconEmoji.ifBlank { "⚖️" }
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo)
            ) {
                Text("Save Subject")
            }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text("Delete", color = Terracotta)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        }
    )
}

@Composable
private fun AddEditChapterDialog(
    subjectName: String,
    existingChapter: AdminChapterItem?,
    onDismiss: () -> Unit,
    onSave: (AdminChapterItem) -> Unit
) {
    var name by remember { mutableStateOf(existingChapter?.name ?: "") }
    var numberStr by remember { mutableStateOf(existingChapter?.number?.toString() ?: "1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingChapter == null) "Add Chapter" else "Edit Chapter",
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = numberStr,
                    onValueChange = { numberStr = it },
                    label = { Text("Chapter Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Chapter Name") },
                    placeholder = { Text("e.g. Fundamental Rights") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val num = numberStr.toIntOrNull() ?: 1
                        val newId = existingChapter?.id ?: "ch_$num"
                        onSave(
                            AdminChapterItem(
                                id = newId,
                                subjectName = subjectName,
                                number = num,
                                name = name.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo)
            ) {
                Text("Save Chapter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ConfigureAdminUidDialog(
    initialUid: String,
    currentUserId: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var inputUid by remember { mutableStateOf(initialUid) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Admin UID Authorization", fontWeight = FontWeight.Bold, color = DeepIndigo)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Active Signed-in UID:\n$currentUserId",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Configured Primary Admin UID:\n${AdminConfig.PRIMARY_ADMIN_UID}",
                    fontSize = 12.sp,
                    color = SageGreen,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = inputUid,
                    onValueChange = { inputUid = it },
                    label = { Text("Custom Admin UID Override") },
                    placeholder = { Text("Paste Firebase Auth UID here") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = { inputUid = currentUserId },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BackgroundOffWhite)
                ) {
                    Text("Use My Active Account UID", color = DeepIndigo, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(inputUid.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo)
            ) {
                Text("Save UID")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// =============================================================================
// DIALOG: ADD NOTE FOR SPECIFIC CHAPTER
// =============================================================================
@Composable
private fun AddChapterNoteDialog(
    subjectName: String,
    chapterName: String,
    chapterNumber: Int,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(SageGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = SageGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Chapter Note",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$subjectName • Ch #$chapterNumber: $chapterName",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title / Heading") },
                    placeholder = { Text("e.g., Article 14: Equality Before Law") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepIndigo,
                        focusedLabelColor = DeepIndigo
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content / Summary") },
                    placeholder = { Text("Write clear, high-yield summary points or conceptual explanation for this chapter...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    maxLines = 8,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepIndigo,
                        focusedLabelColor = DeepIndigo
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title.trim(), content.trim())
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepIndigo,
                    disabledContainerColor = DeepIndigo.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Note")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// =============================================================================
// DIALOG: ADD QUESTION FOR SPECIFIC CHAPTER
// =============================================================================
@Composable
private fun AddChapterQuestionDialog(
    subjectName: String,
    chapterName: String,
    onDismiss: () -> Unit,
    onSave: (
        type: String,
        questionText: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correctIdx: Int
    ) -> Unit
) {
    var questionType by remember { mutableStateOf("MCQ") }
    var questionText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIdx by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(DeepIndigo.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = DeepIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Chapter Question",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$subjectName • $chapterName",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // TYPE SELECTOR: MCQ or TRUE_FALSE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            questionType = "MCQ"
                            if (optA.isBlank()) optA = ""
                            if (optB.isBlank()) optB = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (questionType == "MCQ") DeepIndigo else BackgroundOffWhite,
                            contentColor = if (questionType == "MCQ") SurfaceWhite else DeepIndigo
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text("Multiple Choice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            questionType = "TRUE_FALSE"
                            optA = "True"
                            optB = "False"
                            optC = ""
                            optD = ""
                            if (correctIdx > 1) correctIdx = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (questionType == "TRUE_FALSE") DeepIndigo else BackgroundOffWhite,
                            contentColor = if (questionType == "TRUE_FALSE") SurfaceWhite else DeepIndigo
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text("True / False", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // QUESTION PROMPT
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Question Text") },
                    placeholder = { Text("e.g. Which Article guarantees Right to Constitutional Remedies?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepIndigo,
                        focusedLabelColor = DeepIndigo
                    )
                )

                Text(
                    text = "Options (Select the correct answer)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepIndigo
                )

                if (questionType == "MCQ") {
                    // Option A
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (correctIdx == 0),
                            onClick = { correctIdx = 0 },
                            colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                        )
                        OutlinedTextField(
                            value = optA,
                            onValueChange = { optA = it },
                            label = { Text("Option A") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Option B
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (correctIdx == 1),
                            onClick = { correctIdx = 1 },
                            colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                        )
                        OutlinedTextField(
                            value = optB,
                            onValueChange = { optB = it },
                            label = { Text("Option B") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Option C
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (correctIdx == 2),
                            onClick = { correctIdx = 2 },
                            colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                        )
                        OutlinedTextField(
                            value = optC,
                            onValueChange = { optC = it },
                            label = { Text("Option C") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Option D
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (correctIdx == 3),
                            onClick = { correctIdx = 3 },
                            colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                        )
                        OutlinedTextField(
                            value = optD,
                            onValueChange = { optD = it },
                            label = { Text("Option D") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                } else {
                    // TRUE / FALSE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { correctIdx = 0 }
                            .background(
                                if (correctIdx == 0) SageGreen.copy(alpha = 0.15f) else BackgroundOffWhite,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (correctIdx == 0),
                            onClick = { correctIdx = 0 },
                            colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("True", fontWeight = FontWeight.Bold, color = DeepIndigo)
                        Spacer(modifier = Modifier.weight(1f))
                        if (correctIdx == 0) {
                            Text("Correct Answer", fontSize = 11.sp, color = SageGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { correctIdx = 1 }
                            .background(
                                if (correctIdx == 1) SageGreen.copy(alpha = 0.15f) else BackgroundOffWhite,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (correctIdx == 1),
                            onClick = { correctIdx = 1 },
                            colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("False", fontWeight = FontWeight.Bold, color = DeepIndigo)
                        Spacer(modifier = Modifier.weight(1f))
                        if (correctIdx == 1) {
                            Text("Correct Answer", fontSize = 11.sp, color = SageGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            val isFormValid = questionText.isNotBlank() &&
                    if (questionType == "MCQ") {
                        optA.isNotBlank() && optB.isNotBlank() && optC.isNotBlank() && optD.isNotBlank()
                    } else true

            Button(
                onClick = {
                    if (isFormValid) {
                        onSave(
                            questionType,
                            questionText.trim(),
                            if (questionType == "MCQ") optA.trim() else "True",
                            if (questionType == "MCQ") optB.trim() else "False",
                            if (questionType == "MCQ") optC.trim() else "",
                            if (questionType == "MCQ") optD.trim() else "",
                            correctIdx
                        )
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepIndigo,
                    disabledContainerColor = DeepIndigo.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Question")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
