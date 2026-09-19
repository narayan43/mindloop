package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectName = :subjectName AND chapterName = :chapterName")
    fun getQuestionsByChapter(subjectName: String, chapterName: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectName = :subjectName")
    fun getQuestionsBySubject(subjectName: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE isDue = 1")
    fun getDueQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE timesWrong > 0 ORDER BY timesWrong DESC, lastAttemptTimestamp DESC")
    fun getMistakeQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Long): QuestionEntity?

    @Query("SELECT * FROM questions WHERE linkedNoteId = :noteId")
    fun getQuestionsForNote(noteId: Long): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("UPDATE questions SET timesShown = timesShown + 1, totalAttempts = totalAttempts + 1, timesWrong = timesWrong + :wrongIncrement, totalTimeSpentSeconds = totalTimeSpentSeconds + :timeSpentSec, lastRating = :rating, lastAttemptTimestamp = :now, isDue = :newDue WHERE id = :questionId")
    suspend fun recordQuestionAttempt(
        questionId: Long,
        wrongIncrement: Int,
        rating: String,
        timeSpentSec: Long,
        newDue: Boolean,
        now: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM questions WHERE id IN (:ids)")
    suspend fun deleteQuestionsByIds(ids: List<Long>): Int

    @Query("DELETE FROM questions WHERE subjectName = :subjectName")
    suspend fun deleteQuestionsBySubject(subjectName: String): Int

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions(): Int
}
