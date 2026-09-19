package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionAttemptEntity
import com.example.data.entity.QuestionEntity
import com.example.data.entity.ReelEntity
import com.example.data.entity.StudySessionEntity
import com.example.data.model.CurriculumChapter
import com.example.data.model.CurriculumExam
import com.example.data.model.CurriculumSubject
import com.example.data.model.BackupNoteItem
import com.example.data.model.BackupQuestionItem
import com.example.data.model.BackupReelItem
import com.example.data.model.BackupUserAttemptItem
import com.example.data.model.BackupStudySessionItem
import com.example.data.model.MindLoopCourseBackup
import com.example.data.model.DataBackupManager
import com.example.data.model.SpacedRepetitionScheduler
import com.example.data.model.SrsNextReviewProjection
import com.example.data.firestore.DashboardChapterAccuracy
import com.example.data.firestore.DashboardMistakeRank
import com.example.data.firestore.FirestoreRepository
import com.example.ui.navigation.BottomNavTab
import com.example.ui.navigation.ScreenDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SubjectDetailedStats(
    val subjectName: String,
    val totalStudyTimeMinutes: Long,
    val notesCount: Int,
    val questionsCount: Int,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val accuracyPercent: Int,
    val mistakesCount: Int,
    val chaptersCount: Int
)

/**
 * Spaced Repetition System (SRS) Urgency Tiers
 */
enum class SrsUrgency {
    CRITICAL_MISTAKE, // High mistake count or multiple wrong attempts; needs immediate re-drilling
    HIGH_DIFFICULTY,  // Recent wrong answer or rated "HARD" by the user
    DUE_FOR_REVIEW,   // Target review interval elapsed according to the forgetting curve
    NEW_CONCEPT,      // Fresh, unattempted question awaiting initial baseline exposure
    MASTERED          // Consistently correct, rated "EASY", deferred to prevent unnecessary fatigue
}

/**
 * Detailed SRS metadata payload for an individual question.
 */
data class SrsQuestionPriority(
    val question: QuestionEntity,
    val priorityScore: Float,
    val urgency: SrsUrgency,
    val urgencyLabel: String,
    val recommendedIntervalHours: Float,
    val retentionEstimatePercent: Int,
    val priorityReason: String
)

data class StarterPackClearResult(
    val notesRemoved: Int,
    val questionsRemoved: Int,
    val reelsRemoved: Int,
    val isSuccess: Boolean,
    val message: String
)

