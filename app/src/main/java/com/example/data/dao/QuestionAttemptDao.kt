package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.QuestionAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuestionAttemptEntity): Long

    @Query("UPDATE question_attempts SET self_rating = :rating WHERE attempt_id = :attemptId")
    suspend fun updateSelfRating(attemptId: String, rating: String)

    @Query("SELECT * FROM question_attempts WHERE attempt_id = :attemptId LIMIT 1")
    suspend fun getAttemptById(attemptId: String): QuestionAttemptEntity?

    @Query("SELECT * FROM question_attempts ORDER BY shown_at DESC")
    fun getAllAttempts(): Flow<List<QuestionAttemptEntity>>

    @Query("SELECT * FROM question_attempts WHERE question_id = :questionId ORDER BY shown_at DESC")
    fun getAttemptsForQuestion(questionId: Long): Flow<List<QuestionAttemptEntity>>

    @Query("SELECT * FROM question_attempts WHERE is_correct = 0 ORDER BY shown_at DESC")
    fun getWrongAttempts(): Flow<List<QuestionAttemptEntity>>
}
