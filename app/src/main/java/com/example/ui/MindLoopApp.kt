package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.MindLoopBottomNavBar
import com.example.ui.navigation.BottomNavTab
import com.example.ui.navigation.ScreenDestination
import com.example.ui.screens.AddNotesScreen
import com.example.ui.screens.AddQuestionBottomSheet
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MistakesScreen
import com.example.ui.screens.QuestionReviewScreen
import com.example.ui.screens.ReelExamListScreen
import com.example.ui.screens.ReelSubjectsGridScreen
import com.example.ui.screens.ReelSubjectDetailScreen
import com.example.ui.screens.ReelFeedScreen
import com.example.ui.screens.StudyExamListScreen
import com.example.ui.screens.StudyNotesFeedScreen
import com.example.ui.screens.StudySubjectDetailScreen
import com.example.ui.screens.StudySubjectsGridScreen
import com.example.ui.screens.TestExamListScreen
import com.example.ui.screens.TestSubjectDetailScreen
import com.example.ui.screens.TestSubjectsGridScreen
import com.example.ui.viewmodel.MindLoopViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindLoopApp(
    viewModel: MindLoopViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentDestination by viewModel.currentDestination.collectAsState()

    val allNotes by viewModel.allNotes.collectAsState()
    val allQuestions by viewModel.allQuestions.collectAsState()
    val dueQuestions by viewModel.dueQuestions.collectAsState()
    val allReels by viewModel.allReels.collectAsState()
    val mistakeQuestions by viewModel.mistakeQuestions.collectAsState()
    val mostRevisitedNotes by viewModel.mostRevisitedNotes.collectAsState()
    val customSubjects by viewModel.customSubjects.collectAsState()
    val customChapters by viewModel.customChapters.collectAsState()

    // Dynamic dashboard stats from Firestore
    val weeklyDayCounts by viewModel.weeklyDayCounts.collectAsState()
    val timeSpentBySubject by viewModel.timeSpentBySubject.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val todayStudyMinutes by viewModel.todayStudyMinutes.collectAsState()
    val todayQuestionsDone by viewModel.todayQuestionsDone.collectAsState()
    val todayAccuracy by viewModel.todayAccuracy.collectAsState()
    val avgTimePerQuestionSec by viewModel.avgTimePerQuestionSec.collectAsState()
    val totalAttemptsCount by viewModel.totalAttemptsCount.collectAsState()
    val overallAccuracyPercent by viewModel.overallAccuracyPercent.collectAsState()
    val totalStudyTimeThisWeekStr by viewModel.totalStudyTimeThisWeekStr.collectAsState()
    val chapterAccuracies by viewModel.chapterAccuracies.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Add Question Bottom Sheet State
    var showAddQuestionSheet by remember { mutableStateOf(false) }
    var addQuestionLinkedNoteId by remember { mutableStateOf<Long?>(1) }
    var addQuestionSubject by remember { mutableStateOf("Indian Polity") }
    var addQuestionChapter by remember { mutableStateOf("Fundamental Rights") }
    var addQuestionSourceType by remember { mutableStateOf("note") }
    var addQuestionSourceId by remember { mutableStateOf("1") }

    val isAuthScreen = currentDestination is ScreenDestination.Auth || currentUser == null
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity

    val isUserAdmin = remember(currentUser) {
        com.example.data.config.AdminConfig.isAdmin(
            uid = currentUser?.id,
            email = currentUser?.email,
            localOverrideUid = com.example.data.config.AdminConfig.getLocalAdminUid(context),
            role = currentUser?.role
        )
    }

    // Intercept back button to pop navigation stack
    BackHandler(enabled = currentDestination !is ScreenDestination.Home && !isAuthScreen && currentUser != null) {
        viewModel.navigateBack()
    }

    // Navigation bar remains identical and persistent across every main study screen in the app.
    // Hidden on Admin Panel since it has its own dedicated top-level tabs.
    val showBottomBar = !isAuthScreen && currentUser != null && currentDestination !is ScreenDestination.AdminDashboard
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                MindLoopBottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { tab ->
                        viewModel.selectTab(tab)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (showBottomBar) innerPadding else androidx.compose.foundation.layout.PaddingValues(0.dp))
        ) {
            Crossfade(
                targetState = if (currentUser == null) ScreenDestination.Auth(isSignUp = false) else currentDestination,
                label = "screen_transition"
            ) { destination ->
                when (destination) {
                    is ScreenDestination.Auth -> {
                        com.example.ui.screens.AuthScreen(
                            initialIsSignUp = destination.isSignUp,
                            authManager = viewModel.authManager,
                            activity = activity,
                            currentUser = currentUser,
                            onAuthSuccess = { user ->
                                viewModel.setCurrentUser(user)
                            },
                            onSignOut = {
                                viewModel.logout()
                            },
                            onBack = if (currentUser != null) {
                                { viewModel.navigateBack() }
                            } else null,
                            onSyncToCloud = {
                                viewModel.syncToFirestore { success, count ->
                                    val msg = if (success) "Synced $count items to Cloud Firestore!" else "Cloud sync updated"
                                    android.widget.Toast.makeText(viewModel.repository.context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            onOpenAdmin = if (isUserAdmin) {
                                { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                            } else null
                        )
                    }

                    is ScreenDestination.Home -> {
                        HomeScreen(
                            dueNotesCount = allNotes.size.coerceAtLeast(14),
                            dueQuestionsCount = dueQuestions.size.coerceAtLeast(28),
                            mistakesCount = mistakeQuestions.size.coerceAtLeast(5),
                            todayStudyMinutes = todayStudyMinutes,
                            todayQuestionsDone = todayQuestionsDone,
                            todayAccuracy = todayAccuracy,
                            streakDays = streakDays,
                            weeklyDayCounts = weeklyDayCounts,
                            timeSpentBySubject = timeSpentBySubject,
                            avgTimePerQuestionSec = avgTimePerQuestionSec,
                            totalAttemptsCount = totalAttemptsCount,
                            overallAccuracyPercent = overallAccuracyPercent,
                            totalStudyTimeThisWeekStr = totalStudyTimeThisWeekStr,
                            chapterAccuracies = chapterAccuracies,
                            mostRevisitedNotes = mostRevisitedNotes,
                            userName = currentUser?.name ?: "Student Aspirant",
                            userEmail = currentUser?.email ?: "aspirant@mindloop.org",
                            onStudyNotesClick = {
                                viewModel.selectTab(BottomNavTab.STUDY)
                            },
                            onReelsClick = {
                                viewModel.navigateTo(ScreenDestination.ReelExams)
                            },
                            onReviewQuestionsClick = {
                                viewModel.selectTab(BottomNavTab.TEST)
                            },
                            onMistakesClick = {
                                viewModel.selectTab(BottomNavTab.MISTAKES)
                            },
                            reelsCount = allReels.size,
                            onNoteClick = { noteId ->
                                val note = allNotes.find { it.id == noteId }
                                viewModel.navigateTo(
                                    ScreenDestination.StudyNotesFeed(
                                        subjectName = note?.subjectName ?: "Indian Polity",
                                        chapterName = note?.chapterName ?: "Fundamental Rights",
                                        initialNoteId = noteId
                                    )
                                )
                            },
                            onProfileClick = {
                                viewModel.navigateTo(ScreenDestination.Auth(isSignUp = false))
                            },
                            onSyncClick = {
                                viewModel.syncToFirestore { success, count ->
                                    val message = if (success) {
                                        "Synced $count items to Cloud Firestore!"
                                    } else {
                                        "Sync completed (offline/cloud connected)"
                                    }
                                    android.widget.Toast.makeText(viewModel.repository.context, message, android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            onAdminClick = if (isUserAdmin) {
                                { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                            } else null,
                            isAdmin = isUserAdmin
                        )
                    }

                    is ScreenDestination.Mistakes -> {
                        MistakesScreen(
                            mistakeQuestions = mistakeQuestions,
                            allQuestions = allQuestions,
                            allNotes = allNotes,
                            customSubjects = customSubjects,
                            customChapters = customChapters,
                            onStudyNotes = { subject, chapter, noteId ->
                                viewModel.navigateTo(
                                    ScreenDestination.StudyNotesFeed(
                                        subjectName = subject,
                                        chapterName = chapter,
                                        initialNoteId = noteId
                                    )
                                )
                            },
                            onRetest = { subject, chapter, questionId ->
                                viewModel.navigateTo(
                                    ScreenDestination.QuestionReview(
                                        subjectName = subject,
                                        chapterName = chapter,
                                        specificQuestionId = questionId,
                                        onlyMistakes = true
                                    )
                                )
                            },
                            onRetestSubject = { subject ->
                                viewModel.navigateTo(
                                    ScreenDestination.QuestionReview(
                                        subjectName = subject,
                                        chapterName = "All Chapters",
                                        onlyMistakes = true
                                    )
                                )
                            },
                            onRetestChapter = { subject, chapter ->
                                viewModel.navigateTo(
                                    ScreenDestination.QuestionReview(
                                        subjectName = subject,
                                        chapterName = chapter,
                                        onlyMistakes = true
                                    )
                                )
                            },
                            onWatchReel = { reelId ->
                                val reel = allReels.find { it.id == reelId }
                                viewModel.navigateTo(
                                    ScreenDestination.ReelFeed(
                                        examName = reel?.exam ?: "UPSI",
                                        subjectName = reel?.subject ?: "Reels Concepts",
                                        chapterName = reel?.chapter,
                                        initialReelId = reelId
                                    )
                                )
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    // Study flow
                    is ScreenDestination.StudyExams -> {
                        StudyExamListScreen(
                            onSelectExam = { exam ->
                                viewModel.navigateTo(ScreenDestination.StudySubjectsGrid(exam))
                            },
                            onSelectSubject = { subject ->
                                viewModel.navigateTo(
                                    ScreenDestination.StudySubjectDetail(
                                        examName = "Self-Study & Independent Subjects",
                                        subjectName = subject
                                    )
                                )
                            },
                            onAddNotesClick = {
                                viewModel.navigateTo(ScreenDestination.AddNotes())
                            },
                            onAddNewSubject = { subject, initialChap ->
                                viewModel.addCustomSubject(subject, initialChap)
                            },
                            customSubjects = customSubjects,
                            allNotes = allNotes,
                            allQuestions = allQuestions
                        )
                    }

                    is ScreenDestination.StudySubjectsGrid -> {
                        StudySubjectsGridScreen(
                            examName = destination.examName,
                            customSubjects = customSubjects,
                            onSelectSubject = { subject ->
                                viewModel.navigateTo(
                                    ScreenDestination.StudySubjectDetail(
                                        examName = destination.examName,
                                        subjectName = subject
                                    )
                                )
                            },
                            onAddSubject = {
                                viewModel.navigateTo(ScreenDestination.AddNotes(defaultSubject = "Psychology"))
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    is ScreenDestination.StudySubjectDetail -> {
                        StudySubjectDetailScreen(
                            subjectName = destination.subjectName,
                            allNotes = allNotes,
                            allQuestions = allQuestions,
                            customChapters = customChapters[destination.subjectName] ?: emptyList(),
                            subjectStats = viewModel.getSubjectDetailedStats(destination.subjectName),
                            onStudyFullSubject = {
                                viewModel.navigateTo(
                                    ScreenDestination.StudyNotesFeed(
                                        subjectName = destination.subjectName,
                                        chapterName = ""
                                    )
                                )
                            },
                            onSelectChapter = { chapter ->
                                viewModel.navigateTo(
                                    ScreenDestination.StudyNotesFeed(
                                        subjectName = destination.subjectName,
                                        chapterName = chapter
                                    )
                                )
                            },
                            onAddChapter = { chapterName ->
                                viewModel.addCustomChapter(destination.subjectName, chapterName)
                            },
                            onAddNote = {
                                viewModel.navigateTo(
                                    ScreenDestination.AddNotes(
                                        defaultSubject = destination.subjectName
                                    )
                                )
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    is ScreenDestination.StudyNotesFeed -> {
                        StudyNotesFeedScreen(
                            chapterName = destination.chapterName,
                            subjectName = destination.subjectName,
                            allNotes = allNotes,
                            initialNoteId = destination.initialNoteId,
                            onStartSession = { sessionId, noteId, subject, chapter, startedAt, mode ->
                                viewModel.startStudySession(
                                    sessionId = sessionId,
                                    noteId = noteId,
                                    subject = subject,
                                    chapter = chapter,
                                    startedAt = startedAt,
                                    mode = mode
                                )
                            },
                            onEndSession = { sessionId, endedAt, durationSeconds, noteId ->
                                viewModel.endStudySession(
                                    sessionId = sessionId,
                                    endedAt = endedAt,
                                    durationSeconds = durationSeconds,
                                    noteId = noteId
                                )
                            },
                            onAddQuestionClick = { noteId, chapterName ->
                                addQuestionLinkedNoteId = noteId
                                addQuestionSubject = destination.subjectName
                                addQuestionChapter = chapterName
                                addQuestionSourceType = "note"
                                addQuestionSourceId = noteId.toString()
                                showAddQuestionSheet = true
                            },
                            onRecordTime = { noteId, secs ->
                                viewModel.recordStudyTime(noteId, secs)
                            },
                            onSaveNote = { subj, chap, title, content, uri ->
                                viewModel.addNote(
                                    subjectName = subj,
                                    chapterName = chap,
                                    chapterNumber = 1,
                                    title = title,
                                    summaryText = content,
                                    imageUri = uri
                                )
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    is ScreenDestination.AddNotes -> {
                        AddNotesScreen(
                            defaultSubject = destination.defaultSubject,
                            defaultChapter = destination.defaultChapter,
                            onSaveNote = { subj, chap, title, content, uri ->
                                viewModel.addNote(
                                    subjectName = subj,
                                    chapterName = chap,
                                    chapterNumber = 3,
                                    title = title,
                                    summaryText = content,
                                    imageUri = uri
                                )
                            },
                            onImportCsv = { subj, chap, csv ->
                                viewModel.importCsvQuestions(subj, chap, null, csv)
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    // Test flow
                    is ScreenDestination.TestExams -> {
                        TestExamListScreen(
                            customSubjects = customSubjects,
                            onSelectExam = { exam ->
                                viewModel.navigateTo(ScreenDestination.TestSubjectsGrid(exam))
                            },
                            onSelectCustomSubject = { subject ->
                                viewModel.navigateTo(
                                    ScreenDestination.TestSubjectDetail(
                                        examName = "Self-Study & Independent Subjects",
                                        subjectName = subject
                                    )
                                )
                            },
                            onAddNewSubject = { subjectName ->
                                viewModel.addCustomSubject(subjectName)
                            },
                            onViewSubjectStats = { subjectName ->
                                viewModel.navigateTo(
                                    ScreenDestination.TestSubjectDetail(
                                        examName = "Self-Study & Independent Subjects",
                                        subjectName = subjectName
                                    )
                                )
                            }
                        )
                    }

                    is ScreenDestination.TestSubjectsGrid -> {
                        TestSubjectsGridScreen(
                            examName = destination.examName,
                            customSubjects = customSubjects,
                            onSelectSubject = { subject ->
                                viewModel.navigateTo(
                                    ScreenDestination.TestSubjectDetail(
                                        examName = destination.examName,
                                        subjectName = subject
                                    )
                                )
                            },
                            onAddSubject = {
                                viewModel.addCustomSubject("Psychology")
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    is ScreenDestination.TestSubjectDetail -> {
                        TestSubjectDetailScreen(
                            subjectName = destination.subjectName,
                            allQuestions = allQuestions,
                            customChapters = customChapters[destination.subjectName] ?: emptyList(),
                            subjectStats = viewModel.getSubjectDetailedStats(destination.subjectName),
                            onTestFullSubject = {
                                viewModel.navigateTo(
                                    ScreenDestination.QuestionReview(
                                        subjectName = destination.subjectName,
                                        chapterName = "All Chapters"
                                    )
                                )
                            },
                            onSelectChapter = { chapter ->
                                viewModel.navigateTo(
                                    ScreenDestination.QuestionReview(
                                        subjectName = destination.subjectName,
                                        chapterName = chapter
                                    )
                                )
                            },
                            onAddChapter = { chapterName ->
                                viewModel.addCustomChapter(destination.subjectName, chapterName)
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    is ScreenDestination.QuestionReview -> {
                        val poolQuestions = if (destination.onlyMistakes) {
                            val mistakes = mistakeQuestions.ifEmpty { allQuestions.filter { it.timesWrong > 0 } }
                            mistakes.ifEmpty { allQuestions }
                        } else {
                            allQuestions
                        }

                        val isReelDestination = destination.subjectName.contains("Reel", ignoreCase = true)
                        val reviewQuestions = if (destination.specificQuestionId != null) {
                            allQuestions.filter { it.id == destination.specificQuestionId }
                        } else if (destination.chapterName != null && destination.chapterName != "All Chapters") {
                            val cleanChapter = destination.chapterName.substringAfter(".").trim().ifEmpty { destination.chapterName }
                            val filteredPool = poolQuestions.filter {
                                it.chapterName.equals(destination.chapterName, ignoreCase = true) ||
                                it.chapterName.contains(cleanChapter, ignoreCase = true)
                            }
                            if (filteredPool.isNotEmpty()) filteredPool else {
                                allQuestions.filter {
                                    it.chapterName.equals(destination.chapterName, ignoreCase = true) ||
                                    it.chapterName.contains(cleanChapter, ignoreCase = true)
                                }
                            }
                        } else if (isReelDestination) {
                            val filteredPool = poolQuestions.filter { it.sourceType.equals("reel", ignoreCase = true) || it.subjectName.contains("Reel", ignoreCase = true) }
                            if (filteredPool.isNotEmpty()) filteredPool else {
                                allQuestions.filter { it.sourceType.equals("reel", ignoreCase = true) || it.subjectName.contains("Reel", ignoreCase = true) }
                            }
                        } else {
                            poolQuestions.filter { it.subjectName.equals(destination.subjectName, ignoreCase = true) }
                                .ifEmpty { allQuestions.filter { it.subjectName.equals(destination.subjectName, ignoreCase = true) } }
                        }.ifEmpty {
                            if (isReelDestination) {
                                allQuestions.filter { it.sourceType.equals("reel", ignoreCase = true) }
                            } else allQuestions
                        }

                        // Prioritize questions using Spaced Repetition (SRS) to review difficult questions more frequently
                        val prioritizedQuestions = remember(reviewQuestions) {
                            viewModel.prioritizeQuestionsBySrs(reviewQuestions)
                        }

                        QuestionReviewScreen(
                            chapterName = destination.chapterName ?: "Review",
                            subjectName = destination.subjectName,
                            questions = prioritizedQuestions,
                            onViewSourceNote = { noteId ->
                                // Immediately jump back to the linked note in Study feed!
                                viewModel.navigateTo(
                                    ScreenDestination.StudyNotesFeed(
                                        subjectName = destination.subjectName,
                                        chapterName = destination.chapterName ?: "Fundamental Rights",
                                        initialNoteId = noteId
                                    )
                                )
                            },
                            onViewSourceReel = { reelId ->
                                val reel = allReels.find { it.id == reelId }
                                viewModel.navigateTo(
                                    ScreenDestination.ReelFeed(
                                        examName = reel?.exam ?: "UPSI",
                                        subjectName = reel?.subject ?: destination.subjectName,
                                        chapterName = reel?.chapter,
                                        initialReelId = reelId
                                    )
                                )
                            },
                            onRecordAttempt = { qId, isCorrect, rating, timeSec ->
                                viewModel.recordQuestionAttempt(qId, isCorrect, rating, timeSec)
                            },
                            onLogQuestionAttempt = { attemptId, questionId, noteId, subject, chapter, questionType, shownAt, answeredAt, timeTakenSec, selectedAns, isCorr, rating ->
                                viewModel.logQuestionAttempt(
                                    attemptId = attemptId,
                                    questionId = questionId,
                                    noteId = noteId,
                                    subject = subject,
                                    chapter = chapter,
                                    questionType = questionType,
                                    shownAt = shownAt,
                                    answeredAt = answeredAt,
                                    timeTakenSeconds = timeTakenSec,
                                    selectedAnswer = selectedAns,
                                    isCorrect = isCorr,
                                    selfRating = rating
                                )
                            },
                            onUpdateAttemptRating = { attemptId, questionId, rating ->
                                viewModel.updateAttemptRating(attemptId, questionId, rating)
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    is ScreenDestination.AdminDashboard -> {
                        AdminDashboardScreen(
                            currentUserId = currentUser?.id ?: "local_admin_guest",
                            currentUserEmail = currentUser?.email ?: "admin@mindloop.org",
                            allQuestions = allQuestions,
                            allNotes = allNotes,
                            onBack = { viewModel.navigateBack() },
                            onAddQuestion = { subj, chap, type, text, optA, optB, optC, optD, correctIdx, linkedNoteId ->
                                viewModel.addQuestion(
                                    linkedNoteId = linkedNoteId,
                                    subjectName = subj,
                                    chapterName = chap,
                                    questionType = type,
                                    questionText = text,
                                    optionA = optA,
                                    optionB = optB,
                                    optionC = optC,
                                    optionD = optD,
                                    correctIndex = correctIdx
                                )
                            },
                            onAddNote = { subj, chap, chapNum, title, content ->
                                viewModel.addNote(
                                    subjectName = subj,
                                    chapterName = chap,
                                    chapterNumber = chapNum,
                                    title = title,
                                    summaryText = content,
                                    imageUri = null
                                )
                            },
                            onDeleteNote = { noteId ->
                                viewModel.deleteNote(noteId)
                            },
                            onDeleteQuestion = { questionId ->
                                viewModel.deleteQuestion(questionId)
                            },
                            onFetchStudents = {
                                viewModel.fetchAllStudents()
                            },
                            onFetchStudentAttempts = { studentUid ->
                                viewModel.fetchStudentAttempts(studentUid)
                            },
                            onFetchStudentNotes = { studentUid ->
                                viewModel.fetchStudentNotes(studentUid)
                            },
                            onDeleteStudentNote = { studentUid, noteId ->
                                viewModel.deleteStudentNote(studentUid, noteId)
                            },
                            onImportCsvQuestions = { subject, chapter, linkedNoteId, csv ->
                                viewModel.importCsvQuestions(subject, chapter, linkedNoteId, csv)
                            },
                            onImportCsvNotes = { csv ->
                                viewModel.importCsvNotes(csv)
                            }
                        )
                    }

                    is ScreenDestination.ReelExams -> {
                        ReelExamListScreen(
                            onSelectExam = { exam ->
                                viewModel.navigateTo(ScreenDestination.ReelSubjectsGrid(examName = exam))
                            },
                            onSelectSubject = { subject ->
                                viewModel.navigateTo(ScreenDestination.ReelSubjectDetail(examName = "UPSI", subjectName = subject))
                            },
                            onUploadReelClick = {
                                viewModel.navigateTo(ScreenDestination.ReelFeed(initialReelId = null))
                            },
                            allReels = allReels,
                            customSubjects = customSubjects
                        )
                    }

                    is ScreenDestination.ReelSubjectsGrid -> {
                        ReelSubjectsGridScreen(
                            examName = destination.examName,
                            onBackClick = { viewModel.navigateBack() },
                            onSelectSubject = { subject ->
                                viewModel.navigateTo(ScreenDestination.ReelSubjectDetail(examName = destination.examName, subjectName = subject))
                            },
                            allReels = allReels
                        )
                    }

                    is ScreenDestination.ReelSubjectDetail -> {
                        ReelSubjectDetailScreen(
                            examName = destination.examName,
                            subjectName = destination.subjectName,
                            onBackClick = { viewModel.navigateBack() },
                            onWatchFullSubject = {
                                viewModel.navigateTo(
                                    ScreenDestination.ReelFeed(
                                        examName = destination.examName,
                                        subjectName = destination.subjectName,
                                        chapterName = null
                                    )
                                )
                            },
                            onWatchChapter = { chapter ->
                                viewModel.navigateTo(
                                    ScreenDestination.ReelFeed(
                                        examName = destination.examName,
                                        subjectName = destination.subjectName,
                                        chapterName = chapter
                                    )
                                )
                            },
                            onSelectReel = { reelId ->
                                viewModel.navigateTo(
                                    ScreenDestination.ReelFeed(
                                        examName = destination.examName,
                                        subjectName = destination.subjectName,
                                        chapterName = null,
                                        initialReelId = reelId
                                    )
                                )
                            },
                            onUploadReelClick = {
                                viewModel.navigateTo(
                                    ScreenDestination.ReelFeed(
                                        examName = destination.examName,
                                        subjectName = destination.subjectName
                                    )
                                )
                            },
                            allReels = allReels
                        )
                    }

                    is ScreenDestination.ReelFeed -> {
                        val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
                        ReelFeedScreen(
                            initialReelId = destination.initialReelId,
                            filterExam = destination.examName,
                            filterSubject = destination.subjectName,
                            filterChapter = destination.chapterName,
                            onBackClick = { viewModel.navigateBack() },
                            onAddQuestion = { reelId, subject, chapter ->
                                addQuestionLinkedNoteId = null
                                addQuestionSubject = subject
                                addQuestionChapter = chapter
                                addQuestionSourceType = "reel"
                                addQuestionSourceId = reelId.toString()
                                showAddQuestionSheet = true
                            },
                            onRecordWatch = { reelId, watchSecs ->
                                viewModel.recordReelWatch(reelId, watchSecs)
                            },
                            onUploadReel = { title, desc, subj, chap, uri ->
                                coroutineScope.launch {
                                    val newId = System.currentTimeMillis()
                                    val savedPath = com.example.util.ReelVideoCacheManager.saveUploadedVideo(context, newId, uri)
                                    val newReel = com.example.data.entity.ReelEntity(
                                        id = newId,
                                        title = title,
                                        description = desc,
                                        subject = subj,
                                        chapter = chap,
                                        exam = destination.examName ?: "UPSI",
                                        videoUrl = savedPath,
                                        durationSeconds = 30
                                    )
                                    viewModel.insertReel(newReel)
                                }
                            },
                            allReels = allReels
                        )
                    }
                }
            }

            // Modal Bottom Sheet: Add Question
            if (showAddQuestionSheet) {
                AddQuestionBottomSheet(
                    linkedNoteId = addQuestionLinkedNoteId,
                    subjectName = addQuestionSubject,
                    chapterName = addQuestionChapter,
                    sourceType = addQuestionSourceType,
                    sourceId = addQuestionSourceId,
                    onDismiss = { showAddQuestionSheet = false },
                    onSaveQuestion = { noteId, subj, chap, type, text, optA, optB, optC, optD, correctIdx ->
                        viewModel.addQuestion(
                            linkedNoteId = noteId,
                            subjectName = subj,
                            chapterName = chap,
                            questionType = type,
                            questionText = text,
                            optionA = optA,
                            optionB = optB,
                            optionC = optC,
                            optionD = optD,
                            correctIndex = correctIdx,
                            sourceType = addQuestionSourceType,
                            sourceId = addQuestionSourceId
                        )
                    },
                    onSaveQuestionWithSource = { noteId, subj, chap, type, text, optA, optB, optC, optD, correctIdx, srcType, srcId ->
                        viewModel.addQuestion(
                            linkedNoteId = noteId,
                            subjectName = subj,
                            chapterName = chap,
                            questionType = type,
                            questionText = text,
                            optionA = optA,
                            optionB = optB,
                            optionC = optC,
                            optionD = optD,
                            correctIndex = correctIdx,
                            sourceType = srcType,
                            sourceId = srcId
                        )
                    }
                )
            }
        }
    }
}
