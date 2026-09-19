package com.example.data.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.entity.NoteEntity
import com.example.data.entity.QuestionEntity
import com.example.data.entity.ReelEntity
import com.example.util.OfflineImageManager
import com.example.util.ReelVideoCacheManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

data class BackupNoteItem(
    val id: Long = 0L,
    val examId: String = "UPSI",
    val subjectName: String,
    val chapterName: String,
    val chapterNumber: Int = 1,
    val title: String,
    val summaryText: String,
    val imageUri: String? = null,
    val mediaFileRef: String? = null
)

data class BackupQuestionItem(
    val id: Long = 0L,
    val examId: String = "UPSI",
    val subjectName: String,
    val chapterName: String,
    val questionType: String = "MULTIPLE_CHOICE",
    val questionText: String,
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswerIndex: Int = 0,
    val sourceType: String = "note",
    val sourceId: String = ""
)

data class BackupReelItem(
    val id: Long = 0L,
    val exam: String = "UPSI",
    val subject: String,
    val chapter: String,
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val durationSeconds: Int = 30,
    val uploadedBy: String = "admin",
    val mediaFileRef: String? = null
)

data class BackupUserAttemptItem(
    val attemptId: String = "",
    val questionId: Long = 0L,
    val noteId: Long? = null,
    val subject: String = "",
    val chapter: String = "",
    val questionType: String = "MULTIPLE_CHOICE",
    val shownAt: Long = 0L,
    val answeredAt: Long = 0L,
    val timeTakenSeconds: Long = 0L,
    val selectedAnswer: String = "",
    val isCorrect: Boolean = false,
    val selfRating: String? = null
)

data class BackupStudySessionItem(
    val sessionId: String = "",
    val noteId: Long = 0L,
    val subject: String = "",
    val chapter: String = "",
    val startedAt: Long = 0L,
    val endedAt: Long = 0L,
    val durationSeconds: Long = 0L
)

data class MindLoopCourseBackup(
    val version: Int = 1,
    val appName: String = "MindLoop",
    val exportedAt: Long = System.currentTimeMillis(),
    val exportedDateFormatted: String = "",
    val sourceUser: String = "MindLoop Student",
    val exams: List<CurriculumExam> = emptyList(),
    val subjects: List<CurriculumSubject> = emptyList(),
    val chapters: List<CurriculumChapter> = emptyList(),
    val notes: List<BackupNoteItem> = emptyList(),
    val questions: List<BackupQuestionItem> = emptyList(),
    val reels: List<BackupReelItem> = emptyList(),
    val userAttempts: List<BackupUserAttemptItem> = emptyList(),
    val studySessions: List<BackupStudySessionItem> = emptyList()
) {
    val totalItemsCount: Int
        get() = notes.size + questions.size + reels.size + subjects.size + chapters.size + userAttempts.size + studySessions.size

    val hasUserData: Boolean
        get() = userAttempts.isNotEmpty() || studySessions.isNotEmpty()

    val mistakesCount: Int
        get() = userAttempts.count { !it.isCorrect }
}

data class ParsedPackageResult(
    val backup: MindLoopCourseBackup,
    val isMlpack: Boolean = false,
    val videosCount: Int = 0,
    val imagesCount: Int = 0,
    val totalMediaBytes: Long = 0L,
    val tempExtractDir: File? = null
)

object DataBackupManager {
    private const val TAG = "DataBackupManager"

    fun serializeBackup(backup: MindLoopCourseBackup): String {
        val root = JSONObject()
        root.put("version", backup.version)
        root.put("appName", backup.appName)
        root.put("exportedAt", backup.exportedAt)
        root.put("exportedDateFormatted", backup.exportedDateFormatted.ifBlank {
            SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(backup.exportedAt))
        })
        root.put("sourceUser", backup.sourceUser)