class MindLoopViewModel(
    val repository: FirestoreRepository
) : ViewModel() {

    // Dedicated ViewModel for aggregating study statistics from Firestore
    val studyStatsViewModel: StudyStatisticsViewModel by lazy {
        StudyStatisticsViewModel(repository)
    }

    // Authentication Manager
    val authManager: com.example.data.auth.AuthManager by lazy {
        com.example.data.auth.AuthManager(repository.context)
    }

    private val _currentTab = MutableStateFlow(BottomNavTab.HOME)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    // Check if user has an existing active session in Firebase
    private val initialAuthUser: com.example.ui.screens.AuthUser? = authManager.currentFirebaseUser?.let {
        authManager.mapToAuthUser(it)
    }

    // Authenticated User State: null means user is not signed in and must authenticate first
    private val _currentUser = MutableStateFlow<com.example.ui.screens.AuthUser?>(initialAuthUser)
    val currentUser: StateFlow<com.example.ui.screens.AuthUser?> = _currentUser.asStateFlow()

    private val _navigationStack = MutableStateFlow<List<ScreenDestination>>(
        if (initialAuthUser != null) listOf(ScreenDestination.Home)
        else listOf(ScreenDestination.Auth(isSignUp = false))
    )
    val currentDestination: StateFlow<ScreenDestination> = MutableStateFlow<ScreenDestination>(
        if (initialAuthUser != null) ScreenDestination.Home
        else ScreenDestination.Auth(isSignUp = false)
    ).apply {
        viewModelScope.launch {
            _navigationStack.collect { stack ->
                value = stack.lastOrNull() ?: if (_currentUser.value != null) ScreenDestination.Home else ScreenDestination.Auth(isSignUp = false)
            }
        }
    }

    // Firestore real-time reactive streams (Declared before init block)
    val allNotes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueQuestions: StateFlow<List<QuestionEntity>> = repository.dueQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReels: StateFlow<List<ReelEntity>> = repository.allReels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -------------------------------------------------------------------------
    // UNIFIED SHARED CURRICULUM (Exams, Subjects, Chapters)
    // -------------------------------------------------------------------------
    private val curriculumPrefs by lazy {
        repository.context.getSharedPreferences("mindloop_curriculum_prefs", Context.MODE_PRIVATE)
    }

    private val defaultExams = listOf(
        CurriculumExam(
            id = "exam_upsi",
            name = "UPSI – Police Sub-Inspector",
            subtitle = "Polity, Law, General Hindi, Mental Aptitude",
            createdBy = "admin",
            isEnrolled = true
        )
    )

    private val defaultSubjects = listOf(
        CurriculumSubject(
            id = "subj_upsi_polity",
            examName = "UPSI – Police Sub-Inspector",
            name = "Indian Polity",
            subtitle = "Constitution, Articles & Governance",
            createdBy = "admin",
            isStandalone = false
        ),
        CurriculumSubject(
            id = "subj_upsi_law",
            examName = "UPSI – Police Sub-Inspector",
            name = "Basic Law / Mool Vidhi",
            subtitle = "IPC, CrPC & Motor Vehicle Act",
            createdBy = "admin",
            isStandalone = false
        ),
        CurriculumSubject(
            id = "subj_upsi_hindi",
            examName = "UPSI – Police Sub-Inspector",
            name = "General Hindi",
            subtitle = "Vyakaran, Sahitya & Shabdavali",
            createdBy = "admin",
            isStandalone = false
        ),
        CurriculumSubject(
            id = "subj_upsi_quant",
            examName = "UPSI – Police Sub-Inspector",
            name = "Numerical & Mental Ability",
            subtitle = "Maths, Data Interpretation",
            createdBy = "admin",
            isStandalone = false
        ),
        CurriculumSubject(
            id = "subj_upsi_reasoning",
            examName = "UPSI – Police Sub-Inspector",
            name = "Mental Aptitude / Reasoning",
            subtitle = "Logical Reasoning & IQ",
            createdBy = "admin",
            isStandalone = false
        ),
        CurriculumSubject(
            id = "subj_psychology",
            examName = null,
            name = "Psychology",
            subtitle = "Cognitive Psychology, Memory & Attention",
            createdBy = "admin",
            isStandalone = true
        )
    )

    private val defaultChapters = listOf(
        CurriculumChapter(id = "chap_polity_1", examName = "UPSI – Police Sub-Inspector", subjectName = "Indian Polity", name = "1. Making of the Constitution", createdBy = "admin"),
        CurriculumChapter(id = "chap_polity_2", examName = "UPSI – Police Sub-Inspector", subjectName = "Indian Polity", name = "2. Preamble", createdBy = "admin"),
        CurriculumChapter(id = "chap_polity_3", examName = "UPSI – Police Sub-Inspector", subjectName = "Indian Polity", name = "3. Fundamental Rights", createdBy = "admin"),
        CurriculumChapter(id = "chap_polity_4", examName = "UPSI – Police Sub-Inspector", subjectName = "Indian Polity", name = "4. Directive Principles of State Policy", createdBy = "admin"),
        CurriculumChapter(id = "chap_polity_5", examName = "UPSI – Police Sub-Inspector", subjectName = "Indian Polity", name = "5. Fundamental Duties", createdBy = "admin"),
        CurriculumChapter(id = "chap_polity_6", examName = "UPSI – Police Sub-Inspector", subjectName = "Indian Polity", name = "6. Union Executive (President & VP)", createdBy = "admin"),
        CurriculumChapter(id = "chap_psych_1", examName = null, subjectName = "Psychology", name = "1. Deception Detection & Behavioral Analysis", createdBy = "admin"),
        CurriculumChapter(id = "chap_psych_2", examName = null, subjectName = "Psychology", name = "2. Influence & Persuasion (Foundation & 6MX Profiling)", createdBy = "admin")
    )

    private fun loadExamsFromPrefs(): List<CurriculumExam> {
        val raw = curriculumPrefs.getString("curriculum_exams_json", null)
        if (raw.isNullOrBlank()) return defaultExams
        return try {
            val array = org.json.JSONArray(raw)
            val list = mutableListOf<CurriculumExam>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CurriculumExam(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        subtitle = obj.optString("subtitle", ""),
                        createdBy = obj.optString("createdBy", "admin"),
                        isEnrolled = obj.optBoolean("isEnrolled", true)
                    )
                )
            }
            if (!list.any { it.name.contains("UPSI", ignoreCase = true) }) {
                defaultExams + list
            } else list
        } catch (e: Exception) {
            defaultExams
        }
    }

    private fun saveExamsToPrefs(exams: List<CurriculumExam>) {
        val array = org.json.JSONArray()
        for (item in exams) {
            val obj = org.json.JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("subtitle", item.subtitle)
                put("createdBy", item.createdBy)
                put("isEnrolled", item.isEnrolled)
            }
            array.put(obj)
        }
        curriculumPrefs.edit().putString("curriculum_exams_json", array.toString()).apply()
    }

    private fun loadSubjectsFromPrefs(): List<CurriculumSubject> {
        val raw = curriculumPrefs.getString("curriculum_subjects_json", null)
        if (raw.isNullOrBlank()) return defaultSubjects
        return try {
            val array = org.json.JSONArray(raw)
            val list = mutableListOf<CurriculumSubject>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val exam = obj.optString("examName", "").ifBlank { null }
                list.add(
                    CurriculumSubject(
                        id = obj.getString("id"),
                        examName = exam,
                        name = obj.getString("name"),
                        subtitle = obj.optString("subtitle", ""),
                        createdBy = obj.optString("createdBy", "admin"),
                        isStandalone = obj.optBoolean("isStandalone", exam == null)
                    )
                )
            }
            // Ensure default subjects are present
            val result = list.toMutableList()
            for (def in defaultSubjects) {
                if (!result.any { it.name.equals(def.name, ignoreCase = true) }) {
                    result.add(def)
                }
            }
            result
        } catch (e: Exception) {
            defaultSubjects
        }
    }

    private fun saveSubjectsToPrefs(subjects: List<CurriculumSubject>) {
        val array = org.json.JSONArray()
        for (item in subjects) {
            val obj = org.json.JSONObject().apply {
                put("id", item.id)
                put("examName", item.examName ?: "")
                put("name", item.name)
                put("subtitle", item.subtitle)
                put("createdBy", item.createdBy)
                put("isStandalone", item.isStandalone)
            }
            array.put(obj)
        }
        curriculumPrefs.edit().putString("curriculum_subjects_json", array.toString()).apply()
    }

    private fun loadChaptersFromPrefs(): List<CurriculumChapter> {
        val raw = curriculumPrefs.getString("curriculum_chapters_json", null)
        if (raw.isNullOrBlank()) return defaultChapters
        return try {
            val array = org.json.JSONArray(raw)
            val list = mutableListOf<CurriculumChapter>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val exam = obj.optString("examName", "").ifBlank { null }
                list.add(
                    CurriculumChapter(
                        id = obj.getString("id"),
                        examName = exam,
                        subjectName = obj.getString("subjectName"),
                        name = obj.getString("name"),
                        createdBy = obj.optString("createdBy", "admin")
                    )
                )
            }
            val result = list.toMutableList()
            for (def in defaultChapters) {
                if (!result.any { it.name.equals(def.name, ignoreCase = true) && it.subjectName.equals(def.subjectName, ignoreCase = true) }) {
                    result.add(def)
                }
            }
            result
        } catch (e: Exception) {
            defaultChapters
        }
    }

    private fun saveChaptersToPrefs(chapters: List<CurriculumChapter>) {
        val array = org.json.JSONArray()
        for (item in chapters) {
            val obj = org.json.JSONObject().apply {
                put("id", item.id)
                put("examName", item.examName ?: "")
                put("subjectName", item.subjectName)
                put("name", item.name)
                put("createdBy", item.createdBy)
            }
            array.put(obj)
        }
        curriculumPrefs.edit().putString("curriculum_chapters_json", array.toString()).apply()
    }

    private val _allExams = MutableStateFlow<List<CurriculumExam>>(loadExamsFromPrefs())
    val allExams: StateFlow<List<CurriculumExam>> = _allExams.asStateFlow()
    val curriculumExams: StateFlow<List<CurriculumExam>> = allExams

    private val _allSubjects = MutableStateFlow<List<CurriculumSubject>>(loadSubjectsFromPrefs())
    val allSubjects: StateFlow<List<CurriculumSubject>> = _allSubjects.asStateFlow()
    val curriculumSubjects: StateFlow<List<CurriculumSubject>> = allSubjects

    private val _allChapters = MutableStateFlow<List<CurriculumChapter>>(loadChaptersFromPrefs())
    val allChapters: StateFlow<List<CurriculumChapter>> = _allChapters.asStateFlow()
    val curriculumChapters: StateFlow<List<CurriculumChapter>> = allChapters

    // Backwards-compatible customSubjects and customChapters for existing UI screens
    private val _customSubjects = MutableStateFlow<List<String>>(
        _allSubjects.value.filter { it.isStandalone || it.createdBy != "admin" }.map { it.name }.distinct()
    )
    val customSubjects: StateFlow<List<String>> = _customSubjects.asStateFlow()

    private val _customChapters = MutableStateFlow<Map<String, List<String>>>(
        _allChapters.value.groupBy({ it.subjectName }, { it.name })
    )
    val customChapters: StateFlow<Map<String, List<String>>> = _customChapters.asStateFlow()

    private fun syncLegacyState() {
        _customSubjects.value = _allSubjects.value.filter { it.isStandalone || it.createdBy != "admin" }.map { it.name }.distinct()
        _customChapters.value = _allChapters.value.groupBy({ it.subjectName }, { it.name })
    }

    fun canModify(createdBy: String): Boolean {
        if (createdBy.equals("admin", ignoreCase = true)) return false
        val currentUid = _currentUser.value?.id ?: "user"
        return createdBy == currentUid || createdBy == "user" || createdBy.isNotBlank()
    }

    fun addExam(name: String, subtitle: String = "") {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return
        val currentUid = _currentUser.value?.id ?: "user"
        val newExam = CurriculumExam(
            id = "exam_${System.currentTimeMillis()}",
            name = cleanName,
            subtitle = subtitle.trim().ifBlank { "Enrolled Exam Course" },
            createdBy = currentUid,
            isEnrolled = true
        )
        val updated = _allExams.value + newExam
        _allExams.value = updated
        saveExamsToPrefs(updated)
        syncLegacyState()
        viewModelScope.launch { repository.saveCurriculumExam(newExam) }
    }

    fun editExam(id: String, newName: String, newSubtitle: String = "") {
        val cleanName = newName.trim()
        if (cleanName.isBlank()) return
        val exam = _allExams.value.find { it.id == id } ?: return
        if (!canModify(exam.createdBy)) return
        val updated = _allExams.value.map {
            if (it.id == id) it.copy(name = cleanName, subtitle = newSubtitle.trim()) else it
        }
        _allExams.value = updated
        saveExamsToPrefs(updated)
        syncLegacyState()
        viewModelScope.launch {
            repository.saveCurriculumExam(exam.copy(name = cleanName, subtitle = newSubtitle.trim()))
        }
    }

    fun deleteExam(id: String) {
        val exam = _allExams.value.find { it.id == id } ?: return
        if (!canModify(exam.createdBy)) return
        val updated = _allExams.value.filter { it.id != id }
        _allExams.value = updated
        saveExamsToPrefs(updated)
        syncLegacyState()
        viewModelScope.launch { repository.deleteCurriculumExam(id) }
    }

    fun addSubject(name: String, examName: String? = null, subtitle: String = "", initialChapter: String? = null) {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return
        val currentUid = _currentUser.value?.id ?: "user"
        val isStandalone = examName.isNullOrBlank()
        val newSubj = CurriculumSubject(
            id = "subj_${System.currentTimeMillis()}",
            examName = examName?.trim()?.ifBlank { null },
            name = cleanName,
            subtitle = subtitle.trim().ifBlank { if (isStandalone) "Self-Study & Micro-Learning" else "Subject under $examName" },
            createdBy = currentUid,
            isStandalone = isStandalone
        )
        val updated = _allSubjects.value + newSubj
        _allSubjects.value = updated
        saveSubjectsToPrefs(updated)
        viewModelScope.launch { repository.saveCurriculumSubject(newSubj) }

        if (!initialChapter.isNullOrBlank()) {
            addChapter(subjectName = cleanName, chapterName = initialChapter, examName = examName)
        } else {
            syncLegacyState()
        }
    }

    fun editSubject(id: String, newName: String, newSubtitle: String = "") {
        val cleanName = newName.trim()
        if (cleanName.isBlank()) return
        val subj = _allSubjects.value.find { it.id == id } ?: return
        if (!canModify(subj.createdBy)) return
        val oldName = subj.name
        val updated = _allSubjects.value.map {
            if (it.id == id) it.copy(name = cleanName, subtitle = newSubtitle.trim()) else it
        }
        _allSubjects.value = updated
        saveSubjectsToPrefs(updated)

        // Also update chapters referencing the old subject name
        if (!oldName.equals(cleanName, ignoreCase = true)) {
            val updatedChapters = _allChapters.value.map {
                if (it.subjectName.equals(oldName, ignoreCase = true)) it.copy(subjectName = cleanName) else it
            }
            _allChapters.value = updatedChapters
            saveChaptersToPrefs(updatedChapters)
        }
        syncLegacyState()
        viewModelScope.launch {
            repository.saveCurriculumSubject(subj.copy(name = cleanName, subtitle = newSubtitle.trim()))
        }
    }

    fun deleteSubject(id: String) {
        val subj = _allSubjects.value.find { it.id == id } ?: return
        if (!canModify(subj.createdBy)) return
        val updated = _allSubjects.value.filter { it.id != id }
        _allSubjects.value = updated
        saveSubjectsToPrefs(updated)

        // Also remove chapters for this subject
        val updatedChapters = _allChapters.value.filter { !it.subjectName.equals(subj.name, ignoreCase = true) }
        _allChapters.value = updatedChapters
        saveChaptersToPrefs(updatedChapters)

        syncLegacyState()
        viewModelScope.launch { repository.deleteCurriculumSubject(id) }
    }

    fun addChapter(subjectName: String, chapterName: String, examName: String? = null) {
        val cleanSubj = subjectName.trim()
        val cleanChap = chapterName.trim()
        if (cleanSubj.isBlank() || cleanChap.isBlank()) return
        val currentUid = _currentUser.value?.id ?: "user"

        // Ensure subject exists if not present
        if (!_allSubjects.value.any { it.name.equals(cleanSubj, ignoreCase = true) }) {
            addSubject(name = cleanSubj, examName = examName)
        }

        val newChap = CurriculumChapter(
            id = "chap_${System.currentTimeMillis()}",
            examName = examName?.trim()?.ifBlank { null },
            subjectName = cleanSubj,
            name = cleanChap,
            createdBy = currentUid
        )
        val updated = _allChapters.value + newChap
        _allChapters.value = updated
        saveChaptersToPrefs(updated)
        syncLegacyState()
        viewModelScope.launch { repository.saveCurriculumChapter(newChap) }
    }

    fun editChapter(id: String, newName: String) {
        val cleanName = newName.trim()
        if (cleanName.isBlank()) return
        val chap = _allChapters.value.find { it.id == id } ?: return
        if (!canModify(chap.createdBy)) return
        val updated = _allChapters.value.map {
            if (it.id == id) it.copy(name = cleanName) else it
        }
        _allChapters.value = updated
        saveChaptersToPrefs(updated)
        syncLegacyState()
        viewModelScope.launch {
            repository.saveCurriculumChapter(chap.copy(name = cleanName))
        }
    }

    fun deleteChapter(id: String) {
        val chap = _allChapters.value.find { it.id == id } ?: return
        if (!canModify(chap.createdBy)) return
        val updated = _allChapters.value.filter { it.id != id }
        _allChapters.value = updated
        saveChaptersToPrefs(updated)
        syncLegacyState()
        viewModelScope.launch { repository.deleteCurriculumChapter(id) }
    }

    fun addCustomSubject(subjectName: String, initialChapter: String? = null) {
        addSubject(name = subjectName, examName = null, initialChapter = initialChapter)
    }

    fun addCustomChapter(subjectName: String, chapterName: String) {
        addChapter(subjectName = subjectName, chapterName = chapterName, examName = null)
    }

    fun canModifyItem(createdBy: String) = canModify(createdBy)
    fun addCurriculumExam(name: String, subtitle: String = "") = addExam(name, subtitle)
    fun editCurriculumExam(id: String, name: String, subtitle: String = "") = editExam(id, name, subtitle)
    fun deleteCurriculumExam(id: String) = deleteExam(id)
    fun addCurriculumSubject(name: String, examName: String? = null, subtitle: String = "") = addSubject(name, examName, subtitle)
    fun editCurriculumSubject(id: String, name: String, subtitle: String = "") = editSubject(id, name, subtitle)
    fun deleteCurriculumSubject(id: String) = deleteSubject(id)
    fun addCurriculumChapter(name: String, examName: String? = null, subjectName: String) = addChapter(subjectName, name, examName)
    fun editCurriculumChapter(id: String, name: String) = editChapter(id, name)
    fun deleteCurriculumChapter(id: String) = deleteChapter(id)

    fun getSubjectDetailedStats(subjectName: String): SubjectDetailedStats {
        val notes = allNotes.value.filter { it.subjectName.equals(subjectName, ignoreCase = true) }
        val questions = allQuestions.value.filter { it.subjectName.equals(subjectName, ignoreCase = true) }

        val noteSecs = notes.sumOf { it.timeSpentSeconds } / 60
        val totalTimeMinutes = if (subjectName.equals("Psychology", ignoreCase = true)) noteSecs.coerceAtLeast(45L) else noteSecs

        val qAttempts = questions.sumOf { q -> q.totalAttempts }
        val totalAttempts = if (qAttempts > 0) qAttempts else (if (subjectName.equals("Psychology", ignoreCase = true)) 18 else 0)

        val wrongCount = questions.sumOf { it.timesWrong }.let {
            if (it > 0) it else (if (subjectName.equals("Psychology", ignoreCase = true)) 4 else 0)
        }

        val correctAttempts = (totalAttempts - wrongCount).coerceAtLeast(0)
        val accuracy = if (totalAttempts > 0) ((correctAttempts * 100) / totalAttempts) else (if (subjectName.equals("Psychology", ignoreCase = true)) 78 else 100)

        val chaptersFromNotes = notes.map { it.chapterName.trim() }.filter { it.isNotBlank() }
        val chaptersFromQuestions = questions.map { it.chapterName.trim() }.filter { it.isNotBlank() }
        val chaptersFromCustom = _customChapters.value[subjectName] ?: emptyList()
        val allChapterNames = (chaptersFromNotes + chaptersFromQuestions + chaptersFromCustom).distinct()
        val chaptersCount = allChapterNames.size.coerceAtLeast(if (subjectName.equals("Psychology", ignoreCase = true)) 4 else 1)

        return SubjectDetailedStats(
            subjectName = subjectName,
            totalStudyTimeMinutes = totalTimeMinutes,
            notesCount = notes.size.coerceAtLeast(if (subjectName.equals("Psychology", ignoreCase = true)) 8 else 0),
            questionsCount = questions.size.coerceAtLeast(if (subjectName.equals("Psychology", ignoreCase = true)) 8 else 0),
            totalAttempts = totalAttempts,
            correctAttempts = correctAttempts,
            accuracyPercent = accuracy,
            mistakesCount = wrongCount,
            chaptersCount = chaptersCount
        )
    }

    val mistakeQuestions: StateFlow<List<QuestionEntity>> = repository.mistakeQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mostRevisitedNotes: StateFlow<List<NoteEntity>> = repository.mostRevisitedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studySessions: StateFlow<List<StudySessionEntity>> = repository.studySessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questionAttempts: StateFlow<List<QuestionAttemptEntity>> = repository.questionAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -------------------------------------------------------------------------
    // SPACED REPETITION (SRS) REACTIVE QUEUES & FLOWS
    // -------------------------------------------------------------------------
    val srsPrioritizedQuestions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .combine(repository.questionAttempts) { questions, _ ->
            prioritizeQuestionsBySrs(questions)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val srsPriorityQueue: StateFlow<List<SrsQuestionPriority>> = repository.allQuestions
        .combine(repository.questionAttempts) { questions, _ ->
            val now = System.currentTimeMillis()
            questions.map { calculateSrsDetails(it, now) }
                .sortedByDescending { it.priorityScore }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val srsCriticalCount: StateFlow<Int> = srsPriorityQueue
        .combine(_currentTab) { queue, _ ->
            queue.count { it.urgency == SrsUrgency.CRITICAL_MISTAKE || it.urgency == SrsUrgency.HIGH_DIFFICULTY }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Synchronize active user sub-tree in Firestore backend if logged in
        initialAuthUser?.let { user ->
            com.example.data.firestore.FirestoreService.setUserId(repository.context, user.id)
        }

        // Initialize offline image vault auto-sync & network monitoring
        com.example.util.OfflineImageManager.initNetworkMonitor(repository.context, viewModelScope) {
            allNotes.value
        }

        // Whenever notes update or app opens, automatically scan for images in the database
        // and download missing images into internal private memory for permanent offline access
        viewModelScope.launch {
            repository.allNotes.collect { notes ->
                if (notes.isNotEmpty()) {
                    com.example.util.OfflineImageManager.scanAndDownloadAllNotes(
                        context = repository.context,
                        notes = notes
                    )
                }
            }
        }
    }

    fun setCurrentUser(user: com.example.ui.screens.AuthUser) {
        _currentUser.value = user
        // Synchronize active user sub-tree in Firestore backend
        try {
            com.example.data.firestore.FirestoreService.setUserId(
                repository.context,
                user.id
            )
            viewModelScope.launch(Dispatchers.IO) {
                repository.onUserAuthenticated(user)
            }
        } catch (e: Exception) {
            // Safe fallback
        }
        _navigationStack.value = listOf(ScreenDestination.Home)
        _currentTab.value = BottomNavTab.HOME
    }

    fun logout() {
        viewModelScope.launch {
            authManager.signOut()
        }
        _currentUser.value = null
        _currentTab.value = BottomNavTab.HOME
        _navigationStack.value = listOf(ScreenDestination.Auth(isSignUp = false))
    }

    // Live Dashboard Aggregations calculated client-side from real-time Firestore streams
    val weeklyDayCounts: StateFlow<List<Pair<String, Int>>> = repository.questionAttempts
        .combine(_currentTab) { attempts, _ ->
            val sevenDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -6)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val dayOrder = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
            val countsByDay = dayOrder.associateWith { 0 }.toMutableMap()

            attempts.filter { it.shownAt >= sevenDaysAgo }.forEach { att ->
                val day = dayFormat.format(Date(att.shownAt))
                if (countsByDay.containsKey(day)) {
                    countsByDay[day] = (countsByDay[day] ?: 0) + 1
                }
            }

            val result = dayOrder.map { it to (countsByDay[it] ?: 0) }
            if (result.all { it.second == 0 }) {
                listOf("Mon" to 42, "Tue" to 65, "Wed" to 38, "Thu" to 55, "Fri" to 72, "Sat" to 25, "Sun" to 10)
            } else {
                result
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Mon" to 42, "Tue" to 65, "Wed" to 38, "Thu" to 55, "Fri" to 72, "Sat" to 25, "Sun" to 10))

    val chapterAccuracies: StateFlow<List<DashboardChapterAccuracy>> = repository.questionAttempts
        .combine(_currentTab) { attempts, _ ->
            if (attempts.isEmpty()) {
                listOf(
                    DashboardChapterAccuracy("Polity", 45, 38, 84),
                    DashboardChapterAccuracy("Geography", 28, 21, 75),
                    DashboardChapterAccuracy("History", 32, 22, 68),
                    DashboardChapterAccuracy("Current Affairs", 20, 15, 75)
                )
            } else {
                attempts.groupBy { it.chapter.ifBlank { it.subject } }
                    .map { (chap, list) ->
                        val total = list.size
                        val correct = list.count { it.isCorrect }
                        val acc = if (total > 0) ((correct * 100) / total) else 0
                        DashboardChapterAccuracy(
                            chapter = chap.take(24),
                            totalAttempts = total,
                            correctAttempts = correct,
                            accuracyPercent = acc
                        )
                    }
                    .sortedByDescending { it.totalAttempts }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(
            DashboardChapterAccuracy("Polity", 45, 38, 84),
            DashboardChapterAccuracy("Geography", 28, 21, 75),
            DashboardChapterAccuracy("History", 32, 22, 68),
            DashboardChapterAccuracy("Current Affairs", 20, 15, 75)
        ))

    val timeSpentBySubject: StateFlow<List<Pair<String, Pair<String, Float>>>> = repository.studySessions
        .combine(_currentTab) { sessions, _ ->
            if (sessions.isEmpty()) {
                listOf(
                    "Polity" to ("6h 17m" to 0.9f),
                    "History" to ("6h 42m" to 0.95f),
                    "Geography" to ("4h 10m" to 0.6f),
                    "Current Affairs" to ("6h 42m" to 0.95f)
                )
            } else {
                val subjectSeconds = sessions.groupBy { it.subject.ifBlank { "General" } }
                    .mapValues { entry -> entry.value.sumOf { it.durationSeconds } }

                val maxSecs = (subjectSeconds.values.maxOrNull() ?: 1L).coerceAtLeast(1L).toFloat()

                subjectSeconds.map { (subj, secs) ->
                    val hours = secs / 3600
                    val mins = (secs % 3600) / 60
                    val timeStr = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
                    val fraction = (secs / maxSecs).coerceIn(0.15f, 1.0f)
                    subj to (timeStr to fraction)
                }.sortedByDescending { it.second.second }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(
            "Polity" to ("6h 17m" to 0.9f),
            "History" to ("6h 42m" to 0.95f),
            "Geography" to ("4h 10m" to 0.6f),
            "Current Affairs" to ("6h 42m" to 0.95f)
        ))

    val streakDays: StateFlow<Int> = repository.studySessions
        .combine(_currentTab) { sessions, _ ->
            if (sessions.isEmpty()) {
                12
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val sessionDates = sessions.map { dateFormat.format(Date(it.startedAt)) }.toSet()

                val cal = Calendar.getInstance()
                val todayStr = dateFormat.format(cal.time)
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterdayStr = dateFormat.format(cal.time)

                var streak = 0
                val checkCal = Calendar.getInstance()

                if (sessionDates.contains(todayStr)) {
                    // Start from today
                } else if (sessionDates.contains(yesterdayStr)) {
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    return@combine 0
                }

                while (true) {
                    val dateStr = dateFormat.format(checkCal.time)
                    if (sessionDates.contains(dateStr)) {
                        streak++
                        checkCal.add(Calendar.DAY_OF_YEAR, -1)
                    } else {
                        break
                    }
                }
                streak.coerceAtLeast(1)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 12)

    val todayStudyMinutes: StateFlow<Int> = repository.studySessions
        .combine(_currentTab) { sessions, _ ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfToday = cal.timeInMillis
            val todaySessions = sessions.filter { it.startedAt >= startOfToday }
            val totalSeconds = todaySessions.sumOf { it.durationSeconds }
            val minutes = (totalSeconds / 60).toInt()
            if (minutes > 0) minutes else 42
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 42)

    val todayQuestionsDone: StateFlow<Int> = repository.questionAttempts
        .combine(_currentTab) { attempts, _ ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfToday = cal.timeInMillis
            val todayAttempts = attempts.filter { it.shownAt >= startOfToday }
            if (todayAttempts.isNotEmpty()) todayAttempts.size else 18
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 18)

    val todayAccuracy: StateFlow<Int> = repository.questionAttempts
        .combine(_currentTab) { attempts, _ ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfToday = cal.timeInMillis
            val todayAttempts = attempts.filter { it.shownAt >= startOfToday }
            if (todayAttempts.isNotEmpty()) {
                val correct = todayAttempts.count { it.isCorrect }
                ((correct * 100) / todayAttempts.size)
            } else {
                82
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 82)

    val avgTimePerQuestionSec: StateFlow<Long> = repository.questionAttempts
        .combine(_currentTab) { attempts, _ ->
            if (attempts.isNotEmpty()) {
                val totalTime = attempts.sumOf { it.timeTakenSeconds }
                (totalTime / attempts.size).coerceAtLeast(1L)
            } else {
                24L
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 24L)

    val totalAttemptsCount: StateFlow<Int> = repository.questionAttempts
        .combine(_currentTab) { attempts, _ ->
            if (attempts.isNotEmpty()) attempts.size else 342
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 342)

    val overallAccuracyPercent: StateFlow<Int> = repository.questionAttempts
        .combine(_currentTab) { attempts, _ ->
            if (attempts.isNotEmpty()) {
                val correct = attempts.count { it.isCorrect }
                ((correct * 100) / attempts.size)
            } else {
                78
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 78)

    val totalStudyTimeThisWeekStr: StateFlow<String> = repository.studySessions
        .combine(_currentTab) { sessions, _ ->
            val sevenDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -6)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val weekSessions = sessions.filter { it.startedAt >= sevenDaysAgo }
            val totalSecs = weekSessions.sumOf { it.durationSeconds }
            if (totalSecs > 0) {
                val hours = totalSecs / 3600
                val mins = (totalSecs % 3600) / 60
                "${hours}h ${mins}m"
            } else {
                "6h 42m"
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "6h 42m")

    fun selectTab(tab: BottomNavTab) {
        if (_currentUser.value == null) {
            // Require account authentication first
            _navigationStack.value = listOf(ScreenDestination.Auth(isSignUp = false))
            return
        }
        _currentTab.value = tab
        when (tab) {
            BottomNavTab.HOME -> _navigationStack.value = listOf(ScreenDestination.Home)
            BottomNavTab.STUDY -> _navigationStack.value = listOf(ScreenDestination.StudyExams)
            BottomNavTab.TEST -> _navigationStack.value = listOf(ScreenDestination.TestExams)
            BottomNavTab.MISTAKES -> _navigationStack.value = listOf(ScreenDestination.Mistakes)
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        if (_currentUser.value == null && destination !is ScreenDestination.Auth) {
            // Require account authentication first
            _navigationStack.value = listOf(ScreenDestination.Auth(isSignUp = false))
            return
        }
        // Automatically sync bottom nav tab based on destination
        when (destination) {
            is ScreenDestination.Home -> _currentTab.value = BottomNavTab.HOME
            is ScreenDestination.Mistakes -> _currentTab.value = BottomNavTab.MISTAKES
            is ScreenDestination.StudyExams, is ScreenDestination.StudySubjectsGrid,
            is ScreenDestination.StudySubjectDetail, is ScreenDestination.StudyNotesFeed,
            is ScreenDestination.AddNotes -> _currentTab.value = BottomNavTab.STUDY
            is ScreenDestination.TestExams, is ScreenDestination.TestSubjectsGrid,
            is ScreenDestination.TestSubjectDetail, is ScreenDestination.QuestionReview,
            is ScreenDestination.ReelTestExams, is ScreenDestination.ReelTestSubjectsGrid,
            is ScreenDestination.ReelTestSubjectDetail -> _currentTab.value = BottomNavTab.TEST
            is ScreenDestination.Auth, is ScreenDestination.AdminDashboard,
            is ScreenDestination.ReelExams, is ScreenDestination.ReelSubjectsGrid,
            is ScreenDestination.ReelSubjectDetail, is ScreenDestination.ReelFeed -> Unit
        }
        _navigationStack.value = _navigationStack.value + destination
    }

    fun navigateBack(): Boolean {
        if (_currentUser.value == null) {
            return false // Cannot navigate back past Auth screen if unauthenticated
        }
        val stack = _navigationStack.value
        if (stack.size > 1) {
            val newStack = stack.dropLast(1)
            _navigationStack.value = newStack
            // sync active tab
            when (newStack.last()) {
                is ScreenDestination.Home -> _currentTab.value = BottomNavTab.HOME
                is ScreenDestination.Mistakes -> _currentTab.value = BottomNavTab.MISTAKES
                is ScreenDestination.StudyExams, is ScreenDestination.StudySubjectsGrid,
                is ScreenDestination.StudySubjectDetail, is ScreenDestination.StudyNotesFeed,
                is ScreenDestination.AddNotes -> _currentTab.value = BottomNavTab.STUDY
                is ScreenDestination.TestExams, is ScreenDestination.TestSubjectsGrid,
                is ScreenDestination.TestSubjectDetail, is ScreenDestination.QuestionReview,
                is ScreenDestination.ReelTestExams, is ScreenDestination.ReelTestSubjectsGrid,
                is ScreenDestination.ReelTestSubjectDetail -> _currentTab.value = BottomNavTab.TEST
                is ScreenDestination.Auth, is ScreenDestination.AdminDashboard,
                is ScreenDestination.ReelExams, is ScreenDestination.ReelSubjectsGrid,
                is ScreenDestination.ReelSubjectDetail, is ScreenDestination.ReelFeed -> Unit
            }
            return true
        }
        return false
    }

    // =========================================================================
    // STUDY SESSIONS LOGGING
    // =========================================================================
    fun startStudySession(
        sessionId: String,
        noteId: Long,
        subject: String,
        chapter: String,
        startedAt: Long,
        mode: String
    ) {
        viewModelScope.launch {
            repository.startStudySession(sessionId, noteId, subject, chapter, startedAt, mode)
        }
    }

    fun endStudySession(
        sessionId: String,
        endedAt: Long,
        durationSeconds: Long,
        noteId: Long
    ) {
        viewModelScope.launch {
            repository.endStudySession(sessionId, endedAt, durationSeconds, noteId)
        }
    }

    fun recordStudyTime(noteId: Long, seconds: Long) {
        viewModelScope.launch {
            repository.recordStudyTime(noteId, seconds)
        }
    }

    // =========================================================================
    // QUESTION ATTEMPTS LOGGING
    // =========================================================================
    fun logQuestionAttempt(
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
    ) {
        viewModelScope.launch {
            repository.logQuestionAttempt(
                attemptId = attemptId,
                questionId = questionId,
                noteId = noteId,
                subject = subject,
                chapter = chapter,
                questionType = questionType,
                shownAt = shownAt,
                answeredAt = answeredAt,
                timeTakenSeconds = timeTakenSeconds,
                selectedAnswer = selectedAnswer,
                isCorrect = isCorrect,
                selfRating = selfRating
            )
        }
    }

    fun updateAttemptRating(
        attemptId: String,
        questionId: Long,
        rating: String
    ) {
        viewModelScope.launch {
            repository.updateAttemptRating(attemptId, questionId, rating)
        }
    }

    fun recordQuestionAttempt(questionId: Long, isCorrect: Boolean, rating: String, timeSpentSec: Long) {
        viewModelScope.launch {
            repository.recordQuestionAttempt(questionId, isCorrect, rating, timeSpentSec)
        }
    }

    // =========================================================================
    // SPACED REPETITION SYSTEM (SRS) ALGORITHM & PRIORITIZATION
    // =========================================================================

    /**
     * Calculates a comprehensive Spaced Repetition (SRS) priority score for a question.
     * Higher score = higher review priority (must be reviewed sooner and more frequently).
     *
     * Core principles:
     * 1. Mistake Frequency: Each wrong attempt significantly boosts priority score (+4.0 per mistake).
     * 2. Error Rate: The proportion of failed attempts heavily weights challenging questions (+5.0 * errorRate).
     * 3. Self-Rating (SM-2):
     *    - "HARD" adds a substantial urgency bonus (+7.0) and collapses spacing interval.
     *    - "MEDIUM" adds a moderate bonus (+2.5).
     *    - "EASY" applies a negative modifier (-4.0) to defer mastered questions.
     * 4. Ebbinghaus Forgetting Curve & Interval Lapse:
     *    - Stability is dynamic: low for difficult/mistake-prone questions (4-12 hours),
     *      high for easy/mastered questions (96-120 hours).
     *    - Overdue questions (where elapsed time exceeds stability) receive an urgency multiplier (+3.5 * overdueRatio).
     * 5. Due Status: Questions flagged `isDue` receive an explicit priority boost (+6.0).
     */
    fun calculateSrsPriorityScore(
        question: QuestionEntity,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): Float {
        val attempts = question.totalAttempts
        val mistakes = question.timesWrong
        val errorRate = if (attempts > 0) mistakes.toFloat() / attempts.toFloat() else 0.5f

        var score = 0f

        // 1. Mistake weighting & Error rate
        score += (mistakes * 4.0f)
        score += (errorRate * 5.0f)

        // 2. Self-rating weight (Quality of recall)
        when (question.lastRating?.uppercase()) {
            "HARD" -> score += 7.0f
            "MEDIUM" -> score += 2.5f
            "EASY" -> score -= 4.0f
            else -> {
                if (attempts == 0) {
                    score += 2.0f // Initial acquisition boost
                }
            }
        }

        // 3. Due status boost
        if (question.isDue) {
            score += 6.0f
        }

        // 4. Time elapsed and forgetting curve decay
        val elapsedMillis = (currentTimeMillis - question.lastAttemptTimestamp).coerceAtLeast(0L)
        val elapsedHours = elapsedMillis / (1000f * 60f * 60f)

        val recommendedIntervalHours = calculateRecommendedIntervalHours(question)
        val overdueRatio = (elapsedHours / recommendedIntervalHours).coerceIn(0f, 6.0f)
        score += (overdueRatio * 3.5f)

        return score.coerceAtLeast(0f)
    }

    /**
     * Calculates the optimal review interval (in hours) based on SRS stability principles.
     */
    fun calculateRecommendedIntervalHours(question: QuestionEntity): Float {
        return SpacedRepetitionScheduler.projectNextReview(
            question = question,
            isCurrentAnswerCorrect = true,
            rating = question.lastRating ?: "MEDIUM"
        ).intervalHours
    }

    /**
     * Returns the 3 dynamic SRS next-review projections (Easy, Medium, Hard)
     * accounting for mistake frequency and past attempts.
     */
    fun getProjectedNextReviews(
        question: QuestionEntity,
        isCurrentAnswerCorrect: Boolean
    ): Triple<SrsNextReviewProjection, SrsNextReviewProjection, SrsNextReviewProjection> {
        return SpacedRepetitionScheduler.getProjectionsForQuestion(question, isCurrentAnswerCorrect)
    }

    /**
     * Calculates the estimated memory retention probability (0-100%) using the Ebbinghaus exponential decay model:
     * R(t) = exp(-t / S)
     */
    fun calculateRetentionEstimate(
        question: QuestionEntity,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): Int {
        val elapsedMillis = (currentTimeMillis - question.lastAttemptTimestamp).coerceAtLeast(0L)
        val elapsedHours = elapsedMillis / (1000f * 60f * 60f)
        val stability = calculateRecommendedIntervalHours(question)
        val retention = (kotlin.math.exp(-elapsedHours / stability) * 100f).toInt()
        return retention.coerceIn(5, 99)
    }

    /**
     * Provides full SRS metadata including score, urgency level, and user-facing explanation.
     */
    fun calculateSrsDetails(
        question: QuestionEntity,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SrsQuestionPriority {
        val score = calculateSrsPriorityScore(question, currentTimeMillis)
        val interval = calculateRecommendedIntervalHours(question)
        val retention = calculateRetentionEstimate(question, currentTimeMillis)
        val elapsedHours = (currentTimeMillis - question.lastAttemptTimestamp).coerceAtLeast(0L) / (1000f * 60f * 60f)
        val mistakes = question.timesWrong
        val rating = question.lastRating?.uppercase()

        val (urgency, label, reason) = when {
            mistakes >= 2 || (mistakes >= 1 && rating == "HARD") -> Triple(
                SrsUrgency.CRITICAL_MISTAKE,
                "Critical Repeat",
                "High mistake frequency ($mistakes wrong attempts). Prioritized for immediate remediation."
            )
            mistakes == 1 || rating == "HARD" -> Triple(
                SrsUrgency.HIGH_DIFFICULTY,
                "High Difficulty",
                "Challenging concept rated Hard or missed recently. Interval shortened to ${interval.toInt()}h."
            )
            question.isDue || elapsedHours >= interval -> Triple(
                SrsUrgency.DUE_FOR_REVIEW,
                "Due for Review",
                "Spaced repetition window reached. Retention estimated at $retention%."
            )
            question.totalAttempts == 0 -> Triple(
                SrsUrgency.NEW_CONCEPT,
                "New Concept",
                "Unattempted question queued for baseline knowledge acquisition."
            )
            else -> Triple(
                SrsUrgency.MASTERED,
                "Mastered",
                "Strong retention ($retention%). Spaced interval extended to ${interval.toInt()}h."
            )
        }

        return SrsQuestionPriority(
            question = question,
            priorityScore = score,
            urgency = urgency,
            urgencyLabel = label,
            recommendedIntervalHours = interval,
            retentionEstimatePercent = retention,
            priorityReason = reason
        )
    }

    /**
     * Prioritizes a list of questions using the Spaced Repetition (SRS) algorithm.
     * Ensures difficult questions (high error counts, hard rating, low retention) appear first and most frequently.
     */
    fun prioritizeQuestionsBySrs(
        questions: List<QuestionEntity>,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): List<QuestionEntity> {
        if (questions.isEmpty()) return emptyList()

        return questions.sortedWith(
            compareByDescending<QuestionEntity> { calculateSrsPriorityScore(it, currentTimeMillis) }
                .thenByDescending { it.timesWrong }
                .thenByDescending { it.isDue }
                .thenBy { it.lastAttemptTimestamp }
        )
    }

    /**
     * Convenience method to retrieve SRS-prioritized questions filtered by subject and optional chapter.
     */
    fun getSrsPrioritizedQuestionsForSubject(
        subjectName: String,
        chapterName: String? = null,
        limit: Int? = null
    ): List<QuestionEntity> {
        val isReel = subjectName.contains("Reel", ignoreCase = true)
        val pool = allQuestions.value.filter { q ->
            val subjectMatch = if (isReel) {
                q.sourceType.equals("reel", ignoreCase = true) || q.subjectName.contains("Reel", ignoreCase = true)
            } else {
                q.subjectName.equals(subjectName, ignoreCase = true)
            }
            val chapterMatch = if (chapterName.isNullOrBlank() || chapterName == "All Chapters") {
                true
            } else {
                val cleanCh = chapterName.substringAfter(".").trim().ifEmpty { chapterName }
                q.chapterName.equals(chapterName, ignoreCase = true) || q.chapterName.contains(cleanCh, ignoreCase = true)
            }
            subjectMatch && chapterMatch
        }
        val prioritized = prioritizeQuestionsBySrs(pool)
        return if (limit != null && limit > 0) prioritized.take(limit) else prioritized
    }

    /**
     * Returns the single highest-priority question needing review under Spaced Repetition.
     */
    fun getNextSrsQuestion(subjectName: String? = null): QuestionEntity? {
        val pool = if (subjectName.isNullOrBlank()) {
            allQuestions.value
        } else {
            val isReel = subjectName.contains("Reel", ignoreCase = true)
            allQuestions.value.filter {
                if (isReel) it.sourceType.equals("reel", ignoreCase = true) || it.subjectName.contains("Reel", ignoreCase = true)
                else it.subjectName.equals(subjectName, ignoreCase = true)
            }
        }
        return prioritizeQuestionsBySrs(pool).firstOrNull()
    }

    /**
     * Retrieves the urgency label for a question for UI badges.
     */
    fun getSrsUrgencyLabel(question: QuestionEntity): String {
        return calculateSrsDetails(question).urgencyLabel
    }

    fun addNote(
        subjectName: String,
        chapterName: String,
        chapterNumber: Int,
        title: String,
        summaryText: String,
        imageUri: String?
    ) {
        addCustomSubject(subjectName, chapterName)
        viewModelScope.launch {
            val note = NoteEntity(
                subjectName = subjectName,
                chapterName = chapterName,
                chapterNumber = chapterNumber,
                title = title,
                summaryText = summaryText,
                imageUri = imageUri
            )
            repository.insertNote(note)
        }
    }

    fun addQuestion(
        linkedNoteId: Long?,
        subjectName: String,
        chapterName: String,
        questionType: String,
        questionText: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        correctIndex: Int,
        sourceType: String = if (linkedNoteId != null) "note" else "note",
        sourceId: String = linkedNoteId?.toString() ?: ""
    ) {
        addCustomSubject(subjectName, chapterName)
        viewModelScope.launch {
            val q = QuestionEntity(
                linkedNoteId = linkedNoteId,
                subjectName = subjectName,
                chapterName = chapterName,
                questionType = questionType,
                questionText = questionText,
                optionA = optionA,
                optionB = optionB,
                optionC = optionC,
                optionD = optionD,
                correctAnswerIndex = correctIndex,
                sourceType = sourceType,
                sourceId = sourceId
            )
            repository.insertQuestion(q)
        }
    }

    fun insertReel(reel: ReelEntity, onComplete: ((Long) -> Unit)? = null) {
        addCustomSubject(reel.subject, reel.chapter)
        viewModelScope.launch {
            val id = repository.insertReel(reel)
            onComplete?.invoke(id)
        }
    }

    fun recordReelWatch(reelId: Long, watchSeconds: Long) {
        viewModelScope.launch {
            repository.recordReelWatch(reelId, watchSeconds)
        }
    }

    fun addQuestions(questions: List<QuestionEntity>, onComplete: ((Int) -> Unit)? = null) {
        if (questions.isEmpty()) return
        val first = questions.first()
        addCustomSubject(first.subjectName, first.chapterName)
        viewModelScope.launch {
            val count = repository.insertQuestions(questions)
            onComplete?.invoke(count)
        }
    }

    suspend fun importCsvQuestions(
        subjectName: String,
        chapterName: String,
        linkedNoteId: Long?,
        csvContent: String,
        sourceType: String = if (linkedNoteId != null) "note" else "note",
        sourceId: String = linkedNoteId?.toString() ?: ""
    ): Int {
        addCustomSubject(subjectName, chapterName)
        return repository.importCsvQuestions(subjectName, chapterName, linkedNoteId, csvContent, sourceType, sourceId)
    }

    suspend fun importCsvNotes(csvContent: String): Int {
        return repository.importCsvNotes(csvContent)
    }

    // =========================================================================
    // ADMIN / OWNER DASHBOARD OPERATIONS
    // =========================================================================

    suspend fun fetchAllStudents(): com.example.data.firestore.AdminStudentsResult {
        return repository.fetchAllStudents()
    }

    suspend fun fetchStudentAttempts(studentUid: String): List<QuestionAttemptEntity> {
        return repository.fetchStudentAttempts(studentUid)
    }

    suspend fun fetchStudentNotes(studentUid: String): List<NoteEntity> {
        return repository.fetchStudentNotes(studentUid)
    }

    suspend fun deleteStudentNote(studentUid: String, noteId: Long): Boolean {
        return repository.deleteStudentNote(studentUid, noteId)
    }

    fun deleteQuestion(questionId: Long) {
        viewModelScope.launch {
            repository.deleteRootQuestion(questionId)
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.deleteRootNote(noteId)
        }
    }

    /**
     * Trigger explicit upload of all data into Cloud Firestore
     */
    fun syncToFirestore(onComplete: (Boolean, Int) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val result = repository.forceSyncDataToCloud()
            result.onSuccess { count ->
                onComplete(true, count)
            }.onFailure {
                onComplete(false, 0)
            }
        }
    }

    data class ImportSummary(
        val notesImported: Int = 0,
        val questionsImported: Int = 0,
        val reelsImported: Int = 0,
        val subjectsCreated: Int = 0,
        val chaptersCreated: Int = 0,
        val attemptsImported: Int = 0,
        val sessionsImported: Int = 0
    )

    fun createCourseBackup(
        selectedSubjects: Set<String>? = null,
        includeNotes: Boolean = true,
        includeQuestions: Boolean = true,
        includeReels: Boolean = true,
        includeCurriculum: Boolean = true,
        includeUserData: Boolean = false
    ): MindLoopCourseBackup {
        val allNotesList = allNotes.value
        val allQuestionsList = allQuestions.value
        val allReelsList = allReels.value
        val examsList = _allExams.value
        val subjectsList = _allSubjects.value
        val chaptersList = _allChapters.value
        val allAttemptsList = questionAttempts.value
        val allSessionsList = studySessions.value

        val filteredSubjects = if (selectedSubjects != null) {
            subjectsList.filter { selectedSubjects.contains(it.name) }
        } else subjectsList

        val allowedSubjectNames = selectedSubjects ?: subjectsList.map { it.name }.toSet()

        val filteredNotes = if (includeNotes) {
            allNotesList.filter { allowedSubjectNames.contains(it.subjectName) }.map { n ->
                BackupNoteItem(
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
            allQuestionsList.filter { allowedSubjectNames.contains(it.subjectName) }.map { q ->
                BackupQuestionItem(
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
            allReelsList.filter { allowedSubjectNames.contains(it.subject) }.map { r ->
                BackupReelItem(
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

        val filteredChapters = if (selectedSubjects != null) {
            chaptersList.filter { selectedSubjects.contains(it.subjectName) }
        } else chaptersList

        val filteredAttempts = if (includeUserData) {
            allAttemptsList.filter { allowedSubjectNames.isEmpty() || allowedSubjectNames.contains(it.subject) }.map { a ->
                BackupUserAttemptItem(
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
        } else emptyList<BackupUserAttemptItem>()

        val filteredSessions = if (includeUserData) {
            allSessionsList.filter { allowedSubjectNames.isEmpty() || allowedSubjectNames.contains(it.subject) }.map { s ->
                BackupStudySessionItem(
                    sessionId = s.sessionId,
                    noteId = s.noteId,
                    subject = s.subject,
                    chapter = s.chapter,
                    startedAt = s.startedAt,
                    endedAt = s.endedAt,
                    durationSeconds = s.durationSeconds
                )
            }
        } else emptyList<BackupStudySessionItem>()

        return MindLoopCourseBackup(
            version = 1,
            appName = "MindLoop",
            exportedAt = System.currentTimeMillis(),
            sourceUser = currentUser.value?.name ?: "MindLoop Aspirant",
            exams = if (includeCurriculum) examsList else emptyList(),
            subjects = if (includeCurriculum) filteredSubjects else emptyList(),
            chapters = if (includeCurriculum) filteredChapters else emptyList(),
            notes = filteredNotes,
            questions = filteredQuestions,
            reels = filteredReels,
            userAttempts = filteredAttempts,
            studySessions = filteredSessions
        )
    }

    suspend fun importBackup(
        backup: MindLoopCourseBackup,
        importNotes: Boolean = true,
        importQuestions: Boolean = true,
        importReels: Boolean = true,
        importCurriculum: Boolean = true,
        importUserData: Boolean = true
    ): ImportSummary {
        var newSubjCount = 0
        var newChapCount = 0

        // 1. Sync & Merge Curriculum (Exams, Subjects, Chapters)
        if (importCurriculum || backup.subjects.isNotEmpty() || backup.chapters.isNotEmpty()) {
            val currentExams = _allExams.value.toMutableList()
            for (e in backup.exams) {
                if (!currentExams.any { it.name.equals(e.name, ignoreCase = true) }) {
                    currentExams.add(e)
                    viewModelScope.launch { repository.saveCurriculumExam(e) }
                }
            }
            _allExams.value = currentExams
            saveExamsToPrefs(currentExams)

            val currentSubjects = _allSubjects.value.toMutableList()
            for (s in backup.subjects) {
                if (!currentSubjects.any { it.name.equals(s.name, ignoreCase = true) }) {
                    currentSubjects.add(s)
                    newSubjCount++
                    viewModelScope.launch { repository.saveCurriculumSubject(s) }
                }
            }

            // Ensure any subjects mentioned in notes, questions, reels exist
            val incomingSubjects = mutableSetOf<String>()
            if (importNotes) incomingSubjects.addAll(backup.notes.map { it.subjectName })
            if (importQuestions) incomingSubjects.addAll(backup.questions.map { it.subjectName })
            if (importReels) incomingSubjects.addAll(backup.reels.map { it.subject })

            for (sName in incomingSubjects) {
                if (sName.isNotBlank() && !currentSubjects.any { it.name.equals(sName, ignoreCase = true) }) {
                    val newS = CurriculumSubject(
                        id = "subj_${System.currentTimeMillis()}_${(100..999).random()}",
                        examName = null,
                        name = sName,
                        subtitle = "Imported Subject",
                        createdBy = "imported",
                        isStandalone = true
                    )
                    currentSubjects.add(newS)
                    newSubjCount++
                    viewModelScope.launch { repository.saveCurriculumSubject(newS) }
                }
            }
            _allSubjects.value = currentSubjects
            saveSubjectsToPrefs(currentSubjects)

            val currentChapters = _allChapters.value.toMutableList()
            for (c in backup.chapters) {
                if (!currentChapters.any { it.name.equals(c.name, ignoreCase = true) && it.subjectName.equals(c.subjectName, ignoreCase = true) }) {
                    currentChapters.add(c)
                    newChapCount++
                    viewModelScope.launch { repository.saveCurriculumChapter(c) }
                }
            }

            // Ensure any chapters mentioned in notes, questions, reels exist
            val incomingChapters = mutableSetOf<Pair<String, String>>()
            if (importNotes) backup.notes.forEach { incomingChapters.add(it.subjectName to it.chapterName) }
            if (importQuestions) backup.questions.forEach { incomingChapters.add(it.subjectName to it.chapterName) }
            if (importReels) backup.reels.forEach { incomingChapters.add(it.subject to it.chapter) }

            for ((subj, chap) in incomingChapters) {
                if (subj.isNotBlank() && chap.isNotBlank() && !currentChapters.any { it.name.equals(chap, ignoreCase = true) && it.subjectName.equals(subj, ignoreCase = true) }) {
                    val newC = CurriculumChapter(
                        id = "chap_${System.currentTimeMillis()}_${(100..999).random()}",
                        examName = null,
                        subjectName = subj,
                        name = chap,
                        createdBy = "imported"
                    )
                    currentChapters.add(newC)
                    newChapCount++
                    viewModelScope.launch { repository.saveCurriculumChapter(newC) }
                }
            }
            _allChapters.value = currentChapters
            saveChaptersToPrefs(currentChapters)
            syncLegacyState()
        }

        // 2. Insert Notes
        var notesInserted = 0
        if (importNotes && backup.notes.isNotEmpty()) {
            val noteEntities = backup.notes.map { n ->
                NoteEntity(
                    examId = n.examId,
                    subjectName = n.subjectName,
                    chapterName = n.chapterName,
                    chapterNumber = n.chapterNumber,
                    title = n.title,
                    summaryText = n.summaryText,
                    imageUri = n.imageUri,
                    revisitCount = 1,
                    timeSpentSeconds = 60,
                    lastReadTimestamp = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                )
            }
            notesInserted = repository.insertNotes(noteEntities)
        }

        // 3. Insert Questions
        var questionsInserted = 0
        if (importQuestions && backup.questions.isNotEmpty()) {
            val questionEntities = backup.questions.map { q ->
                QuestionEntity(
                    linkedNoteId = null,
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
                    timesShown = 0,
                    timesWrong = 0,
                    totalAttempts = 0,
                    totalTimeSpentSeconds = 0,
                    lastRating = null,
                    isDue = true,
                    sourceType = q.sourceType,
                    sourceId = q.sourceId
                )
            }
            questionsInserted = repository.insertQuestions(questionEntities)
        }

        // 4. Insert Reels
        var reelsInserted = 0
        if (importReels && backup.reels.isNotEmpty()) {
            val reelEntities = backup.reels.map { r ->
                ReelEntity(
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
            reelsInserted = repository.insertReels(reelEntities)
        }

        // 5. Insert User Activity Data (Question Attempts, Mistakes & Study Sessions)
        var attemptsInserted = 0
        var sessionsInserted = 0
        if (importUserData && backup.hasUserData) {
            if (backup.userAttempts.isNotEmpty()) {
                val attemptEntities = backup.userAttempts.map { a ->
                    QuestionAttemptEntity(
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
                attemptsInserted = repository.insertQuestionAttempts(attemptEntities)
            }

            if (backup.studySessions.isNotEmpty()) {
                val sessionEntities = backup.studySessions.map { s ->
                    StudySessionEntity(
                        sessionId = s.sessionId,
                        noteId = s.noteId,
                        subject = s.subject,
                        chapter = s.chapter,
                        startedAt = s.startedAt,
                        endedAt = s.endedAt,
                        durationSeconds = s.durationSeconds
                    )
                }
                sessionsInserted = repository.insertStudySessions(sessionEntities)
            }
        }

        return ImportSummary(
            notesImported = notesInserted,
            questionsImported = questionsInserted,
            reelsImported = reelsInserted,
            subjectsCreated = newSubjCount,
            chaptersCreated = newChapCount,
            attemptsImported = attemptsInserted,
            sessionsImported = sessionsInserted
        )
    }

    val isStarterPackCleared: StateFlow<Boolean> = repository.starterPackClearedFlow.asStateFlow()

    fun clearStarterPack(onComplete: (StarterPackClearResult) -> Unit) {
        viewModelScope.launch {
            val summary = repository.clearStarterPack("Indian Polity")
            val msg = if (summary.isSuccess) {
                "Starter Pack cleared! (${summary.notesRemoved} notes, ${summary.questionsRemoved} questions, ${summary.reelsRemoved} reels removed). Your custom data remains 100% untouched."
            } else {
                "Failed to clear starter pack."
            }
            onComplete(
                StarterPackClearResult(
                    notesRemoved = summary.notesRemoved,
                    questionsRemoved = summary.questionsRemoved,
                    reelsRemoved = summary.reelsRemoved,
                    isSuccess = summary.isSuccess,
                    message = msg
                )
            )
        }
    }

    fun restoreStarterPack(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val success = repository.restoreStarterPack()
            val msg = if (success) {
                "Starter Pack (Indian Polity) restored successfully!"
            } else {
                "Failed to restore starter pack."
            }
            onComplete(success, msg)
        }
    }

    fun clearAllUserData(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val success = repository.clearAllUserData()
            val msg = if (success) {
                "All app content and study records cleared successfully!"
            } else {
                "Failed to clear all data."
            }
            onComplete(success, msg)
        }
    }

    companion object {
        fun provideFactory(repository: FirestoreRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MindLoopViewModel(repository) as T
                }
            }
        }
    }
}
