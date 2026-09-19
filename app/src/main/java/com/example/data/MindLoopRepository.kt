package com.example.data

import com.example.data.dao.NoteDao
import com.example.data.dao.QuestionAttemptDao
import com.example.data.dao.QuestionDao
import com.example.data.dao.StudyLogDao
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionAttemptEntity
import com.example.data.entity.QuestionEntity
import com.example.data.entity.StudySessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MindLoopRepository(
    private val noteDao: NoteDao,
    private val questionDao: QuestionDao,
    private val studyLogDao: StudyLogDao,
    private val questionAttemptDao: QuestionAttemptDao
) {
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val allQuestions: Flow<List<QuestionEntity>> = questionDao.getAllQuestions()
    val dueQuestions: Flow<List<QuestionEntity>> = questionDao.getDueQuestions()
    val mistakeQuestions: Flow<List<QuestionEntity>> = questionDao.getMistakeQuestions()
    val mostRevisitedNotes: Flow<List<NoteEntity>> = noteDao.getMostRevisitedNotes(3)
    val studySessions: Flow<List<StudySessionEntity>> = studyLogDao.getAllSessions()
    val questionAttempts: Flow<List<QuestionAttemptEntity>> = questionAttemptDao.getAllAttempts()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val initialNotes = InitialDataProvider.getInitialNotes()
        val initialQuestions = InitialDataProvider.getInitialQuestions()
        noteDao.insertNotes(initialNotes)
        questionDao.insertQuestions(initialQuestions)
        val existingSessions = studyLogDao.getAllSessions().first()
        if (existingSessions.isEmpty()) {
            studyLogDao.insertSessions(InitialDataProvider.getInitialSessions())
        }
    }

    suspend fun importCsvNotes(csvContent: String): Int {
        val notesToInsert = mutableListOf<NoteEntity>()
        val lines = csvContent.lines().filter { it.isNotBlank() }
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("note_id,", ignoreCase = true) || 
                trimmed.startsWith("subject,", ignoreCase = true) ||
                trimmed.startsWith("chapter,", ignoreCase = true) ||
                trimmed.startsWith("title,", ignoreCase = true)) {
                continue
            }
            // Parse CSV respecting quotes
            val tokens = mutableListOf<String>()
            val sb = StringBuilder()
            var inQuotes = false
            for (char in trimmed) {
                when {
                    char == '\"' -> inQuotes = !inQuotes
                    char == ',' && !inQuotes -> {
                        tokens.add(sb.toString().trim())
                        sb.clear()
                    }
                    else -> sb.append(char)
                }
            }
            tokens.add(sb.toString().trim())

            // Schema A (standard 7+ columns): note_id, subject, chapter, title, content, [timeSpent/image_uri], [revisit/image_uri], [image_uri]
            // Schema B (direct notes format): subject, chapter, title, content, [image_uri]
            if (tokens.size >= 4) {
                val hasExplicitId = tokens[0].any { it.isDigit() } && tokens.size >= 5
                val idNum = if (hasExplicitId) tokens[0].filter { it.isDigit() }.toLongOrNull() ?: 0L else 0L
                val subject = if (hasExplicitId) tokens[1] else tokens[0]
                val chapter = if (hasExplicitId) tokens[2] else tokens[1]
                val title = if (hasExplicitId) tokens[3] else tokens[2]
                val content = if (hasExplicitId) tokens[4] else tokens[3]
                
                // Detect image_uri / file path across remaining tokens (http, https, file:, content:, /storage, .jpg, .png, etc.)
                val remainingTokens = if (hasExplicitId) tokens.drop(5) else tokens.drop(4)
                val detectedImageUri = remainingTokens.firstOrNull { token ->
                    val lower = token.lowercase()
                    lower.startsWith("http://") || lower.startsWith("https://") || 
                    lower.startsWith("content://") || lower.startsWith("file://") || 
                    lower.startsWith("/") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || 
                    lower.endsWith(".png") || lower.endsWith(".webp")
                }?.ifBlank { null }

                val chapNumber = chapter.split(".").firstOrNull()?.trim()?.toIntOrNull() ?: 1

                notesToInsert.add(
                    NoteEntity(
                        id = idNum,
                        examId = "UPSI",
                        subjectName = subject,
                        chapterName = chapter,
                        chapterNumber = chapNumber,
                        title = title,
                        summaryText = content,
                        imageUri = detectedImageUri,
                        revisitCount = 1,
                        timeSpentSeconds = 120L
                    )
                )
            }
        }
        if (notesToInsert.isNotEmpty()) {
            noteDao.insertNotes(notesToInsert)
        }
        return notesToInsert.size
    }

    fun getNotesBySubject(subjectName: String): Flow<List<NoteEntity>> =
        noteDao.getNotesBySubject(subjectName)

    fun getNotesByChapter(subjectName: String, chapterName: String): Flow<List<NoteEntity>> =
        noteDao.getNotesByChapter(subjectName, chapterName)

    fun getNoteById(id: Long): Flow<NoteEntity?> = noteDao.getNoteById(id)

    suspend fun getNoteByIdDirect(id: Long): NoteEntity? = noteDao.getNoteByIdDirect(id)

    fun getQuestionsBySubject(subjectName: String): Flow<List<QuestionEntity>> =
        questionDao.getQuestionsBySubject(subjectName)

    fun getQuestionsByChapter(subjectName: String, chapterName: String): Flow<List<QuestionEntity>> =
        questionDao.getQuestionsByChapter(subjectName, chapterName)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun insertQuestion(question: QuestionEntity): Long = questionDao.insertQuestion(question)

    suspend fun insertQuestions(questions: List<QuestionEntity>) = questionDao.insertQuestions(questions)

    // =========================================================================
    // REAL-TIME STUDY SESSION LOGGING
    // =========================================================================
    /**
     * Start a new study session row immediately when user opens a note.
     * Persists initial record so partial session is preserved if app is killed/force closed.
     */
    suspend fun startStudySession(
        sessionId: String,
        noteId: Long,
        subject: String,
        chapter: String,
        startedAt: Long,
        mode: String
    ) {
        val dayOfWeek = SimpleDateFormat("EEE", Locale.ENGLISH).format(Date(startedAt))
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date(startedAt))

        val session = StudySessionEntity(
            sessionId = sessionId,
            noteId = noteId,
            subject = subject,
            chapter = chapter,
            startedAt = startedAt,
            endedAt = startedAt,
            durationSeconds = 0L,
            mode = mode,
            dayOfWeek = dayOfWeek,
            subjectName = subject,
            durationMinutes = 0,
            questionsAttempted = 0,
            dateStr = dateStr,
            timestamp = startedAt
        )
        studyLogDao.insertSession(session)
    }

    /**
     * Update study session row with end timestamp and computed duration.
     * Also increments note timeSpentSeconds and revisitCount in notes table.
     */
    suspend fun endStudySession(
        sessionId: String,
        endedAt: Long,
        durationSeconds: Long,
        noteId: Long
    ) {
        val durationMins = (durationSeconds / 60).toInt()
        studyLogDao.updateSessionEnd(
            sessionId = sessionId,
            endedAt = endedAt,
            durationSeconds = durationSeconds,
            durationMinutes = durationMins
        )
        if (durationSeconds > 0 && noteId > 0) {
            noteDao.recordStudySession(noteId, durationSeconds)
        }
    }

    /**
     * Legacy helper kept for direct duration updates if needed
     */
    suspend fun recordStudyTime(noteId: Long, seconds: Long) {
        if (seconds > 0) {
            noteDao.recordStudySession(noteId, seconds)
        }
    }

    // =========================================================================
    // REAL-TIME QUESTION ATTEMPT LOGGING
    // =========================================================================
    /**
     * Log question attempt immediately upon answer selection.
     * Returns the attemptId so subsequent self-rating can update the same row.
     */
    suspend fun logQuestionAttempt(
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
        val attempt = QuestionAttemptEntity(
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
        questionAttemptDao.insertAttempt(attempt)

        // Also update aggregate statistics in questions table
        val isDueNext = selfRating == "HARD" || !isCorrect
        val wrongIncrement = if (isCorrect) 0 else 1
        questionDao.recordQuestionAttempt(
            questionId = questionId,
            wrongIncrement = wrongIncrement,
            rating = selfRating ?: "MEDIUM",
            timeSpentSec = timeTakenSeconds,
            newDue = isDueNext,
            now = answeredAt
        )
    }

    /**
     * Update self-rating on the specific attempt row when user taps Easy / Medium / Hard.
     */
    suspend fun updateAttemptRating(
        attemptId: String,
        questionId: Long,
        rating: String
    ) {
        questionAttemptDao.updateSelfRating(attemptId, rating)
        val isDueNext = rating == "HARD"
        questionDao.recordQuestionAttempt(
            questionId = questionId,
            wrongIncrement = 0,
            rating = rating,
            timeSpentSec = 0L,
            newDue = isDueNext
        )
    }

    suspend fun recordQuestionAttempt(
        questionId: Long,
        isCorrect: Boolean,
        rating: String,
        timeSpentSec: Long
    ) {
        val isDueNext = rating == "HARD" || !isCorrect
        val wrongIncrement = if (isCorrect) 0 else 1
        questionDao.recordQuestionAttempt(
            questionId = questionId,
            wrongIncrement = wrongIncrement,
            rating = rating,
            timeSpentSec = timeSpentSec,
            newDue = isDueNext
        )
    }

    suspend fun importCsvQuestions(
        subjectName: String,
        chapterName: String,
        linkedNoteId: Long?,
        csvContent: String,
        sourceType: String = if (linkedNoteId != null) "note" else "note",
        sourceId: String = linkedNoteId?.toString() ?: ""
    ): Int {
        val questionsToInsert = mutableListOf<QuestionEntity>()
        val lines = csvContent.lines().filter { it.isNotBlank() }
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("Type,", ignoreCase = true) || 
                trimmed.startsWith("Question,", ignoreCase = true) ||
                trimmed.startsWith("question_id,", ignoreCase = true)) {
                continue
            }
            // Parse CSV respecting quotes
            val tokens = mutableListOf<String>()
            val sb = StringBuilder()
            var inQuotes = false
            for (char in trimmed) {
                when {
                    char == '\"' -> inQuotes = !inQuotes
                    char == ',' && !inQuotes -> {
                        tokens.add(sb.toString().trim())
                        sb.clear()
                    }
                    else -> sb.append(char)
                }
            }
            tokens.add(sb.toString().trim())

            // Check if full questions.csv format (16 columns)
            if (tokens.size >= 11 && tokens[0].startsWith("Q", ignoreCase = true)) {
                val qIdNum = tokens[0].filter { it.isDigit() }.toLongOrNull() ?: 0L
                val nIdNum = tokens[1].filter { it.isDigit() }.toLongOrNull() ?: linkedNoteId
                val subj = tokens.getOrNull(2)?.ifBlank { subjectName } ?: subjectName
                val chap = tokens.getOrNull(3)?.ifBlank { chapterName } ?: chapterName
                val rawType = tokens[4]
                val qText = tokens[5]
                val optA = tokens[6]
                val optB = tokens[7]
                val optC = tokens.getOrNull(8) ?: ""
                val optD = tokens.getOrNull(9) ?: ""
                val correctAns = tokens.getOrNull(10) ?: ""
                val shown = tokens.getOrNull(11)?.toIntOrNull() ?: 0
                val wrong = tokens.getOrNull(12)?.toIntOrNull() ?: 0
                val totalAtt = tokens.getOrNull(13)?.toIntOrNull() ?: 0
                val avgSec = tokens.getOrNull(14)?.toLongOrNull() ?: 15L
                val rating = tokens.getOrNull(15)?.uppercase()

                val isTf = rawType.contains("True", ignoreCase = true) || rawType.contains("TF", ignoreCase = true)
                val finalA: String
                val finalB: String
                val finalC: String
                val finalD: String
                val correctIdx: Int

                if (isTf) {
                    finalA = "True"
                    finalB = "False"
                    finalC = ""
                    finalD = ""
                    correctIdx = if (correctAns.equals("True", ignoreCase = true) || correctAns.equals("T", ignoreCase = true)) 0 else 1
                } else {
                    finalA = optA
                    finalB = optB
                    finalC = optC
                    finalD = optD
                    correctIdx = when {
                        correctAns.equals(optA, ignoreCase = true) -> 0
                        correctAns.equals(optB, ignoreCase = true) -> 1
                        correctAns.equals(optC, ignoreCase = true) -> 2
                        correctAns.equals(optD, ignoreCase = true) -> 3
                        else -> 0
                    }
                }

                questionsToInsert.add(
                    QuestionEntity(
                        id = qIdNum,
                        linkedNoteId = nIdNum,
                        examId = "UPSI",
                        subjectName = subj,
                        chapterName = chap,
                        questionType = if (isTf) "TRUE_FALSE" else "MULTIPLE_CHOICE",
                        questionText = qText,
                        optionA = finalA as String,
                        optionB = finalB as String,
                        optionC = finalC as String,
                        optionD = finalD as String,
                        correctAnswerIndex = correctIdx as Int,
                        timesShown = shown,
                        timesWrong = wrong,
                        totalAttempts = totalAtt,
                        totalTimeSpentSeconds = totalAtt * avgSec,
                        lastRating = rating,
                        isDue = wrong > 0 || rating == "HARD" || (totalAtt > 0 && totalAtt % 3 == 0),
                        sourceType = sourceType,
                        sourceId = sourceId
                    )
                )
            } else if (tokens.size >= 3) {
                // Simple bulk import format
                val type = if (tokens[0].contains("TF", ignoreCase = true) || tokens[0].contains("True", ignoreCase = true)) {
                    "TRUE_FALSE"
                } else {
                    "MULTIPLE_CHOICE"
                }
                val questionText = tokens[1]
                if (type == "TRUE_FALSE") {
                    val correct = if (tokens.getOrNull(2)?.contains("F", ignoreCase = true) == true) 1 else 0
                    questionsToInsert.add(
                        QuestionEntity(
                            linkedNoteId = linkedNoteId,
                            examId = "UPSI",
                            subjectName = subjectName,
                            chapterName = chapterName,
                            questionType = "TRUE_FALSE",
                            questionText = questionText,
                            optionA = "True",
                            optionB = "False",
                            correctAnswerIndex = correct,
                            isDue = true,
                            sourceType = sourceType,
                            sourceId = sourceId
                        )
                    )
                } else {
                    val optA = tokens.getOrNull(2) ?: ""
                    val optB = tokens.getOrNull(3) ?: ""
                    val optC = tokens.getOrNull(4) ?: ""
                    val optD = tokens.getOrNull(5) ?: ""
                    val ansStr = tokens.getOrNull(6) ?: "0"
                    val correctIdx = when (ansStr.uppercase()) {
                        "A", "1" -> 0
                        "B", "2" -> 1
                        "C", "3" -> 2
                        "D", "4" -> 3
                        else -> ansStr.toIntOrNull() ?: 0
                    }
                    questionsToInsert.add(
                        QuestionEntity(
                            linkedNoteId = linkedNoteId,
                            examId = "UPSI",
                            subjectName = subjectName,
                            chapterName = chapterName,
                            questionType = "MULTIPLE_CHOICE",
                            questionText = questionText,
                            optionA = optA,
                            optionB = optB,
                            optionC = optC,
                            optionD = optD,
                            correctAnswerIndex = correctIdx,
                            isDue = true,
                            sourceType = sourceType,
                            sourceId = sourceId
                        )
                    )
                }
            }
        }
        if (questionsToInsert.isNotEmpty()) {
            questionDao.insertQuestions(questionsToInsert)
        }
        return questionsToInsert.size
    }

    suspend fun clearStarterPack(clearSubject: String = "Indian Polity") {
        val starterNoteIds = InitialDataProvider.getInitialNotes().map { it.id }
        val starterQuestionIds = InitialDataProvider.getInitialQuestions().map { it.id }
        noteDao.deleteNotesBySubject(clearSubject)
        noteDao.deleteNotesByIds(starterNoteIds)
        questionDao.deleteQuestionsBySubject(clearSubject)
        questionDao.deleteQuestionsByIds(starterQuestionIds)
    }

    suspend fun restoreStarterPack() {
        noteDao.insertNotes(InitialDataProvider.getInitialNotes())
        questionDao.insertQuestions(InitialDataProvider.getInitialQuestions())
        studyLogDao.insertSessions(InitialDataProvider.getInitialSessions())
    }

    suspend fun clearAllData() {
        noteDao.deleteAllNotes()
        questionDao.deleteAllQuestions()
        studyLogDao.deleteAllSessions()
        questionAttemptDao.deleteAllAttempts()
    }
}