        // Exams
        val examsArr = JSONArray()
        for (exam in backup.exams) {
            val o = JSONObject()
            o.put("id", exam.id)
            o.put("name", exam.name)
            o.put("subtitle", exam.subtitle)
            o.put("createdBy", exam.createdBy)
            o.put("isEnrolled", exam.isEnrolled)
            examsArr.put(o)
        }
        root.put("exams", examsArr)

        // Subjects
        val subjectsArr = JSONArray()
        for (sub in backup.subjects) {
            val o = JSONObject()
            o.put("id", sub.id)
            o.put("examName", sub.examName ?: "")
            o.put("name", sub.name)
            o.put("subtitle", sub.subtitle)
            o.put("createdBy", sub.createdBy)
            o.put("isStandalone", sub.isStandalone)
            subjectsArr.put(o)
        }
        root.put("subjects", subjectsArr)

        // Chapters
        val chaptersArr = JSONArray()
        for (chap in backup.chapters) {
            val o = JSONObject()
            o.put("id", chap.id)
            o.put("examName", chap.examName ?: "")
            o.put("subjectName", chap.subjectName)
            o.put("name", chap.name)
            o.put("createdBy", chap.createdBy)
            chaptersArr.put(o)
        }
        root.put("chapters", chaptersArr)

        // Notes
        val notesArr = JSONArray()
        for (note in backup.notes) {
            val o = JSONObject()
            o.put("id", note.id)
            o.put("examId", note.examId)
            o.put("subjectName", note.subjectName)
            o.put("chapterName", note.chapterName)
            o.put("chapterNumber", note.chapterNumber)
            o.put("title", note.title)
            o.put("summaryText", note.summaryText)
            o.put("imageUri", note.imageUri ?: "")
            if (note.mediaFileRef != null) {
                o.put("mediaFileRef", note.mediaFileRef)
            }
            notesArr.put(o)
        }
        root.put("notes", notesArr)

        // Questions
        val questionsArr = JSONArray()
        for (q in backup.questions) {
            val o = JSONObject()
            o.put("id", q.id)
            o.put("examId", q.examId)
            o.put("subjectName", q.subjectName)
            o.put("chapterName", q.chapterName)
            o.put("questionType", q.questionType)
            o.put("questionText", q.questionText)
            o.put("optionA", q.optionA)
            o.put("optionB", q.optionB)
            o.put("optionC", q.optionC)
            o.put("optionD", q.optionD)
            o.put("correctAnswerIndex", q.correctAnswerIndex)
            o.put("sourceType", q.sourceType)
            o.put("sourceId", q.sourceId)
            questionsArr.put(o)
        }
        root.put("questions", questionsArr)

        // Reels
        val reelsArr = JSONArray()
        for (reel in backup.reels) {
            val o = JSONObject()
            o.put("id", reel.id)
            o.put("exam", reel.exam)
            o.put("subject", reel.subject)
            o.put("chapter", reel.chapter)
            o.put("title", reel.title)
            o.put("description", reel.description)
            o.put("videoUrl", reel.videoUrl)
            o.put("durationSeconds", reel.durationSeconds)
            o.put("uploadedBy", reel.uploadedBy)
            if (reel.mediaFileRef != null) {
                o.put("mediaFileRef", reel.mediaFileRef)
            }
            reelsArr.put(o)
        }
        root.put("reels", reelsArr)

        // User Generated Data: Question Attempts (History & Mistakes)
        if (backup.userAttempts.isNotEmpty()) {
            val attemptsArr = JSONArray()
            for (att in backup.userAttempts) {
                val o = JSONObject()
                o.put("attemptId", att.attemptId)
                o.put("questionId", att.questionId)
                if (att.noteId != null) o.put("noteId", att.noteId)
                o.put("subject", att.subject)
                o.put("chapter", att.chapter)
                o.put("questionType", att.questionType)
                o.put("shownAt", att.shownAt)
                o.put("answeredAt", att.answeredAt)
                o.put("timeTakenSeconds", att.timeTakenSeconds)
                o.put("selectedAnswer", att.selectedAnswer)
                o.put("isCorrect", att.isCorrect)
                if (att.selfRating != null) o.put("selfRating", att.selfRating)
                attemptsArr.put(o)
            }
            root.put("userAttempts", attemptsArr)
        }

        // User Generated Data: Study Sessions (Reading Logs & Timers)
        if (backup.studySessions.isNotEmpty()) {
            val sessionsArr = JSONArray()
            for (sess in backup.studySessions) {
                val o = JSONObject()
                o.put("sessionId", sess.sessionId)
                o.put("noteId", sess.noteId)
                o.put("subject", sess.subject)
                o.put("chapter", sess.chapter)
                o.put("startedAt", sess.startedAt)
                o.put("endedAt", sess.endedAt)
                o.put("durationSeconds", sess.durationSeconds)
                sessionsArr.put(o)
            }
            root.put("studySessions", sessionsArr)
        }

        return root.toString(2)
    }

    fun parseBackup(jsonString: String): Result<MindLoopCourseBackup> {
        return try {
            val root = JSONObject(jsonString)
            val version = root.optInt("version", 1)
            val appName = root.optString("appName", "MindLoop")
            val exportedAt = root.optLong("exportedAt", System.currentTimeMillis())
            val exportedDateFormatted = root.optString("exportedDateFormatted", "")
            val sourceUser = root.optString("sourceUser", "MindLoop Aspirant")

            val exams = mutableListOf<CurriculumExam>()
            val examsArr = root.optJSONArray("exams")
            if (examsArr != null) {
                for (i in 0 until examsArr.length()) {
                    val o = examsArr.getJSONObject(i)
                    exams.add(
                        CurriculumExam(
                            id = o.optString("id", "exam_$i"),
                            name = o.optString("name", "Exam"),
                            subtitle = o.optString("subtitle", ""),
                            createdBy = o.optString("createdBy", "imported"),
                            isEnrolled = o.optBoolean("isEnrolled", true)
                        )
                    )
                }
            }

            val subjects = mutableListOf<CurriculumSubject>()
            val subjectsArr = root.optJSONArray("subjects")
            if (subjectsArr != null) {
                for (i in 0 until subjectsArr.length()) {
                    val o = subjectsArr.getJSONObject(i)
                    val ex = o.optString("examName", "").ifBlank { null }
                    subjects.add(
                        CurriculumSubject(
                            id = o.optString("id", "subj_$i"),
                            examName = ex,
                            name = o.optString("name", "Subject"),
                            subtitle = o.optString("subtitle", ""),
                            createdBy = o.optString("createdBy", "imported"),
                            isStandalone = o.optBoolean("isStandalone", ex == null)
                        )
                    )
                }
            }

            val chapters = mutableListOf<CurriculumChapter>()
            val chaptersArr = root.optJSONArray("chapters")
            if (chaptersArr != null) {
                for (i in 0 until chaptersArr.length()) {
                    val o = chaptersArr.getJSONObject(i)
                    val ex = o.optString("examName", "").ifBlank { null }
                    chapters.add(
                        CurriculumChapter(
                            id = o.optString("id", "chap_$i"),
                            examName = ex,
                            subjectName = o.optString("subjectName", "General"),
                            name = o.optString("name", "Chapter"),
                            createdBy = o.optString("createdBy", "imported")
                        )
                    )
                }
            }

            val notes = mutableListOf<BackupNoteItem>()
            val notesArr = root.optJSONArray("notes")
            if (notesArr != null) {
                for (i in 0 until notesArr.length()) {
                    val o = notesArr.getJSONObject(i)
                    notes.add(
                        BackupNoteItem(
                            id = o.optLong("id", 0L),
                            examId = o.optString("examId", "UPSI"),
                            subjectName = o.optString("subjectName", "General"),
                            chapterName = o.optString("chapterName", "General Chapter"),
                            chapterNumber = o.optInt("chapterNumber", 1),
                            title = o.optString("title", "Untitled Note"),
                            summaryText = o.optString("summaryText", ""),
                            imageUri = o.optString("imageUri", "").ifBlank { null },
                            mediaFileRef = o.optString("mediaFileRef", "").ifBlank { null }
                        )
                    )
                }
            }

            val questions = mutableListOf<BackupQuestionItem>()
            val questionsArr = root.optJSONArray("questions")
            if (questionsArr != null) {
                for (i in 0 until questionsArr.length()) {
                    val o = questionsArr.getJSONObject(i)
                    questions.add(
                        BackupQuestionItem(
                            id = o.optLong("id", 0L),
                            examId = o.optString("examId", "UPSI"),
                            subjectName = o.optString("subjectName", "General"),
                            chapterName = o.optString("chapterName", "General Chapter"),
                            questionType = o.optString("questionType", "MULTIPLE_CHOICE"),
                            questionText = o.optString("questionText", ""),
                            optionA = o.optString("optionA", ""),
                            optionB = o.optString("optionB", ""),
                            optionC = o.optString("optionC", ""),
                            optionD = o.optString("optionD", ""),
                            correctAnswerIndex = o.optInt("correctAnswerIndex", 0),
                            sourceType = o.optString("sourceType", "note"),
                            sourceId = o.optString("sourceId", "")
                        )
                    )
                }
            }

            val reels = mutableListOf<BackupReelItem>()
            val reelsArr = root.optJSONArray("reels")
            if (reelsArr != null) {
                for (i in 0 until reelsArr.length()) {
                    val o = reelsArr.getJSONObject(i)
                    reels.add(
                        BackupReelItem(
                            id = o.optLong("id", 0L),
                            exam = o.optString("exam", "UPSI"),
                            subject = o.optString("subject", "General"),
                            chapter = o.optString("chapter", "General Chapter"),
                            title = o.optString("title", ""),
                            description = o.optString("description", ""),
                            videoUrl = o.optString("videoUrl", ""),
                            durationSeconds = o.optInt("durationSeconds", 30),
                            uploadedBy = o.optString("uploadedBy", "admin"),
                            mediaFileRef = o.optString("mediaFileRef", "").ifBlank { null }
                        )
                    )
                }
            }

            val userAttempts = mutableListOf<BackupUserAttemptItem>()
            val attemptsArr = root.optJSONArray("userAttempts")
            if (attemptsArr != null) {
                for (i in 0 until attemptsArr.length()) {
                    val o = attemptsArr.getJSONObject(i)
                    userAttempts.add(
                        BackupUserAttemptItem(
                            attemptId = o.optString("attemptId", "att_$i"),
                            questionId = o.optLong("questionId", 0L),
                            noteId = if (o.has("noteId")) o.optLong("noteId") else null,
                            subject = o.optString("subject", "General"),
                            chapter = o.optString("chapter", "General Chapter"),
                            questionType = o.optString("questionType", "MULTIPLE_CHOICE"),
                            shownAt = o.optLong("shownAt", 0L),
                            answeredAt = o.optLong("answeredAt", 0L),
                            timeTakenSeconds = o.optLong("timeTakenSeconds", 0L),
                            selectedAnswer = o.optString("selectedAnswer", ""),
                            isCorrect = o.optBoolean("isCorrect", false),
                            selfRating = if (o.has("selfRating")) o.optString("selfRating") else null
                        )
                    )
                }
            }

            val studySessions = mutableListOf<BackupStudySessionItem>()
            val sessionsArr = root.optJSONArray("studySessions")
            if (sessionsArr != null) {
                for (i in 0 until sessionsArr.length()) {
                    val o = sessionsArr.getJSONObject(i)
                    studySessions.add(
                        BackupStudySessionItem(
                            sessionId = o.optString("sessionId", "session_$i"),
                            noteId = o.optLong("noteId", 0L),
                            subject = o.optString("subject", "General"),
                            chapter = o.optString("chapter", "General Chapter"),
                            startedAt = o.optLong("startedAt", 0L),
                            endedAt = o.optLong("endedAt", 0L),
                            durationSeconds = o.optLong("durationSeconds", 0L)
                        )
                    )
                }
            }

            Result.success(
                MindLoopCourseBackup(
                    version = version,
                    appName = appName,
                    exportedAt = exportedAt,
                    exportedDateFormatted = exportedDateFormatted,
                    sourceUser = sourceUser,
                    exams = exams,
                    subjects = subjects,
                    chapters = chapters,
                    notes = notes,
                    questions = questions,
                    reels = reels,
                    userAttempts = userAttempts,
                    studySessions = studySessions
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse backup JSON: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Packages the course backup, including full offline videos (.mp4) and note images (.jpg/.png),
     * into a single compressed `.mlpack` (ZIP) container and launches the system share chooser.
     */
    fun exportAndShareMlpack(
        context: Context,
        backup: MindLoopCourseBackup,
        userShareTitle: String = "MindLoop Study Pack"
    ): Boolean {
        return try {
            val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val mlpackFile = File(cacheDir, "mindloop_course_pack_$timeStamp.mlpack")

            ZipOutputStream(BufferedOutputStream(FileOutputStream(mlpackFile))).use { zipOut ->
                // 1. Package reels videos
                val manifestReels = mutableListOf<BackupReelItem>()
                for (reel in backup.reels) {
                    val candidateVideoFile = ReelVideoCacheManager.getCachedVideoFile(context, reel.id)
                        ?: (if (reel.videoUrl.startsWith("/") && File(reel.videoUrl).exists()) File(reel.videoUrl) else null)
                        ?: ReelVideoCacheManager.ensureLocalReelFile(context, reel.id)

                    val hasValidVideo = candidateVideoFile.exists() && candidateVideoFile.length() > 0
                    val mediaRef = if (hasValidVideo) "media/videos/reel_${reel.id}.mp4" else null

                    manifestReels.add(reel.copy(mediaFileRef = mediaRef))

                    if (hasValidVideo && mediaRef != null) {
                        try {
                            zipOut.putNextEntry(ZipEntry(mediaRef))
                            candidateVideoFile.inputStream().use { input ->
                                input.copyTo(zipOut)
                            }
                            zipOut.closeEntry()
                            Log.d(TAG, "Added video to mlpack: $mediaRef (${candidateVideoFile.length()} bytes)")
                        } catch (ve: Exception) {
                            Log.w(TAG, "Failed packing video $mediaRef: ${ve.message}")
                        }
                    }
                }

                // 2. Package note images
                val manifestNotes = mutableListOf<BackupNoteItem>()
                for (note in backup.notes) {
                    val candidateImage = OfflineImageManager.getExistingLocalImageFile(context, note.id, note.imageUri)
                    val hasValidImage = candidateImage != null && candidateImage.exists() && candidateImage.length() > 0
                    val ext = candidateImage?.extension?.ifBlank { "jpg" } ?: "jpg"
                    val mediaRef = if (hasValidImage) "media/images/note_${note.id}.$ext" else null

                    manifestNotes.add(note.copy(mediaFileRef = mediaRef))

                    if (hasValidImage && mediaRef != null && candidateImage != null) {
                        try {
                            zipOut.putNextEntry(ZipEntry(mediaRef))
                            candidateImage.inputStream().use { input ->
                                input.copyTo(zipOut)
                            }
                            zipOut.closeEntry()
                            Log.d(TAG, "Added note image to mlpack: $mediaRef (${candidateImage.length()} bytes)")
                        } catch (ie: Exception) {
                            Log.w(TAG, "Failed packing image $mediaRef: ${ie.message}")
                        }
                    }
                }

                // 3. Serialize and add manifest.json
                val backupWithRefs = backup.copy(
                    reels = manifestReels,
                    notes = manifestNotes
                )
                val manifestJson = serializeBackup(backupWithRefs)
                zipOut.putNextEntry(ZipEntry("manifest.json"))
                zipOut.write(manifestJson.toByteArray(Charsets.UTF_8))
                zipOut.closeEntry()
            }

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                mlpackFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "$userShareTitle (MindLoop .mlpack Course Pack)")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "📦 MindLoop Complete Course Pack (.mlpack)\nIncludes: ${backup.notes.size} Notes, ${backup.questions.size} Questions, ${backup.reels.size} Video Reels (with full offline media).\nOpen with MindLoop to study completely offline!"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share MindLoop .mlpack Pack via...")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed exporting .mlpack: ${e.message}", e)
            // Fallback to JSON share
            exportAndShare(context, backup, userShareTitle)
        }
    }

    /**
     * Safely inspects and unpacks a backup file from a Uri (supporting both .mlpack ZIP archives and plain .json).
     * Any media extracted during preview is staged in a temporary cache directory.
     */
    fun unpackBackupFromUri(context: Context, uri: Uri): Result<ParsedPackageResult> {
        return try {
            // Check if input stream is a ZIP (.mlpack) by inspecting magic bytes PK\x03\x04
            val isZip = context.contentResolver.openInputStream(uri)?.use { stream ->
                val header = ByteArray(4)
                val readCount = stream.read(header)
                readCount == 4 && header[0] == 0x50.toByte() && header[1] == 0x4B.toByte()
            } ?: false

            if (isZip) {
                val tempDir = File(context.cacheDir, "temp_import_${System.currentTimeMillis()}").apply { mkdirs() }
                var manifestJson: String? = null
                var videoCount = 0
                var imageCount = 0
                var totalBytes = 0L

                context.contentResolver.openInputStream(uri)?.use { rawIn ->
                    ZipInputStream(BufferedInputStream(rawIn)).use { zipIn ->
                        var entry = zipIn.nextEntry
                        while (entry != null) {
                            val name = entry.name
                            if (!entry.isDirectory) {
                                if (name == "manifest.json" || name.endsWith("/manifest.json")) {
                                    manifestJson = zipIn.bufferedReader(Charsets.UTF_8).readText()
                                } else if (name.startsWith("media/videos/") || name.contains("/videos/")) {
                                    val fileName = File(name).name
                                    val outVideoDir = File(tempDir, "videos").apply { mkdirs() }
                                    val outFile = File(outVideoDir, fileName)
                                    FileOutputStream(outFile).use { out ->
                                        zipIn.copyTo(out)
                                    }
                                    videoCount++
                                    totalBytes += outFile.length()
                                } else if (name.startsWith("media/images/") || name.contains("/images/")) {
                                    val fileName = File(name).name
                                    val outImageDir = File(tempDir, "images").apply { mkdirs() }
                                    val outFile = File(outImageDir, fileName)
                                    FileOutputStream(outFile).use { out ->
                                        zipIn.copyTo(out)
                                    }
                                    imageCount++
                                    totalBytes += outFile.length()
                                }
                            }
                            zipIn.closeEntry()
                            entry = zipIn.nextEntry
                        }
                    }
                }

                if (manifestJson == null) {
                    tempDir.deleteRecursively()
                    return Result.failure(IllegalArgumentException("Invalid .mlpack file: manifest.json was not found inside the package."))
                }

                val parseResult = parseBackup(manifestJson!!)
                parseResult.fold(
                    onSuccess = { backup ->
                        Result.success(
                            ParsedPackageResult(
                                backup = backup,
                                isMlpack = true,
                                videosCount = videoCount,
                                imagesCount = imageCount,
                                totalMediaBytes = totalBytes,
                                tempExtractDir = tempDir
                            )
                        )
                    },
                    onFailure = { err ->
                        tempDir.deleteRecursively()
                        Result.failure(err)
                    }
                )
            } else {
                // Plain JSON backup
                val content = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                } ?: ""
                val parseResult = parseBackup(content)
                parseResult.map { backup ->
                    ParsedPackageResult(
                        backup = backup,
                        isMlpack = false
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed unpacking backup uri: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Commits all unpacked media into the application's private internal storage:
     * - Videos are moved to `context.filesDir/private_reels_cache/`
     * - Note images are moved to `context.filesDir/private_notes_vault/`
     *
     * Guarantees:
     * Both directories include a `.nomedia` file and reside strictly within internal storage (`filesDir`).
     * As a result, media scanner, device Gallery, and Google Photos will NEVER display these files.
     * They remain 100% restricted to the MindLoop app.
     */
    fun commitImportedMedia(context: Context, packageResult: ParsedPackageResult): MindLoopCourseBackup {
        val tempDir = packageResult.tempExtractDir ?: return packageResult.backup
        return try {
            val reelsCacheDir = ReelVideoCacheManager.getCacheDir(context) // context.filesDir/private_reels_cache
            val notesVaultDir = OfflineImageManager.getVaultDirectory(context) // context.filesDir/private_notes_vault

            // Guarantee .nomedia exists in both destination folders
            File(reelsCacheDir, ".nomedia").let { if (!it.exists()) it.createNewFile() }
            File(notesVaultDir, ".nomedia").let { if (!it.exists()) it.createNewFile() }

            val tempVideos = File(tempDir, "videos").listFiles() ?: emptyArray()
            val videoFileMap = mutableMapOf<String, File>()
            for (vid in tempVideos) {
                val destFile = File(reelsCacheDir, vid.name)
                vid.copyTo(destFile, overwrite = true)
                videoFileMap[vid.name] = destFile
            }

            val tempImages = File(tempDir, "images").listFiles() ?: emptyArray()
            val imageFileMap = mutableMapOf<String, File>()
            for (img in tempImages) {
                val destFile = File(notesVaultDir, img.name)
                img.copyTo(destFile, overwrite = true)
                imageFileMap[img.name] = destFile
            }

            // Map updated local paths into the backup data model
            val updatedReels = packageResult.backup.reels.map { reel ->
                val refName = reel.mediaFileRef?.let { File(it).name }
                val targetFile = (refName?.let { videoFileMap[it] })
                    ?: videoFileMap["reel_${reel.id}.mp4"]
                if (targetFile != null && targetFile.exists()) {
                    reel.copy(videoUrl = targetFile.absolutePath)
                } else {
                    reel
                }
            }

            val updatedNotes = packageResult.backup.notes.map { note ->
                val refName = note.mediaFileRef?.let { File(it).name }
                val targetFile = (refName?.let { imageFileMap[it] })
                    ?: imageFileMap.entries.find { it.key.startsWith("note_${note.id}.") }?.value
                if (targetFile != null && targetFile.exists()) {
                    note.copy(imageUri = targetFile.absolutePath)
                } else {
                    note
                }
            }

            // Clean up temporary cache
            tempDir.deleteRecursively()

            packageResult.backup.copy(
                reels = updatedReels,
                notes = updatedNotes
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed committing imported media: ${e.message}", e)
            try { tempDir.deleteRecursively() } catch (_: Exception) {}
            packageResult.backup
        }
    }

    fun exportAndShare(
        context: Context,
        backup: MindLoopCourseBackup,
        userShareTitle: String = "MindLoop Study Pack"
    ): Boolean {
        return try {
            val jsonString = serializeBackup(backup)
            val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(cacheDir, "mindloop_course_pack_$timeStamp.json")
            file.writeText(jsonString)

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "$userShareTitle (MindLoop Course Backup)")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "📚 MindLoop Course Data Pack\nIncludes: ${backup.notes.size} Notes, ${backup.questions.size} Questions, ${backup.reels.size} Reels, across ${backup.subjects.size} Subjects.\nOpen this in MindLoop using 'Import Data'!"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share MindLoop Study Data via...")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share backup file: ${e.message}", e)
            // Fallback to text share
            try {
                val jsonString = serializeBackup(backup)
                val textIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, jsonString)
                    putExtra(Intent.EXTRA_SUBJECT, "$userShareTitle (MindLoop JSON)")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(textIntent, "Share MindLoop Data (Text)"))
                true
            } catch (e2: Exception) {
                Log.e(TAG, "Fallback share also failed: ${e2.message}")
                false
            }
        }
    }
}
